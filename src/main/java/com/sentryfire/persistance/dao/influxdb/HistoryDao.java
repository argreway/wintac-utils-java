 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      PAYDao.java
  * Created:   6/20/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.persistance.dao.influxdb;

 import java.util.List;
 import java.util.Map;
 import java.util.Objects;
 import java.util.stream.Collectors;

 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.sentryfire.model.LaborHistory;
 import com.sentryfire.model.WOHistory;
 import com.sentryfire.persistance.DAOFactory;
 import org.influxdb.dto.Point;
 import org.influxdb.dto.QueryResult;
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeZone;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class HistoryDao
 {

    Logger log = LoggerFactory.getLogger(getClass());

    public static final String ALL_WO_HISTORY = "select * from " + InfluxClient.MEASUREMENT_HISTORY.WO_HISTORY;

    public void writeLaborHistoryRecords(List<LaborHistory> laborHistories)
    {
       try
       {
          DAOFactory.getHistoryInfluxClient().write(getHistoryPoints(laborHistories));
       }
       catch (Exception e)
       {
          e.printStackTrace();
          log.error("Failed to write history items to influxdb." + e);
       }
    }

    public void writeWOHistory(Map<String, Map<String, List<String>>> workOrderList)
    {
       try
       {
          DateTime ts = new DateTime(DateTimeZone.UTC);
          List<Point> points = Lists.newArrayList();

          for (String year : workOrderList.keySet())
          {

             Map<String, List<String>> monthCount = workOrderList.get(year);

             for (Map.Entry<String, List<String>> entry : monthCount.entrySet())
             {
                // Create the points
                Map<String, Object> fields = Maps.newHashMap();
                fields.put("count", entry.getValue().size());
                fields.put("jobs", entry.getValue().stream().filter(Objects::nonNull).sorted().collect(Collectors.joining(",")));

                Map<String, String> tags = Maps.newHashMap();
                tags.put("year", year);
                tags.put("month", entry.getKey());

                points.add(InfluxClient.createPoint(ts.getMillis(), InfluxClient.MEASUREMENT_HISTORY.WO_HISTORY.toString(), tags, fields));
             }
          }

          // Submit the points
          DAOFactory.getHistoryInfluxClient().write(points);
       }
       catch (Exception e)
       {
          e.printStackTrace();
          log.error("Failed to write history items to influxdb." + e);
       }
    }

    public List<WOHistory> getWOHistory(DateTime start,
                                        DateTime end)
    {
       try
       {
          String queryString = ALL_WO_HISTORY + " WHERE time >= " + start.getMillis() + "ms AND time <= " + end.getMillis() + "ms";
          QueryResult queryResult = DAOFactory.getInfluxClient().query(queryString);

          List<WOHistory> results = DAOFactory.getResultMapper().toPOJO(queryResult, WOHistory.class);
          return results;
       }
       catch (Exception e)
       {
          e.printStackTrace();
          log.error("Failed to get wo history from influxdb." + e);
       }
       return null;
    }

    protected List<Point> getHistoryPoints(List<LaborHistory> histories)
    {

       List<Point> points = Lists.newArrayList();
       for (LaborHistory history : histories)
       {
          Map<String, Object> fields = Maps.newHashMap();
          fields.put("fixedLabor", history.getFixedLabor());
          fields.put("directLabor", history.getDirectLabor());
          fields.put("totalLabor", history.getTotalLabor());
          fields.put("totalCost", history.getTotalCost());
          fields.put("totalRevenue", history.getTotalRevenue());
          fields.put("grossProfit", history.getGrossProfit());
          fields.put("netProfit", history.getNetProfit());
          fields.put("contribMargin", history.getContribMargin());

          Map<String, String> tags = Maps.newHashMap();

          DateTime timeStamp = new DateTime(history.getTime());

          points.add(InfluxClient.createPoint(timeStamp.getMillis(), InfluxClient.MEASUREMENT_HISTORY.EFF_RATIO.toString(), tags, fields));
       }

       return points;
    }

 }
