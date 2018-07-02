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

 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.sentryfire.persistance.DAOFactory;
 import com.sentryfire.model.LaborHistory;
 import org.influxdb.dto.Point;
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeZone;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class HistoryDao
 {

    Logger log = LoggerFactory.getLogger(getClass());
    public static final String MEASUREMENT_ER = "eff_ratio";
    public static final String MEASUREMENT_WO = "wo_history";

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

    public void writeWOHistory(Map<String, Map<String, Integer>> workOrderList)
    {
       try
       {
          DateTime ts = new DateTime(DateTimeZone.UTC);
          List<Point> points = Lists.newArrayList();

          for (String year : workOrderList.keySet())
          {

             Map<String, Integer> monthCount = workOrderList.get(year);

             for (Map.Entry<String, Integer> entry : monthCount.entrySet())
             {
                // Create the points
                Map<String, Object> fields = Maps.newHashMap();
                fields.put("count", entry.getValue());

                Map<String, String> tags = Maps.newHashMap();
                tags.put("year", year);
                tags.put("month", entry.getKey());

                points.add(InfluxClient.createPoint(ts.getMillis(), MEASUREMENT_WO, tags, fields));
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

          points.add(InfluxClient.createPoint(timeStamp.getMillis(), MEASUREMENT_ER, tags, fields));
       }

       return points;
    }

 }
