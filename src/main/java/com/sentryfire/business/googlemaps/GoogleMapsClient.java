 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      GoogleMapsClient.java
  * Created:   5/25/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.googlemaps;

 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.stream.Collectors;

 import com.google.gson.Gson;
 import com.google.gson.GsonBuilder;
 import com.google.maps.GeoApiContext;
 import com.google.maps.GeocodingApi;
 import com.google.maps.model.GeocodingResult;
 import com.sentryfire.persistance.DAOFactory;
 import com.sentryfire.model.WO;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class GoogleMapsClient
 {
    Logger log = LoggerFactory.getLogger(getClass());
    // sentryfirescheduler
    private static final String API_KEY = "AIzaSyCYmecgTNytV9Z-Z8IOB86cCWwapJGeCj0";

    public void route()
    {
       List<WO> list = getWorkOrderList();

       Set<String> techs = list.stream().map(WO::getTECH).collect(Collectors.toSet());

       Map<String, List<WO>> techToWo = techs.stream().collect(Collectors.toMap(
          t -> t, t -> list.stream().filter(w1 -> w1.getTECH().equals(t)).collect(Collectors.toList())));

       for (Map.Entry<String, List<WO>> entry : techToWo.entrySet())
       {
          System.out.println("key: " + entry.getKey());
          System.out.println("Values " + entry.getValue().stream().map(w -> convert(w.getADR1())).collect(Collectors.toList()));
       }

       List<WO> rc = techToWo.get("RC");


//       map();

    }

    protected void map()
    {
       try
       {
          GeoApiContext context = new GeoApiContext.Builder()
             .apiKey(API_KEY).build();
          //           PlacesApi.nearbySearchNextPage();
          //
          GeocodingResult[] results = GeocodingApi.geocode(
             context,
             "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
          Gson gson = new GsonBuilder().setPrettyPrinting().create();
          System.out.println(gson.toJson(results[0].addressComponents));
       }
       catch (Exception e)
       {
          log.error("Failed to query googlemaps api: ", e);
       }


    }

    protected List<WO> getWorkOrderList()
    {
       MutableDateTime start = new MutableDateTime();
       start.setYear(2018);
       start.setMonthOfYear(7);
       start.setDayOfMonth(1);
       start.setHourOfDay(0);

       MutableDateTime end = new MutableDateTime(start);
       end.setDayOfMonth(start.dayOfMonth().getMaximumValue());


       List<WO> result = DAOFactory.getWipDao().getWorkOrdersByTime(start.toDateTime(), end.toDateTime());

       List<WO> denver = result.stream().filter(w -> w.getDEPT().equals("DENVER")).collect(Collectors.toList());
       List<WO> greeley = result.stream().filter(w -> w.getDEPT().equals("GREELEY")).collect(Collectors.toList());
       List<WO> cosprings = result.stream().filter(w -> w.getDEPT().equals("CO_SPRINGS")).collect(Collectors.toList());

       log.info("Total Count: " + result.size());
       log.info("Denver Count: " + denver.size());
       log.info("Greeley Count: " + greeley.size());
       log.info("COSprings Count: " + cosprings.size());

       return cosprings;
    }

    protected String convert(String item)
    {
       if (item == null)
          return item;
       return item.replace("_", " ");
    }

 }
