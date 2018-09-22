 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      SentryMainTest.java
  * Created:   7/27/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire;

 import java.util.List;
 import java.util.Map;
 import java.util.function.Function;
 import java.util.stream.Collectors;

 import com.sentryfire.business.schedule.model.GeoCodeData;
 import com.sentryfire.business.utils.SerializerUtils;
 import com.sentryfire.model.WO;
 import org.joda.time.DateTime;
 import org.testng.annotations.Test;

 public class SentryMainTest
 {
    @Test
    public void testMe()
    {
//       System.out.println(MapUtils.distance(39.734907, -104.970863, 33.915151, -117.564805) + "To CA");
//       System.out.println(MapUtils.distance(32.9697, -96.80322, 29.46786, -98.53506) + " Kilometers\n");
    }

    @Test
    public void testUpdateJobNames()
    {

       DateTime now = new DateTime();
       List<WO> woList = SerializerUtils.deSerializeWOList(now);
       Map<String, WO> in2ToWOList = woList.stream().collect(Collectors.toMap(WO::getIN2, Function.identity()));

       Map<String, GeoCodeData> result = SerializerUtils.deSerializeGeoCodeMap();

       for (Map.Entry<String, GeoCodeData> entry : result.entrySet())
       {
          GeoCodeData d = entry.getValue();
          String in2 = d.getWoNumber();
          WO wo = in2ToWOList.get(in2);
          if (wo == null)
             continue;
          System.out.println("in2 " + in2);
          d.setJobName(wo.getNAME());
       }
       System.out.println("done");
       SerializerUtils.serializeGeoCodeMap(result);

    }


 }
