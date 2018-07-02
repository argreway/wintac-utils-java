 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WOHistory.java
  * Created:   6/25/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.history.workorder;

 import java.util.List;
 import java.util.Map;

 import com.google.common.collect.Maps;
 import com.sentryfire.persistance.DAOFactory;
 import com.sentryfire.model.WO;
 import org.joda.time.DateTime;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class WOHistory
 {
    private Logger log = LoggerFactory.getLogger(getClass());

    public void updateMonthlyWOCount()
    {
       // Update the history counts when the get processed + 1 year time

       DateTime now = new DateTime();

       MutableDateTime start = new MutableDateTime();
       start.setYear(now.getYear() - 1);
       start.setMonthOfYear(now.getMonthOfYear());
       start.setDayOfMonth(1);
       start.setHourOfDay(0);

       MutableDateTime terminate = new MutableDateTime(now);
       terminate.addMonths(1);
       terminate.setDayOfMonth(15);

       Map<String, Map<String, Integer>> yearToMonthToCount = Maps.newHashMap();

       while (start.isBefore(terminate))
       {
          MutableDateTime end = new MutableDateTime(start);
          end.setDayOfMonth(end.dayOfMonth().getMaximumValue());
          end.setHourOfDay(end.hourOfDay().getMaximumValue());

          log.info("Updating history for  [" + start + "] to [" + end + "]");
          List<WO> result = DAOFactory.getWipDao().getWorkOrdersByTime(start.toDateTime(), end.toDateTime());

          int size = 0;
          if (result != null)
          {
             log.info("Found " + result.size());
             size = result.size();
          }

          Map<String, Integer> monthCount = yearToMonthToCount.get("" + start.getYear());
          if (monthCount == null)
          {
             monthCount = Maps.newHashMap();
             yearToMonthToCount.put("" + start.getYear(), monthCount);
          }
          monthCount.put("" + start.getMonthOfYear(), size);
          yearToMonthToCount.put("" + start.getYear(), monthCount);

          start.addMonths(1);
          end.addMonths(1);
       }

       DAOFactory.getHistoryDao().writeWOHistory(yearToMonthToCount);
    }

 }
