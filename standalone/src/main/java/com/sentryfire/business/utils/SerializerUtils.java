 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      SerializerUtils.java
  * Created:   7/24/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.utils;

 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.util.List;
 import java.util.Map;

 import com.sentryfire.business.schedule.model.DistanceData;
 import com.sentryfire.business.schedule.model.GeoCodeData;
 import com.sentryfire.config.AppConfiguartion;
 import com.sentryfire.model.WO;
 import org.joda.time.DateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class SerializerUtils
 {
    static Logger log = LoggerFactory.getLogger(SerializerUtils.class);

    public static void serializeWOList(DateTime time,
                                       Object obj)
    {
       serializeObject(time, obj, "wo-data");
    }

    public static List<WO> deSerializeWOList(DateTime dateTime)
    {
       return (List<WO>) deSerializeObject(dateTime, "wo-data");
    }

    public static void serializeDistanceDataName(DateTime time,
                                                 Object obj,
                                                 String name)
    {
       serializeObject(time, obj, name + "-distance-data");
    }

    public static Map<String, Map<String, DistanceData>> deSerializeDistanceDataName(DateTime time,
                                                                                     String name)
    {
       // Let's deserialize an Object
       return (Map<String, Map<String, DistanceData>>) deSerializeObject(time, name + "-distance-data");
    }

    public static void serializeDistributionMap(DateTime time,
                                                Map<String, List<WO>> distMap)
    {
       serializeObject(time, distMap, "distribution-map");
    }

    public static Map<String, List<WO>> deSerializeDistributionMap(DateTime time)
    {
       return (Map<String, List<WO>>) deSerializeObject(time, "distribution-map");
    }

    public static void serializeGeoCodeMap(Object obj)
    {
       serializeObject(null, obj, "geo-code");
    }

    public static Map<String, GeoCodeData> deSerializeGeoCodeMap()
    {
       return (Map<String, GeoCodeData>) deSerializeObject(null, "geo-code");
    }

    /////////////
    // protected
    /////////////
    protected static void serializeObject(DateTime time,
                                          Object obj,
                                          String file)
    {
       // Let's serialize an Object
       try
       {
          String fileLocation = AppConfiguartion.getInstance().getDataDirBase() + file + ".ser";
          if (time != null)
          {
             String yearMonth = getMonthYearString(time);
             fileLocation = AppConfiguartion.getInstance().getDataDirBase() + yearMonth + "/";
             File dir = new File(fileLocation);
             if (!dir.exists())
                dir.mkdir();
             fileLocation = AppConfiguartion.getInstance().getDataDirBase() + yearMonth + "/" + file + ".ser";
          }
          FileOutputStream fileOut = new FileOutputStream(fileLocation);
          ObjectOutputStream out = new ObjectOutputStream(fileOut);
          out.writeObject(obj);
          out.close();
          fileOut.close();
       }
       catch (FileNotFoundException e)
       {
          e.printStackTrace();
       }
       catch (IOException e)
       {
          e.printStackTrace();
       }

    }


    protected static Object deSerializeObject(DateTime time,
                                              String file)
    {
       Object result = null;
       try
       {
          String fileLocation = AppConfiguartion.getInstance().getDataDirBase() + "/" + file + ".ser";
          if (time != null)
          {
             String yearMonth = getMonthYearString(time);
             fileLocation = AppConfiguartion.getInstance().getDataDirBase() + yearMonth + "/" + file + ".ser";
          }
          FileInputStream fileIn = new FileInputStream(fileLocation);
          ObjectInputStream in = new ObjectInputStream(fileIn);
          result = in.readObject();
          in.close();
          fileIn.close();
       }
       catch (IOException e2)
       {
          log.warn("Failed to deserialize: ", e2);
       }
       catch (Exception ex)
       {
          ex.printStackTrace();
       }
       return result;
    }

    protected static String getMonthYearString(DateTime dateTime)
    {
       return dateTime.getMonthOfYear() + "-" + dateTime.getYear();

    }

 }
