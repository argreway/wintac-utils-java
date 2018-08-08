 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      DAOFactory.java
  * Created:   6/1/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.persistance;

 import com.sentryfire.config.ExternalConfiguartion;
 import com.sentryfire.persistance.dao.influxdb.ARDao;
 import com.sentryfire.persistance.dao.influxdb.HistoryDao;
 import com.sentryfire.persistance.dao.influxdb.ITEMDao;
 import com.sentryfire.persistance.dao.influxdb.InfluxAdminDao;
 import com.sentryfire.persistance.dao.influxdb.InfluxClient;
 import com.sentryfire.persistance.dao.influxdb.PAYDao;
 import com.sentryfire.persistance.dao.influxdb.WIPDao;
 import com.sentryfire.persistance.dao.sql.DBConnect;
 import org.influxdb.impl.InfluxDBResultMapper;

 public class DAOFactory
 {
    private static DBConnect dbConnect = new DBConnect();

    private static InfluxClient influxClient = new InfluxClient(false);
    private static InfluxClient influxClientHistory = new InfluxClient(true);
    private static InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

    private static InfluxAdminDao influxAdminDao = new InfluxAdminDao();
    private static ARDao arDao = new ARDao();
    private static ITEMDao itemDao = new ITEMDao();
    private static PAYDao payDao = new PAYDao();
    private static HistoryDao historyDao = new HistoryDao();
    private static WIPDao wipDao = new WIPDao();


    //// Getters

    synchronized public static DBConnect sqlDB()
    {
       dbConnect.connectToDB(
          ExternalConfiguartion.getInstance().getServer(),
          ExternalConfiguartion.getInstance().getDatabase(),
          ExternalConfiguartion.getInstance().getUser(),
          ExternalConfiguartion.getInstance().getPassword());
       return dbConnect;
    }

    public static InfluxAdminDao getInfluxAdminDao()
    {
       return influxAdminDao;
    }

    public static InfluxClient getInfluxClient()
    {
       return influxClient;
    }

    public static InfluxClient getHistoryInfluxClient()
    {
       return influxClientHistory;
    }

    public static ARDao getArDao()
    {
       return arDao;
    }

    public static InfluxDBResultMapper getResultMapper()
    {
       return resultMapper;
    }

    public static ITEMDao getItemDao()
    {
       return itemDao;
    }

    public static PAYDao getPayDao()
    {
       return payDao;
    }

    public static HistoryDao getHistoryDao()
    {
       return historyDao;
    }

    public static WIPDao getWipDao()
    {
       return wipDao;
    }

    public static void shutdown()
    {
       influxClient.shutdown();
       influxClientHistory.shutdown();
    }
 }
