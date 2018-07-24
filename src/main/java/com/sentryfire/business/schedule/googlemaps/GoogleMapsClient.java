 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      GoogleMapsClient.java
  * Created:   5/25/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.googlemaps;

 import java.util.Collection;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.stream.Collectors;

 import com.google.common.collect.Maps;
 import com.google.common.collect.Sets;
 import com.google.gson.Gson;
 import com.google.gson.GsonBuilder;
 import com.google.maps.GeoApiContext;
 import com.google.maps.GeocodingApi;
 import com.google.maps.model.GeocodingResult;
 import com.sentryfire.config.AppConfiguartion;
 import com.sentryfire.config.ExternalConfiguartion;
 import com.sentryfire.model.Item;
 import com.sentryfire.model.ItemStatHolder;
 import com.sentryfire.model.SKILL;
 import com.sentryfire.model.WO;
 import com.sentryfire.model.WOMeta;
 import com.sentryfire.persistance.DAOFactory;
 import org.joda.time.DateTime;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class GoogleMapsClient
 {
    Logger log = LoggerFactory.getLogger(getClass());

    public List<WO> route(DateTime start)
    {
       List<WO> result = getWorkOrderList(start);

       List<WO> denver = result.stream().filter(w -> w.getDEPT().equals("DENVER")).collect(Collectors.toList());
       List<WO> greeley = result.stream().filter(w -> w.getDEPT().equals("GREELEY")).collect(Collectors.toList());
       List<WO> cosprings = result.stream().filter(w -> w.getDEPT().equals("CO_SPRINGS")).collect(Collectors.toList());


       log.info("\n\n\nDenver WO Count: " + denver.size());
       calculateWorkload(denver);
       log.info("\n\n\nGreeley WO Count: " + greeley.size());
       calculateWorkload(greeley);
       log.info("\n\n\nCOSprings WO Count: " + cosprings.size());
       calculateWorkload(cosprings);

       log.info("\n\n\nTotal WO Count: " + result.size());
       calculateWorkload(result);


//       Set<String> itemCodes = result.stream().map(WO::getLineItems).flatMap(i -> i.stream()).collect(Collectors.toList()).
//          stream().map(Item::getIC).collect(Collectors.toSet());
//       mapTechs(list);
       System.out.println("done");
//       map();

       return result;
    }

    private void calculateWorkload(List<WO> list)
    {
       Map<String, Integer> itemTimes = AppConfiguartion.getInstance().getItemTimeMinsMap();
       Map<String, SKILL> itemSkill = AppConfiguartion.getInstance().getItemToSkill();
//       List<Item> allItems = list.stream().map(WO::getLineItems).flatMap(i -> i.stream()).collect(Collectors.toList());
       Set<String> unknown = Sets.newHashSet();


       for (WO wo : list)
       {
          WOMeta meta = new WOMeta();
          wo.setMetaData(meta);
          for (Item item : wo.getLineItems())
          {
             String ic = item.getIC();
             SKILL skill = itemSkill.get(ic);
             ItemStatHolder current = new ItemStatHolder(0, 0, ic, skill);
             meta.getItemStatHolderList().add(current);

             // If count is null/0 - there should be at least 1 item
             Integer count = item.getHQ();
             if (count == null || count == 0)
                count = 1;

             if (ic != null)
             {
                Integer time = itemTimes.get(ic);
                if (time != null)
                {
                   current.setMin(count * time);
                   current.setCount(count);
                }
                else
                {
                   unknown.add(ic);
                }
             }
          }
       }

       log.error("Unknown item times: " + unknown);
       List<ItemStatHolder> allItemStats = list.stream().map(WO::getMetaData).map(WOMeta::getItemStatHolderList).flatMap(Collection::stream).collect(Collectors.toList());


       log.info("Totals Per Item Type:");
       printItemTotals(allItemStats);
       log.info("----------");

       Integer driveTotal = list.size() * AppConfiguartion.getInstance().getDriveTime();
       log.info("Drive Total: " + driveTotal);

       Integer workTotal = allItemStats.stream().map(ItemStatHolder::getMin).mapToInt(Number::intValue).sum();
       log.info("Work Item Total: " + workTotal);
       Integer subTotal = driveTotal + workTotal;
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
             .apiKey(ExternalConfiguartion.getInstance().getGoogleMapApiKey()).build();
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

    public List<WO> getWorkOrderList(DateTime start)
    {
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

    protected void printItemTotals(List<ItemStatHolder> allItemHolders)
    {
       Map<String, ItemStatHolder> sum = Maps.newHashMap();
       for (ItemStatHolder h : allItemHolders)
       {
          ItemStatHolder current = sum.get(h.getItemCode());
          if (current == null)
          {
             current = new ItemStatHolder(0, 0, h.getItemCode(),
                                          AppConfiguartion.getInstance().getItemToSkill().get(h.getItemCode()));
             sum.put(h.getItemCode(), current);
          }
          current.setMin(current.getMin() + h.getMin());
          current.setCount(current.getCount() + h.getCount());
       }

       List<ItemStatHolder> printSum = sum.values().stream().sorted(
          (o1, o2) -> o2.getMin().compareTo(o1.getMin())).collect(Collectors.toList());

       log.info(printSum.toString());
    }

 }
