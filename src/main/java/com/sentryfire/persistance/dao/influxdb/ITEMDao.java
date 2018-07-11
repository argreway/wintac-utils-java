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

import com.sentryfire.model.Item;
import com.sentryfire.persistance.DAOFactory;
import org.influxdb.dto.QueryResult;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class ITEMDao
 {

    Logger log = LoggerFactory.getLogger(getClass());

    protected static final String ALL_ITEM = "SELECT * FROM ITEM";

    public List<Item> getAllItemRecords()
    {
       try
       {
          QueryResult queryResult = DAOFactory.getInfluxClient().query(ALL_ITEM);
          List<Item> results = DAOFactory.getResultMapper().toPOJO(queryResult, Item.class);
          return results;
       }
       catch (Exception e)
       {
          e.printStackTrace();
          log.error("Failed to query influxdb for ITEM." + e);
       }
       return null;
    }

    public List<Item> getItemRecordsByTime(DateTime start,
                                           DateTime end)
    {
       try
       {
          String queryString = ALL_ITEM + " WHERE time >= " + start.getMillis() + "ms AND time <= " + end.getMillis() + "ms";

          QueryResult queryResult = DAOFactory.getInfluxClient().query(queryString);

          List<Item> results = DAOFactory.getResultMapper().toPOJO(queryResult, Item.class);
          return results;
       }
       catch (Exception e)
       {
          e.printStackTrace();
          log.error("Failed to query influxdb for ITEM." + e);
       }
       return null;
    }

    public List<Item> getWOItemRecordsByTime(DateTime start,
                                             DateTime end)
    {
       try
       {
          String queryString = ALL_ITEM + " WHERE time >= " + start.getMillis() + "ms AND time <= " + end.getMillis() + "ms";

          QueryResult queryResult = DAOFactory.getInfluxClient().query(queryString);

          List<Item> results = DAOFactory.getResultMapper().toPOJO(queryResult, Item.class);
          return results;
       }
       catch (Exception e)
       {
          e.printStackTrace();
          log.error("Failed to query influxdb for ITEM." + e);
       }
       return null;
    }

    public List<Item> getHistoryWOItemRecordsByIn2(DateTime start,
                                            DateTime end,
                                            String in2)
    {
       try
       {
          String queryString = ALL_ITEM + " WHERE time >= " + start.getMillis() + "ms " +
                               "AND time <= " + end.getMillis() + "ms " +
                               "AND IN2 = '" + in2 + "'";

          QueryResult queryResult = DAOFactory.getHistoryInfluxClient().query(queryString);

          return DAOFactory.getResultMapper().toPOJO(queryResult, Item.class);
       }
       catch (Exception e)
       {
          e.printStackTrace();
          log.error("Failed to query influxdb for ITEM." + e);
       }
       return null;
    }

 }
