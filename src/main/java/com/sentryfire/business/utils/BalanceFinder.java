 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      BalanceFinder.java
  * Created:   6/26/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.utils;

 import java.util.Arrays;
 import java.util.List;
 import java.util.stream.Collectors;

 import com.google.common.collect.Lists;
 import com.sentryfire.persistance.DAOFactory;
 import com.sentryfire.model.Item;
 import org.joda.time.MutableDateTime;

 public class BalanceFinder
 {

    public void findGLDiff()
    {
       Double target = 149.15;
//       Double target = 217.25;
//       Double target = 210.00;


       MutableDateTime start = new MutableDateTime();
       start.setYear(2018);
       start.setMonthOfYear(1);
       start.setDayOfMonth(1);
       start.setHourOfDay(0);

       MutableDateTime terminate = new MutableDateTime(start);
//       terminate.setMonthOfYear(1);
       terminate.setHourOfDay(23);

       List<Item> items = DAOFactory.getItemDao().getItemRecordsByTime(start.toDateTime(), terminate.toDateTime());

       System.out.println("Found " + items.size());


       // First search the AR line items
       List<Item> result = items.stream().filter(i ->
                                                    i.getCOST().equals(target)
                                                    || i.getTOTCOST().equals(target)
                                                    || i.getPOCOST().equals(target)
                                                    || i.getRP().equals(target)
       ).collect(Collectors.toList());

       System.out.println("Found " + result.size());


       List<Double> searchList = items.stream().filter(i -> i.getCOST() != 0).map(i -> i.getCOST()).collect(Collectors.toList());
       searchList.addAll(items.stream().filter(i -> i.getRP() != 0).map(i -> i.getRP()).collect(Collectors.toList()));
       searchList.addAll(items.stream().filter(i -> i.getPOCOST() != 0).map(i -> i.getPOCOST()).collect(Collectors.toList()));
       searchList.addAll(items.stream().filter(i -> i.getTOTCOST() != 0).map(i -> i.getTOTCOST()).collect(Collectors.toList()));

       System.out.println("Searching " + searchList);
       sum_up(searchList, target);

       System.out.println("Done Searching ");

    }


    static void sum_up_recursive(List<Double> numbers,
                                 Double target,
                                 List<Double> partial)
    {
       Double s = 0.0;

       for (Double x : partial)
          s += x;

       if (s.equals(target))
       {
          System.out.println("sum(" + Arrays.toString(partial.toArray()) + ")=" + target);
       }

       if (s >= target)
          return;

       for (int i = 0; i < numbers.size(); i++)
       {
          List<Double> remaining = Lists.newArrayList();
          Double n = numbers.get(i);

          for (int j = i + 1; j < numbers.size(); j++)
             remaining.add(numbers.get(j));

          List<Double> partial_rec = Lists.newArrayList(partial);

          partial_rec.add(n);
          sum_up_recursive(remaining, target, partial_rec);
       }
    }

    static void sum_up(List<Double> numbers,
                       Double target)
    {
       sum_up_recursive(numbers, target, Lists.newArrayList());
    }

 }
