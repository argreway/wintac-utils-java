 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WorkLoadCalculator.java
  * Created:   8/9/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule;

 import java.util.Collection;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.stream.Collectors;

 import com.google.common.collect.Maps;
 import com.google.common.collect.Sets;
 import com.sentryfire.config.AppConfiguartion;
 import com.sentryfire.model.Item;
 import com.sentryfire.model.ItemStatHolder;
 import com.sentryfire.model.SKILL;
 import com.sentryfire.model.WO;
 import com.sentryfire.model.WOMeta;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class WorkLoadCalculator
 {
    static Logger log = LoggerFactory.getLogger(WorkLoadCalculator.class);

    public static void calculateWorkLoad(List<WO> list)
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

    protected static void printItemTotals(List<ItemStatHolder> allItemHolders)
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
