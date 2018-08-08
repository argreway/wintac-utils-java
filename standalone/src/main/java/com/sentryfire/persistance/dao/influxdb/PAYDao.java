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

 import com.sentryfire.persistance.DAOFactory;
 import com.sentryfire.model.Payroll;
 import org.influxdb.dto.QueryResult;
 import org.joda.time.DateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class PAYDao
 {

    Logger log = LoggerFactory.getLogger(getClass());

    protected static final String ALL_PAY = "SELECT * FROM PAY";

    public List<Payroll> getAllPayRecords()
    {
       try
       {
          QueryResult queryResult = DAOFactory.getInfluxClient().query(ALL_PAY);
          List<Payroll> results = DAOFactory.getResultMapper().toPOJO(queryResult, Payroll.class);
          return results;
       }
       catch (Exception e)
       {
          e.printStackTrace();
          log.error("Failed to query influxdb for PAY." + e);
       }
       return null;
    }

    public List<Payroll> getPayRecordsByTime(DateTime start,
                                             DateTime end)
    {
       try
       {
          String queryString = ALL_PAY + " WHERE time >= " + start.getMillis() + "ms AND time <= " + end.getMillis() + "ms";
          QueryResult queryResult = DAOFactory.getInfluxClient().query(queryString);

          List<Payroll> results = DAOFactory.getResultMapper().toPOJO(queryResult, Payroll.class);
          return results;
       }
       catch (Exception e)
       {
          e.printStackTrace();
          log.error("Failed to query influxdb for PAY." + e);
       }
       return null;
    }

 }
