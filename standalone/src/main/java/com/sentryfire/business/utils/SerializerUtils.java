 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      SerializerUtils.java
  * Created:   7/24/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.utils;

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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class SerializerUtils
 {
    static Logger log = LoggerFactory.getLogger(SerializerUtils.class);

    public static void serializeWOList(Object obj)
    {
       serializeObject(obj, "wo-data");
    }

    public static List<WO> deSerializeWOList()
    {
       return (List<WO>) deSerializeObject("wo-data");
    }

    public static void serializeDistanceDataName(Object obj,
                                                 String name)
    {
       serializeObject(obj, name + "-distance-data");
    }

    public static Map<String, Map<String, DistanceData>> deSerializeDistanceDataName(String name)
    {
       // Let's deserialize an Object
       return (Map<String, Map<String, DistanceData>>) deSerializeObject(name + "-distance-data");
    }

    public static void serializeGeoCodeMap(Object obj)
    {
       serializeObject(obj, "geo-code");
    }

    public static Map<String, GeoCodeData> deSerializeGeoCodeMap()
    {
       return (Map<String, GeoCodeData>) deSerializeObject("geo-code");
    }

    /////////////
    // protected
    /////////////
    protected static void serializeObject(Object obj,
                                          String file)
    {
       // Let's serialize an Object
       try
       {
          FileOutputStream fileOut = new FileOutputStream(AppConfiguartion.getInstance().getDataDirBase() + file + ".ser");
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


    protected static Object deSerializeObject(String file)
    {
       Object result = null;
       try
       {
          FileInputStream fileIn = new FileInputStream(AppConfiguartion.getInstance().getDataDirBase() + file + ".ser");
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

 }
