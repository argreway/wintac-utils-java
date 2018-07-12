 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WIPDao.java
  * Created:   6/21/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.persistance.dao.influxdb;

 import java.util.List;

 import com.sentryfire.business.history.workorder.WOHistoryManager;
 import com.sentryfire.persistance.DAOFactory;
 import com.sentryfire.model.WO;
 import org.influxdb.dto.QueryResult;
 import org.joda.time.DateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class WIPDao
 {

    Logger log = LoggerFactory.getLogger(getClass());

    protected static final String ALL_WO = "SELECT * FROM WO";
    protected static final String ALL_INV = "SELECT * FROM INV";
    protected WOHistoryManager woHistoryManager = new WOHistoryManager();

    public List<WO> getAllWorkOrders()
    {
       try
       {
          QueryResult queryResult = DAOFactory.getInfluxClient().query(ALL_WO);
          List<WO> results = DAOFactory.getResultMapper().toPOJO(queryResult, WO.class);
          return results;
       }
       catch (Exception e)
       {
          log.error("Failed to query influxdb for WO." + e);
       }
       return null;
    }

    public List<WO> getWorkOrdersByTime(DateTime start,
                                        DateTime end)
    {
       try
       {
          String queryString = ALL_WO + " WHERE time >= " + start.getMillis() + "ms AND time <= " + end.getMillis() + "ms";
          QueryResult queryResult = DAOFactory.getInfluxClient().query(queryString);

          List<WO> results = DAOFactory.getResultMapper().toPOJO(queryResult, WO.class);
          return results;
       }
       catch (Exception e)
       {
          e.printStackTrace();
          log.error("Failed to query influxdb for WO." + e);
       }
       return null;
    }

    public List<WO> getHistoryWorkOrdersByTime(DateTime start,
                                               DateTime end)
    {
       try
       {
          String queryString = ALL_WO + " WHERE time >= " + start.getMillis() + "ms AND time <= " + end.getMillis() + "ms";
          QueryResult queryResult = DAOFactory.getHistoryInfluxClient().query(queryString);

          List<WO> results = DAOFactory.getResultMapper().toPOJO(queryResult, WO.class);
          return results;
       }
       catch (Exception e)
       {
          log.error("Failed to query influxdb for WO." + e);
       }
       return null;
    }

    public List<WO> getHistoryWOAndItems(DateTime start,
                                         DateTime end)
    {
       try
       {
          return woHistoryManager.getWorkOrdersWithItems(start, end);
       }
       catch (Exception e)
       {
          log.error("Failed to get work orders with items inserted." + e);
       }
       return null;
    }

 }
