 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      InfluxAdminDao.java
  * Created:   6/28/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.persistance.dao.influxdb;

 import com.sentryfire.persistance.DAOFactory;

 public class InfluxAdminDao
 {

    public void createDataBase()
    {
       try
       {
          DAOFactory.getInfluxClient().createDataBase();
       }
       catch (Exception e)
       {
          e.printStackTrace();
          System.err.println("Failed to create influxdb database." + e);
       }
    }

    public void dropDataBase()
    {
       try
       {
          DAOFactory.getInfluxClient().dropDataBase();
       }
       catch (Exception e)
       {
          e.printStackTrace();
          System.err.println("Failed to create influxdb database." + e);
       }
    }

 }
