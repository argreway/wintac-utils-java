 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WOHistoryManager.java
  * Created:   6/25/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.history.workorder;

 import java.util.List;
 import java.util.Map;
 import java.util.stream.Collectors;

 import javax.swing.table.DefaultTableModel;

 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.sentryfire.model.Item;
 import com.sentryfire.model.WO;
 import com.sentryfire.persistance.DAOFactory;
 import com.sentryfire.persistance.dao.influxdb.InfluxClient;
 import org.joda.time.DateTime;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class WOHistoryManager
 {
    private Logger log = LoggerFactory.getLogger(getClass());

    public void updateMonthlyWOCount()
    {
       DateTime now = new DateTime();

       MutableDateTime start = new MutableDateTime();
       start.setYear(2010);
       start.setMonthOfYear(now.getMonthOfYear());
       start.setDayOfMonth(1);
       start.setHourOfDay(0);
       start.setMinuteOfHour(0);
       start.setSecondOfMinute(0);

       MutableDateTime terminate = new MutableDateTime(now);
       // This should get us past any generated work orders
       terminate.addMonths(1);
       terminate.setDayOfMonth(15);

       Map<String, Map<String, List<String>>> yearToMonthToCount = Maps.newHashMap();

       while (start.isBefore(terminate))
       {
          MutableDateTime end = new MutableDateTime(start);
          end.setDayOfMonth(end.dayOfMonth().getMaximumValue());
          end.setHourOfDay(end.hourOfDay().getMaximumValue());
          end.setMinuteOfHour(end.minuteOfHour().getMaximumValue());
          end.setSecondOfMinute(end.secondOfMinute().getMaximumValue());

          log.info("Updating history for  [" + start + "] to [" + end + "]");
          List<WO> result = DAOFactory.getWipDao().getWorkOrdersByTime(start.toDateTime(), end.toDateTime());

          List<String> jobs = Lists.newArrayList();
          if (result != null)
          {
             log.info("Found " + result.size());
             jobs = result.stream().map(WO::getIN2).collect(Collectors.toList());
          }

          Map<String, List<String>> monthJobs = yearToMonthToCount.get("" + start.getYear());
          if (monthJobs == null)
          {
             monthJobs = Maps.newHashMap();
             yearToMonthToCount.put("" + start.getYear(), monthJobs);
          }
          monthJobs.put("" + start.getMonthOfYear(), jobs);
          yearToMonthToCount.put("" + start.getYear(), monthJobs);

          start.addMonths(1);
          end.addMonths(1);
       }

       DAOFactory.getHistoryDao().writeWOHistory(yearToMonthToCount);
    }

    /**
     * use this when we are going to write the items to the db
     */
    public void updateWOAndItems()
    {
       // Update the current and next month if there are less than 50 History WOs
       MutableDateTime begin = new MutableDateTime();
       begin.setDayOfMonth(1);
       begin.setHourOfDay(0);
       begin.setMinuteOfDay(0);
       begin.setSecondOfDay(0);
       begin.setMillisOfSecond(0);

       MutableDateTime stop = new MutableDateTime(begin);
       stop.setDayOfMonth(stop.dayOfMonth().getMaximumValue());
       stop.setHourOfDay(stop.hourOfDay().getMaximumValue());
       stop.setMinuteOfHour(stop.minuteOfHour().getMaximumValue());
       stop.setSecondOfMinute(stop.secondOfMinute().getMaximumValue());

       // Current Month
       DefaultTableModel dt = DAOFactory.sqlDB().getWorkOrdersByTime(begin.toDateTime(), stop.toDateTime());
       List<WO> historyWOs = DAOFactory.getWipDao().getHistoryWorkOrdersByTime(begin.toDateTime(), stop.toDateTime());
       if (historyWOs != null && dt != null && historyWOs.size() < 50 && dt.getRowCount() > 50)
       {
          log.info("Found " + historyWOs.size() + " History WOs and " + dt.getRowCount() + " actual WOs for current month " + begin + ".  Will update.");
          DAOFactory.getHistoryInfluxClient().insertInfluxStatsForDataTable(dt, InfluxClient.COL.JDATE.toString(), InfluxClient.WIP_FIELDS, null, InfluxClient.MEASUREMENT_HISTORY.WO.toString());
          getWorkOrdersWithItemsAndUpdate(begin.toDateTime(), stop.toDateTime(), true);
       }
       else
       {
          log.info("Found " + historyWOs.size() + " History WOs and " + dt.getRowCount() + " actual WOs for current month " + begin + ".  No items to update.");
       }

       // Next Month
       begin.addMonths(1);
       stop.addMonths(1);
       dt = DAOFactory.sqlDB().getWorkOrdersByTime(begin.toDateTime(), stop.toDateTime());
       historyWOs = DAOFactory.getWipDao().getHistoryWorkOrdersByTime(begin.toDateTime(), stop.toDateTime());
       if (historyWOs != null && dt != null && historyWOs.size() < 50 && dt.getRowCount() > 50)
       {
          log.info("Found " + historyWOs.size() + " History WOs and " + dt.getRowCount() + " actual WOs for current month " + begin + ".  Will update.");
          DAOFactory.getHistoryInfluxClient().insertInfluxStatsForDataTable(dt, InfluxClient.COL.JDATE.toString(), InfluxClient.WIP_FIELDS, null, InfluxClient.MEASUREMENT_HISTORY.WO.toString());
          getWorkOrdersWithItemsAndUpdate(begin.toDateTime(), stop.toDateTime(), true);
       }
       else
       {
          log.info("Found " + historyWOs.size() + " History WOs and " + dt.getRowCount() + " actual WOs for current month " + begin + ".  No items to update.");
       }
    }

    /**
     * use this when you just want to get the list of WO with the items
     * populated in to them.  Note you must use the updateHistoryWOAndItems first.
     */
    public List<WO> getWorkOrdersWithItems()
    {
       // TODO fix this
       return getWorkOrdersWithItemsAndUpdate(null, null, false);

    }

    private List<WO> getWorkOrdersWithItemsAndUpdate(DateTime begin,
                                                     DateTime stop,
                                                     boolean update)
    {

       String errors = "";
       log.info("Updating history for  [" + begin + "] to [" + stop + "]");
       List<WO> result = DAOFactory.getWipDao().getHistoryWorkOrdersByTime(begin.toDateTime(), stop.toDateTime());
       Map<String, Integer> map = Maps.newHashMap();
       int count = 0;
       try
       {
          List<String> list = result.stream().map(WO::getIN2).collect(Collectors.toList());

          if (update)
          {
             log.info("Updating work order items.");
             for (String in2 : list)
             {
                DefaultTableModel dt = DAOFactory.sqlDB().getItemsForWorkOrder(in2);
                DAOFactory.getHistoryInfluxClient().insertInfluxStatsForDataTable(dt, InfluxClient.COL.IDATE.toString(), InfluxClient.ITEM_FIELDS, InfluxClient.HISTORY_ITEM_TAGS, InfluxClient.MEASUREMENT_HISTORY.ITEM.toString());
                count += dt.getRowCount();
                map.put(in2, dt.getRowCount());
             }
          }

          for (WO workOrder : result)
          {
             List<Item> items = DAOFactory.getItemDao().getHistoryWOItemRecordsByIn2(begin.toDateTime(), stop.toDateTime(), workOrder.getIN2());
             if (update && map.get(workOrder.getIN2()) != items.size())
             {
                errors = errors + "IN2 " + workOrder.getIN2() + " E: " + map.get(workOrder.getIN2()) + " F: " + items.size() + ", ";
             }
             workOrder.setLineItems(items);
          }

          if (update)
          {
             log.info("Found [" + count + "] items.");
             if (!errors.isEmpty())
                log.error("Items could not be mapped: " + errors);
          }
       }
       catch (Exception e)
       {
          log.error("Failed to update items in WOs ", e);
       }

       return result;
    }

 }
