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

 import com.google.common.collect.Maps;
 import com.google.gson.Gson;
 import com.google.gson.GsonBuilder;
 import com.google.maps.GeoApiContext;
 import com.google.maps.GeocodingApi;
 import com.google.maps.model.GeocodingResult;
 import com.sentryfire.SentryAppConfiguartion;
 import com.sentryfire.model.Item;
 import com.sentryfire.model.WO;
 import com.sentryfire.persistance.DAOFactory;
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
       List<WO> result = getWorkOrderList();

       List<WO> denver = result.stream().filter(w -> w.getDEPT().equals("DENVER")).collect(Collectors.toList());
       List<WO> greeley = result.stream().filter(w -> w.getDEPT().equals("GREELEY")).collect(Collectors.toList());
       List<WO> cosprings = result.stream().filter(w -> w.getDEPT().equals("CO_SPRINGS")).collect(Collectors.toList());


       log.info("\n\n\nDenver Count: " + denver.size());
       calculateWorkload(denver);
       log.info("\n\n\nGreeley Count: " + greeley.size());
       calculateWorkload(greeley);
       log.info("\n\n\nCOSprings Count: " + cosprings.size());
       calculateWorkload(cosprings);

       log.info("\n\n\nTotal Count: " + result.size());
       calculateWorkload(result);


//       Set<String> itemCodes = result.stream().map(WO::getLineItems).flatMap(i -> i.stream()).collect(Collectors.toList()).
//          stream().map(Item::getIC).collect(Collectors.toSet());
//       mapTechs(list);
//       map();

    }

    private void calculateWorkload(List<WO> list)
    {
       Map<String, Integer> itemTimes = SentryAppConfiguartion.getInstance().getItemTimeMinsMap();
       Map<String, StatHolder> totalTimes = Maps.newHashMap();
       List<Item> allItems = list.stream().map(WO::getLineItems).flatMap(i -> i.stream()).collect(Collectors.toList());


       for (Item item : allItems)
       {
          String ic = item.getIC();
          for (Map.Entry<String, Integer> entry : itemTimes.entrySet())
          {
             if (ic != null && item.getHQ() != null && ic.equals(entry.getKey()) && entry.getValue() != null)
             {
                StatHolder current = totalTimes.get(ic);
                int total = item.getHQ() * entry.getValue();
                if (current == null)
                {
                   current = new StatHolder();
                   current.min = 0;
                   current.count = 0;
                   totalTimes.put(ic, current);
                }

                current.min += total;
                current.count += item.getHQ();
             }
          }
       }
       System.out.println(totalTimes);
       log.info("Totals Per Item Type:");
       log.info(totalTimes.toString());
       log.info("----------");

       Integer driveTotal = list.size() * SentryAppConfiguartion.getInstance().getDriveTime();
       log.info("Drive Total: " + driveTotal);

       Integer subTotal = totalTimes.values().stream().map(h -> h.min).mapToInt(Number::intValue).sum();
       subTotal += driveTotal;
       log.info("----------");
       log.info("Sum (Min): " + subTotal);
       log.info("Sum (Hours): " + subTotal / 60.0);
       log.info("Sum (Week): " + subTotal / 60.0 / 40.0);
       log.info("Men (Work Months): " + subTotal / 60.0 / 40.0 / 4.0);
    }

    private void mapTechs(List<WO> list)
    {
       // Create buckets for techs
       Set<String> techs = list.stream().map(WO::getTECH).collect(Collectors.toSet());
       Map<String, List<WO>> techToWo = techs.stream().collect(Collectors.toMap(
          t -> t, t -> list.stream().filter(w1 -> w1.getTECH().equals(t)).collect(Collectors.toList())));

       for (Map.Entry<String, List<WO>> entry : techToWo.entrySet())
       {
          System.out.println("key: " + entry.getKey());
          System.out.println("Values " + entry.getValue().stream().map(w -> convert(w.getADR1())).collect(Collectors.toList()));
       }

       List<WO> rc = techToWo.get("RC");
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
       start.setMinuteOfHour(0);
       start.setSecondOfMinute(0);

       MutableDateTime end = new MutableDateTime(start);
       end.setDayOfMonth(start.dayOfMonth().getMaximumValue());


       return DAOFactory.getWipDao().getHistoryWOAndItems(start.toDateTime(), end.toDateTime());
    }

    protected String convert(String item)
    {
       if (item == null)
          return item;
       return item.replace("_", " ");
    }

    protected class StatHolder
    {
       public Integer min;
       public Integer count;

       @Override
       public String toString()
       {
          return "(min=" + min +
                 ", count=" + count + ")";
       }
    }

 }
