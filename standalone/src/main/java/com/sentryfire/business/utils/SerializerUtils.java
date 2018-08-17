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
 import com.sentryfire.model.WO;

 public class SerializerUtils
 {

    public static void serializeList(Object obj)
    {
       // Let's serialize an Object
       try
       {
          FileOutputStream fileOut = new FileOutputStream("/tmp/test-data.txt");
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

    public static List<WO> deWOSerializeList()
    {
       // Let's deserialize an Object
       List<WO> result = null;
       try
       {
          FileInputStream fileIn = new FileInputStream("/tmp/test-data.txt");
          ObjectInputStream in = new ObjectInputStream(fileIn);
          result = (List<WO>) in.readObject();
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

    public static void serializeDistanceDataName(Object obj,
                                                 String name)
    {
       // Let's serialize an Object
       try
       {
          FileOutputStream fileOut = new FileOutputStream("/tmp/" + name + "-distance-data.txt");
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


    public static Map<String, Map<String, DistanceData>> deSerializeDistanceDataName(String name)
    {
       // Let's deserialize an Object
       Map<String, Map<String, DistanceData>> result = null;
       try
       {
          FileInputStream fileIn = new FileInputStream("/tmp/" + name + "-distance-data.txt");
          ObjectInputStream in = new ObjectInputStream(fileIn);
          result = (Map<String, Map<String, DistanceData>>) in.readObject();
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
