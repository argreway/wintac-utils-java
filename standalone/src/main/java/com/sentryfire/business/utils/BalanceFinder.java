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
 import java.util.Map;
 import java.util.stream.Collectors;

 import javax.swing.table.DefaultTableModel;

 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.sentryfire.model.ActivityLog;
 import com.sentryfire.model.Item;
 import com.sentryfire.model.PojoConverterUtil;
 import com.sentryfire.persistance.DAOFactory;
 import org.joda.time.MutableDateTime;

 public class BalanceFinder
 {

    public static void searchHistoryForIncorrectDate()
    {
       MutableDateTime start = new MutableDateTime();
       start.setYear(2018);
       start.setMonthOfYear(4);
       start.setDayOfMonth(1);
       start.setHourOfDay(0);

       MutableDateTime end = new MutableDateTime(start);
       end.setYear(2018);
       end.setMonthOfYear(5);
       end.setDayOfMonth(2);
       end.setHourOfDay(23);

       DefaultTableModel model = DAOFactory.sqlDB().getUserActivityLog(start.toDateTime(), end.toDateTime());

       List<ActivityLog> logs = PojoConverterUtil.convertTableToLog(model);

       List<ActivityLog> invLogs = logs.stream().filter(l -> l.getAction().contains("Invoice")).collect(Collectors.toList());

       Map<String, List<ActivityLog>> cnToIn = Maps.newHashMap();
       for (ActivityLog aLog : invLogs)
       {
          String str = aLog.getAction();
          str = str.substring(str.lastIndexOf("Invoice")).trim();
          str = str.substring(str.indexOf(" ")).trim();
          String[] ary = str.split("-");

          String cn = ary[0].trim();
          String[] ary2 = cn.split(" ");
          if (ary2.length > 1)
             cn = ary2[ary2.length - 1];
          String in = ary[1].trim();

          aLog.setCn(cn);
          aLog.setIn(in);

          List<ActivityLog> current = cnToIn.get(cn + ":" + in);
          if (current == null)
          {
             current = Lists.newArrayList();
             cnToIn.put(cn + ":" + in, current);
          }
          current.add(aLog);
       }


       for (Map.Entry<String, List<ActivityLog>> entry : cnToIn.entrySet())
       {
          String[] ary = entry.getKey().split(":");
          String cn = ary[0];
          String in = ary[1];

          DefaultTableModel dt = DAOFactory.sqlDB().getAllRCVValues(cn, in);
          if (dt.getRowCount() == 0)
             System.err.println("Entry no longer exists: " + cn + " " + in);
          else
          {
             for (ActivityLog aLog : entry.getValue())
             {
                aLog.setInvDate(PojoConverterUtil.getValueFromTable(dt, "INVDATE"));
                aLog.setjDate(PojoConverterUtil.getValueFromTable(dt, "JDATE"));
                aLog.setdDate(PojoConverterUtil.getValueFromTable(dt, "DDATE"));
             }
          }
       }

       List<ActivityLog> badActivities = Lists.newArrayList();
       for (ActivityLog aLog : invLogs)
       {
          if (aLog.getInvDate() != null)
          {
             if (!aLog.getInvDate().stream().filter(s -> s.contains("2017")).collect(Collectors.toList()).isEmpty())
             {
                badActivities.add(aLog);
             }
          }
       }

       badActivities.forEach(s -> System.out.println(s.getDate() + " : " + s.getAction() + "     ->      " + s.getInvDate()));
    }

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
