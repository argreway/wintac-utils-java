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
 import java.util.Vector;
 import java.util.concurrent.TimeUnit;

 import javax.swing.table.DefaultTableModel;

 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.sentryfire.model.Item;
 import okhttp3.OkHttpClient;
 import org.influxdb.BatchOptions;
 import org.influxdb.InfluxDB;
 import org.influxdb.InfluxDBFactory;
 import org.influxdb.dto.BatchPoints;
 import org.influxdb.dto.Point;
 import org.influxdb.dto.Query;
 import org.influxdb.dto.QueryResult;
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeZone;
 import org.joda.time.format.DateTimeFormat;
 import org.joda.time.format.DateTimeFormatter;
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
    private static int BATCH_SIZE = 10000;

    protected static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S");
    protected static DateTimeFormatter msFormatter = DateTimeFormat.forPattern("M/d/yyyy HH:mm:ss a");


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

    public enum MEASUREMENT_HISTORY
    {
       WO,
       ITEM,
       WO_HISTORY,
       INV_HISTORY,
       EFF_RATIO
    }

    public enum COL
    {
       JDATE,
       INVDATE,
       IDATE,
       CHKDATE
    }

    public static List<String> WIP_FIELDS = Lists.newArrayList("SUBTOTAL", "TOTTAX", "TOTTAX2", "MAT", "LAB", "MATC", "LABC");
    public static List<String> AR_FIELDS = Lists.newArrayList("SUBTOTAL", "TOTTAX", "TOTTAX2", "MAT", "LAB", "MATC", "LABC",
                                                              "BALANCE", "RECEIPTS_PAID_AMOUNT");
    public static List<String> PAY_FIELDS = Lists.newArrayList("NHOURS ", "OHOURS ", "SHOURS ", "VHOURS ", "HOURS5 ", "HOURS6 ",
                                                               "HOURS7 ", "HOURS8 ", "HOURS9 ", "HOURS10 ", "GROSS", "TIPS ", "FED_WH ", "ST_WH ", "LOC_WH ", "SOC ", "MED ", "BEN ",
                                                               "ATSAV ", "BTSAV ", "REIMB ", "MISC1 ", "DED5 ", "DED6 ", "DED7 ", "DED8 ", "DED9 ", "DED10 ", "SDI ", "FLI ", "SUI");
    public static List<String> ITEM_FIELDS = Lists.newArrayList("COST", "RP", "TOTCOST", "POCOST", "COST", "HQ", "HQ2");

    public static List<String> ITEM_TAGS = Lists.newArrayList("MISC1", "DEPT", "DIVISION", "IC", "TYPE");
    // We want the IN2 index for history but too big of perf hit for all reg WOs
    public static List<String> HISTORY_ITEM_TAGS = Lists.newArrayList("MISC1", "DEPT", "DIVISION", "IC", "IN2", "TYPE");

    //////////
    // Helpers
    //////////

    static int count = 0;

    static Random randomHr = new Random();
    static Random randomMin = new Random();
    static Random randomSec = new Random();
    static Random randomMilli = new Random();

    public static Point buildPoint(String measurement,
                                   Map<String, Object> fields,
                                   Map<String, String> tags,
                                   DateTime timestamp)
    {
       Point.Builder builder = Point.measurement(measurement);
       if (timestamp != null)
       {
          int milliRotate = randomMilli.nextInt(999);
          int minRotate = randomMin.nextInt(59);
          int secRotate = randomSec.nextInt(59);
          int hrRotate = randomHr.nextInt(12) + 11;

          // Randomize the
          DateTime iTimeStamp = new DateTime(timestamp.getYear(), timestamp.getMonthOfYear(),
                                             timestamp.getDayOfMonth(), hrRotate, minRotate, secRotate, milliRotate);
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

    public static void clearStampList()
    {
       stampList.clear();
    }

    public void insertInfluxStatsForDataTable(DefaultTableModel dataTable,
                                              String dateColumn,
                                              List<String> fieldColumns,
                                              String measurement)
    {
       insertInfluxStatsForDataTable(dataTable, dateColumn, fieldColumns, null, measurement);
    }

    public List<Item> convertToItemsList(DefaultTableModel model)
    {
       List<Item> items = Lists.newArrayList();
       if (model != null)
       {

       }

       return items;
    }

    public void insertInfluxStatsForDataTable(DefaultTableModel dataTable,
                                              String dateColumn,
                                              List<String> fieldColumns,
                                              List<String> tagColumns,
                                              String measurement)
    {
       List<Point> measurements = Lists.newArrayList();
       DateTime timestamp = new DateTime(DateTimeZone.UTC);

       // Create Measurement per row
       for (Object rowObj : dataTable.getDataVector())
       {
          Vector<Object> row = (Vector<Object>) rowObj;

          Map<String, Object> fields = Maps.newHashMap();
          Map<String, String> tags = Maps.newHashMap();

          int nCols = dataTable.getColumnCount();
          int dateColumnIdx = -1;
          for (int i = 0; i < nCols; i++)
          {
             String columnName = dataTable.getColumnName(i);


             Object value = row.get(i);
             if (value == null)
                continue;

             String rowVal = row.get(i).toString();

             if (columnName.equals(dateColumn))
             {
                dateColumnIdx = i;
                if (rowVal != null && !rowVal.trim().isEmpty())
                {
                   DateTime convDate = formatter.parseDateTime(rowVal);
                   rowVal = msFormatter.print(convDate);
                }
             }


             if (isField(fieldColumns, columnName))
             {
                fields.put(columnName, Float.parseFloat(rowVal));
             }
             else if (isField(tagColumns, columnName))
             {
                String tValue = rowVal.trim().replace(" ", "_");
                if (tValue.endsWith(".0"))
                   tValue = tValue.substring(0, tValue.indexOf("."));
                tags.put(columnName, tValue);
             }
             else if (tagColumns != null)
             {
                //   Everything else should be treated as a string value
                String tValue = rowVal.trim().replace(" ", "_");
                if (tValue.endsWith(".0"))
                   tValue = tValue.substring(0, tValue.indexOf("."));
                fields.put(columnName, tValue);
             }
             else
             {
                String tValue = rowVal.trim().replace(" ", "_");
                if (tValue.endsWith(".0"))
                   tValue = tValue.substring(0, tValue.indexOf("."));
                tags.put(columnName, tValue);
             }
          }

          if (dateColumnIdx > -1)
          {
             String convDate = row.get(dateColumnIdx).toString();
             if (convDate != null && !convDate.trim().isEmpty())
                timestamp = formatter.parseDateTime(convDate);
          }
          else
          {
             log.error("No date column to generate timestamp! [" + dateColumn + "] ");
          }

          measurements.add(InfluxClient.buildPoint(measurement, fields, tags, timestamp));

          if (measurements.size() == BATCH_SIZE)
          {
             try
             {
                log.info("Bulk write " + measurements.size());
                write(measurements);
                measurements.clear();
                Thread.sleep(100);
             }
             catch (Exception e)
             {
                log.error("Failed to send bulk points due to : ", e);
             }
          }
       }

       // Bulk write out the stats
       write(measurements);
       clearStampList();
       log.info("Final bulk write " + measurements.size());
    }

    protected static boolean isField(List<String> fieldList,
                                     String columnName)
    {
       if (fieldList == null)
          return false;
       for (String fieldName : fieldList)
       {
          if (columnName.equals(fieldName))
             return true;
       }
       return false;
    }


    //////////
    // DB Functions
    //////////

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


    public QueryResult query(String queryString)
    {
       Query query = new Query(queryString, dbName);
       log.info("BEGIN query [" + queryString + "]");
       QueryResult result = influxDB.query(query);
       log.info("END query.");
       return result;

    }

    public void shutdown()
    {
       influxDB.close();
    }
 }
