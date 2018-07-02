 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      InfluxClient.java
  * Created:   5/24/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.persistance.dao.influxdb;

 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.concurrent.TimeUnit;

 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import okhttp3.OkHttpClient;
 import org.influxdb.BatchOptions;
 import org.influxdb.InfluxDB;
 import org.influxdb.InfluxDBFactory;
 import org.influxdb.dto.BatchPoints;
 import org.influxdb.dto.Point;
 import org.influxdb.dto.Query;
 import org.influxdb.dto.QueryResult;
 import org.joda.time.DateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class InfluxClient
 {
    protected static Logger log = LoggerFactory.getLogger(InfluxClient.class);

    private static String dbNameRealTime = "sentry_stats_time";
    private static String dbNameHistory = "sentry_history";

    private InfluxDB influxDB;
    private String dbName = dbNameRealTime;

    private static final Long TIMEOUT_SEC = 120L;

    static List<DateTime> stampList = Lists.newArrayList();

    public InfluxClient(boolean history)
    {
       OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(TIMEOUT_SEC, TimeUnit.SECONDS).
          readTimeout(TIMEOUT_SEC, TimeUnit.SECONDS).writeTimeout(TIMEOUT_SEC, TimeUnit.SECONDS);

       influxDB = InfluxDBFactory.connect("http://192.168.1.30:8086", "sentry", "sentry", builder);

       if (history)
          dbName = dbNameHistory;

       influxDB.setDatabase(dbName);
       influxDB.enableBatch(BatchOptions.DEFAULTS);
    }

    public enum MEASUREMENT
    {
       WO,
       INV,
       PO,
       PROP,
       PAY,
       ITEM,
       AR
    }

    public enum COL
    {
       TECH,
       JDATE,
       INVDATE,
       IDATE,
       CHKDATE
    }

    public static List<String> WIP_FIELDS = Lists.newArrayList("SUBTOTAL", "TOTTAX", "TOTTAX2", "MAT", "LAB", "MATC", "LABC");
    public static List<String> ITEM_FIELDS = Lists.newArrayList("COST", "RP", "TOTCOST", "POCOST", "COST", "HQ", "HQ2");
    public static List<String> ITEM_TAGS = Lists.newArrayList("MISC1", "DEPT", "DIVISION", "IC");
    //public static List<String> ITEM_FIELDS = Lists.newArrayList (
    //    "COUNTER", "CN","[IN]","PAGENUM","POCOST","IC","NAME","HQ","HQ2","RP","COST","IDATE","CSDATE",
    //        "DEPT","ACC1","ACC2","INOTE","MISC1","TOTCOST" );
    public static List<String> AR_FIELDS = Lists.newArrayList("SUBTOTAL", "TOTTAX", "TOTTAX2", "MAT", "LAB", "MATC", "LABC",
                                                              "BALANCE", "RECEIPTS_PAID_AMOUNT");
    public static List<String> PAY_FIELDS = Lists.newArrayList("NHOURS ", "OHOURS ", "SHOURS ", "VHOURS ", "HOURS5 ", "HOURS6 ",
                                                               "HOURS7 ", "HOURS8 ", "HOURS9 ", "HOURS10 ", "GROSS", "TIPS ", "FED_WH ", "ST_WH ", "LOC_WH ", "SOC ", "MED ", "BEN ",
                                                               "ATSAV ", "BTSAV ", "REIMB ", "MISC1 ", "DED5 ", "DED6 ", "DED7 ", "DED8 ", "DED9 ", "DED10 ", "SDI ", "FLI ", "SUI");


    public void createDataBase()
    {
       log.info("Creating influxdb [" + dbName + "]");
       influxDB.createDatabase(dbName);
    }

    public void dropDataBase()
    {
       log.info("Dropping influxdb [" + dbName + "]");
       influxDB.deleteDatabase(dbName);
    }

    public void write(List<Point> pointList)
    {
       BatchPoints batchPoints = BatchPoints
          .database(dbName)
          .build();

       pointList.stream().forEach(p -> batchPoints.point(p));
       influxDB.write(batchPoints);
    }

    public void write(String measurement,
                      Map<String, String> tags,
                      Map<String, Object> fields)
    {
       write(System.currentTimeMillis(), measurement, tags, fields);
    }

    public void write(long timeStamp,
                      String measurement,
                      Map<String, String> tags,
                      Map<String, Object> fields)
    {
       influxDB.write(createPoint(timeStamp, measurement, tags, fields));
    }

    public static void clearStampList()
    {
       stampList.clear();
    }

    public static Point createPoint(long timeStamp,
                                    String measurement,
                                    Map<String, String> tags,
                                    Map<String, Object> fields)
    {
       Point.Builder builder = Point.measurement(measurement)
          .time(timeStamp, TimeUnit.MILLISECONDS);

       if (tags != null)
          builder.tag(tags);

       if (fields != null)
          builder.fields(fields);

       return builder.build();
    }

    static int count = 0;
    static Random random = new Random();

    public static Point buildPoint(String measurement,
                                   Map<String, Object> fields,
                                   Map<String, String> tags,
                                   DateTime timestamp)
    {
       Point.Builder builder = Point.measurement(measurement);
       if (timestamp != null)
       {
          int milliRotate = random.nextInt(999);
          int minRotate = random.nextInt(59);
          int secRotate = random.nextInt(59);


          // Randomize the
          DateTime iTimeStamp = new DateTime(timestamp.getYear(), timestamp.getMonthOfYear(),
                                             timestamp.getDayOfMonth(), timestamp.getHourOfDay(), minRotate, secRotate, milliRotate);
          builder.time(iTimeStamp.getMillis(), TimeUnit.MILLISECONDS);

          if (stampList.contains(iTimeStamp))
          {
             log.error("Duplicate time stamp detected: " + ++count + " time: " + iTimeStamp);
          }
          stampList.add(iTimeStamp);
       }
       if (fields != null)
       {
          for (Map.Entry<String, Object> item : fields.entrySet())
          {
             if (item.getValue() instanceof Integer)
                builder.addField(item.getKey(), (int) item.getValue());
             else if (item.getValue() instanceof Float)
                builder.addField(item.getKey(), (float) item.getValue());
             else if (item.getValue() instanceof String)
                builder.addField(item.getKey(), (String) item.getValue());
          }
       }
       if (tags != null)
       {
          for (Map.Entry<String, String> item : tags.entrySet())
          {
             String itemValue = item.getValue();
             if (itemValue == null || itemValue.trim().isEmpty())
             {
                itemValue = "<empty>";
             }
             else if (itemValue.contains(System.lineSeparator()))
             {
                itemValue = itemValue.replace(System.lineSeparator(), "");
             }

             builder.tag(item.getKey(), itemValue);
          }
       }
       return builder.build();
    }


    public QueryResult query(String queryString)
    {
       Query query = new Query(queryString, dbName);
       log.info("BEGIN query.");
       QueryResult result = influxDB.query(query);
       log.info("END query.");
       return result;

    }

    public void test()
    {
       Map<String, Object> fields = Maps.newHashMap();
       fields.put("used", 80L);
       fields.put("free", 1L);

       Map<String, String> tags = Maps.newHashMap();
       tags.put("test", "1");
       write("cpu", tags, fields);

//       String ALL_AR = "SELECT * FROM INV where time >= now() - 1d";
       String queryString = "SELECT * FROM AR limit 1";
       QueryResult result = query(queryString);

       log.info("Result: " + result);
       influxDB.close();
    }

    public void shutdown()
    {
       influxDB.close();
    }
 }
