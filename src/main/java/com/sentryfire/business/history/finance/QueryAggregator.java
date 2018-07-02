 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      QueryAggregator.java
  * Created:   6/20/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.history.finance;

 import java.util.List;
 import java.util.Objects;

 import com.google.common.collect.Lists;
 import com.sentryfire.persistance.DAOFactory;
 import com.sentryfire.model.Item;
 import com.sentryfire.model.LaborHistory;
 import com.sentryfire.model.Payroll;
 import org.joda.time.DateTime;
 import org.joda.time.MutableDateTime;

 public class QueryAggregator
 {

    public void laborEfficencyRatios()
    {
       DateTime now = new DateTime();

       int year = 2010;

       MutableDateTime start = new MutableDateTime();
       start.setYear(year);
       start.setMonthOfYear(1);
       start.setDayOfMonth(1);
       start.setMinuteOfDay(0);

       MutableDateTime end = new MutableDateTime();
       end.setYear(year);
       end.setMonthOfYear(12);
       end.setDayOfMonth(31);
       end.setHourOfDay(23);
       end.setMinuteOfHour(59);

       List<LaborHistory> historyList = Lists.newArrayList();

       while (start.isBefore(now))
       {
          List<Item> itemList = DAOFactory.getItemDao().getItemRecordsByTime(start.toDateTime(), end.toDateTime());
          List<Payroll> payrollList = DAOFactory.getPayDao().getPayRecordsByTime(start.toDateTime(), end.toDateTime());


          Double cost = itemList.stream().filter(Objects::nonNull).mapToDouble(Item::getTOTCOST).sum();
          Double revenue = itemList.stream().filter(Objects::nonNull).
             filter(i -> i.getCOST() != null).mapToDouble(Item::getCOST).sum();

          Double totalPay = payrollList.stream().filter(Objects::nonNull).mapToDouble(Payroll::getGROSS).sum();
          Double fixedLabor = payrollList.stream().filter(Objects::nonNull).filter(this::isDirect).mapToDouble(Payroll::getGROSS).sum();

          Double directLabor = totalPay - fixedLabor;
          Double grossProfit = revenue - cost;
          Double contribMargin = grossProfit - directLabor;
          Double netProfit = contribMargin - fixedLabor;

          System.out.println("Date: " + end + " Rev: " + revenue + " Cost: " + cost + " Pay: " + totalPay + " DL: " + directLabor + " FL: " + fixedLabor);
          LaborHistory history = new LaborHistory();
          history.setTime(end.toString());
          history.setTotalRevenue(revenue);
          history.setTotalCost(cost);
          history.setDirectLabor(directLabor);
          history.setFixedLabor(fixedLabor);
          history.setTotalLabor(totalPay);
          history.setGrossProfit(grossProfit);
          history.setContribMargin(contribMargin);
          history.setNetProfit(netProfit);

          historyList.add(history);

          // Increment year
          year++;
          start.setYear(year);
          end.setYear(year);
       }


       DAOFactory.getHistoryDao().writeLaborHistoryRecords(historyList);
    }

    protected boolean isDirect(Payroll pay)
    {
//       System.out.println("Date: " + date + ", " + item.getTOTCOST());
       return FIXED_EMP.contains(pay.getNAME());
    }

    public static final List<String> FIXED_EMP = Lists.newArrayList(
       "DUFFY",
       "ANTHONY GREWAY",
       "BILLY RAY HENDERSON",
       "BRITTANY TRUJILLO",
       "CATHERINE FLAGG",
       "CHARISSA SNIDER",
       "DEBORAH CAMPBELL",
       "DUNCAN GRAY",
       "GLORIA GREWAY",
       "JULIA MILES",
       "LOUIS GREWAY",
       "MARLENE ALEMAN",
       "MOLLY DUFFY",
       "NICHOLAS WESTRICH",
       "PRISCELLA HUGHES",
       "PRISCELLA LASTER",
       "SAMANTHA HELM",
       "SHIVADAS FLAGG",
       "STEVEN BUXMAN",
       "ROBERT GRAY",
       "VERONICA CRUZ",
       "WILLIAM KLINK",
       "ZOE ANDERSON"
    );

 }
