 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      ARDao.java
  * Created:   6/1/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.persistance.dao.influxdb;

 import java.util.List;

 import com.google.common.collect.Lists;
 import com.sentryfire.config.ExternalConfiguartion;
 import com.sentryfire.persistance.DAOFactory;
 import com.sentryfire.model.AccountRecievable;
 import org.influxdb.dto.QueryResult;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class ARDao
 {
    Logger log = LoggerFactory.getLogger(getClass());

    protected static final String AR_ALL = "SELECT * FROM AR";
    protected static final String AR_COLLECT = "SELECT * FROM AR WHERE BALANCE > 0 AND time <= now() - 730d";

    public List<AccountRecievable> getAllARRecords()
    {
       try
       {
          QueryResult queryResult = DAOFactory.getInfluxClient().query(AR_ALL);
          List<AccountRecievable> results = DAOFactory.getResultMapper().toPOJO(queryResult, AccountRecievable.class);
          return results;
       }
       catch (Exception e)
       {
          e.printStackTrace();
          System.err.println("Failed to query influxdb for AR." + e);
       }
       return null;
    }

    public List<AccountRecievable> getARRecordsOlderThan2Years()
    {
       try
       {
          QueryResult queryResult = DAOFactory.getInfluxClient().query(AR_COLLECT);
          List<AccountRecievable> results = DAOFactory.getResultMapper().toPOJO(queryResult, AccountRecievable.class);
          return results;
       }
       catch (Exception e)
       {
          e.printStackTrace();
          System.err.println("Failed to query influxdb for AR." + e);
       }
       return null;
    }

    public List<AccountRecievable> getFilteredARRecordsOlderThan2Years()
    {
       List<AccountRecievable> fullList = getARRecordsOlderThan2Years();

       log.info("Exclusion List: " + ExternalConfiguartion.getInstance().getExclusionList());
       List<AccountRecievable> result = Lists.newArrayList();

       for (AccountRecievable ar : fullList)
       {

          for (String exclusion : ExternalConfiguartion.getInstance().getExclusionList())
          {
             if (exclusion.equals(ar.getCN()))
                log.info("Filtering: " + ar);
             else
                result.add(ar);
          }
       }
       return result;
    }
 }
