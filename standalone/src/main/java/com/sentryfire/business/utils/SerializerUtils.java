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
 import com.sentryfire.config.AppConfiguartion;
 import com.sentryfire.model.WO;

 public class SerializerUtils
 {


    public static void serializeWOList(Object obj)
    {
       serializeObject(obj, "test-data");
    }

    public static List<WO> deSerializeWOList()
    {
       return (List<WO>) deSerializeObject("test-data");
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


    public static Object deSerializeObject(String file)
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
          e2.printStackTrace();
       }
       catch (Exception ex)
       {
          ex.printStackTrace();
       }
       return result;
    }

 }
