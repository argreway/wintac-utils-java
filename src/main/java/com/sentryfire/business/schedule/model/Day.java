 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      Day.java
  * Created:   7/19/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.model;

 import java.util.Iterator;
 import java.util.Map;
 import java.util.Objects;
 import java.util.TreeMap;
 import java.util.stream.Collectors;

 import com.google.common.collect.Maps;
 import com.sentryfire.config.AppConfiguartion;
 import com.sentryfire.model.WO;
 import org.joda.time.DateTime;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class Day
 {
    private Logger log = LoggerFactory.getLogger(getClass());


    private Integer dayNumber;
    private Integer monthNumber;
    private Integer year;

    protected DateTime END_OF_DAY;
    protected DateTime BEGIN_OF_DAY;
    protected DateTime LUNCH_WINDOW_BEGIN;
    protected DateTime LUNCH_WINDOW_END;

    // Can be used for vacation or weekends
    private boolean isWorkDay = false;
    private boolean isCleared = false;

    // Sorted by start time
    TreeMap<Float, EventTask> eventTaskList = new TreeMap<>();

    public Day(Integer dayNumber,
               Integer monthNumber,
               Integer year,
               boolean isWorkDay)
    {
       this.dayNumber = dayNumber;
       this.monthNumber = monthNumber;
       this.year = year;
       this.isWorkDay = isWorkDay;

       BEGIN_OF_DAY = getCurrentDayWithHourAndMin(
          AppConfiguartion.getInstance().getBeginDayHour(),
          AppConfiguartion.getInstance().getBeginDayMin()).toDateTime();
       END_OF_DAY = getCurrentDayWithHourAndMin(
          AppConfiguartion.getInstance().getEndDayHour(),
          AppConfiguartion.getInstance().getEndDayMin()).toDateTime();
       LUNCH_WINDOW_BEGIN = getCurrentDayWithHourAndMin(
          AppConfiguartion.getInstance().getLunchWindowBeginHr(),
          0).toDateTime();
       LUNCH_WINDOW_END = getCurrentDayWithHourAndMin(
          AppConfiguartion.getInstance().getLunchWindowEndHr(),
          0).toDateTime();
    }

    public EventTask scheduleEventTaskMins(WO wo,
                                           Integer taskTimeMins)
    {
       if (!isWorkDay)
          return null;

       // Round task to closest next 15 mins
       int mod = taskTimeMins % 15;
       if (mod != 0)
          taskTimeMins += (15 - mod);
       Integer roundTime = taskTimeMins;

       // Check for slot available
       Map<DateTime, Integer> slots = getTimeGaps();
       if (slots.values().stream().noneMatch(i -> i >= roundTime))
          return null;

       DateTime start = null;
       for (Map.Entry<DateTime, Integer> entry : slots.entrySet())
       {
          if (entry.getValue() >= roundTime)
          {
             start = entry.getKey();
             break;
          }
       }

       MutableDateTime endTask = new MutableDateTime(start);
       endTask.addMinutes(roundTime);

       EventTask task = new EventTask(wo, start, endTask.toDateTime(), false);


       // If more than 6 hours schedule lunch at site
       if (roundTime >= 360)
       {
//          buildLunchTask(LUNCH_WINDOW_BEGIN);
          // Build in lunch
          task.setLunchBuildIn(true);
          endTask.addMinutes(AppConfiguartion.getInstance().getLunchTime());
          task.setEnd(endTask.toDateTime());
       }
       else if (endTask.isAfter(LUNCH_WINDOW_BEGIN) && endTask.isBefore(LUNCH_WINDOW_END))
       {
          buildLunchTask(endTask.toDateTime());
//          endTask.addMinutes(AppConfiguartion.getInstance().getLunchTime());
//          task.setEnd(endTask.toDateTime());
       }

       addEventTask(task);
       return task;
    }

    /**
     * NOTE - shrink gaps that are not at the beginning of the day to account for
     * drive time.
     * <p>
     */
    public Map<DateTime, Integer> getTimeGaps()
    {
       Map<DateTime, Integer> result = Maps.newHashMap();
       if (eventTaskList.isEmpty())
       {
          // Full day available
          result.put(BEGIN_OF_DAY, 8 * 60);
          return result;
       }

       DateTime currentTime = BEGIN_OF_DAY;

       Iterator<EventTask> iter = eventTaskList.values().iterator();
       while (iter.hasNext())
       {
          EventTask task = iter.next();

          MutableDateTime taskEffectiveStart = new MutableDateTime(task.getStart());
          MutableDateTime taskEffectiveEnd = new MutableDateTime(task.getEnd());
          if (task.isLunch())
             taskEffectiveEnd.addMinutes(AppConfiguartion.getInstance().getDriveTime() / 2);
          else if (!taskEffectiveStart.isEqual(BEGIN_OF_DAY))
             taskEffectiveStart.addMinutes(-AppConfiguartion.getInstance().getDriveTime());
          else if (taskEffectiveStart.isEqual(BEGIN_OF_DAY))
             taskEffectiveEnd.addMinutes(AppConfiguartion.getInstance().getDriveTime());

          Integer currentGapMin = (int) (taskEffectiveStart.getMillis() - currentTime.getMillis()) / 1000 / 60;
          if (currentGapMin > 0)
             result.put(currentTime, currentGapMin);
          currentTime = taskEffectiveEnd.toDateTime();

          // End of Day
          if (!iter.hasNext())
          {
             if (!task.isLunch() && !task.getStart().isEqual(BEGIN_OF_DAY))
                taskEffectiveEnd.addMinutes(AppConfiguartion.getInstance().getDriveTime());
             currentGapMin = (int) (END_OF_DAY.getMillis() - taskEffectiveEnd.getMillis()) / 1000 / 60;
             if (currentGapMin > 0)
                result.put(taskEffectiveEnd.toDateTime(), currentGapMin);
          }
       }

       return result;
    }

    protected EventTask buildLunchTask(DateTime startLunch)
    {
       if (eventTaskList.values().stream().anyMatch(EventTask::isLunch))
          return null;

       MutableDateTime endLunch = new MutableDateTime(startLunch);
       endLunch.addMinutes(AppConfiguartion.getInstance().getLunchTime());

       EventTask lunch = new EventTask(null, startLunch, endLunch.toDateTime(), true);
       addEventTask(lunch);
       return lunch;
    }

    public Integer getDayNumber()
    {
       return dayNumber;
    }

    public void setDayNumber(Integer dayNumber)
    {
       this.dayNumber = dayNumber;
    }

    public Integer getMonthNumber()
    {
       return monthNumber;
    }

    public void setMonthNumber(Integer monthNumber)
    {
       this.monthNumber = monthNumber;
    }

    public void addEventTask(EventTask eventTask)
    {
       // TODO some basic error handling to make sure the event fits with no overlap!

       Float key = Float.parseFloat(eventTask.getStart().getHourOfDay() + "." +
                                    String.format("%02d", eventTask.getStart().getMinuteOfHour()));
       if (eventTaskList.containsKey(key))
          log.error("This should never happen but we have the same key in the map " + key + " day: " + dayNumber);

       eventTaskList.put(key, eventTask);
    }

    public Map<Float, EventTask> getEventTaskList()
    {
       return eventTaskList;
    }

    public void setEventTaskList(TreeMap<Float, EventTask> eventTaskList)
    {
       this.eventTaskList = eventTaskList;
    }

    public boolean isWorkDay()
    {
       return isWorkDay;
    }

    public void setWorkDay(boolean workDay)
    {
       isWorkDay = workDay;
    }

    public Integer getYear()
    {
       return year;
    }

    public void setYear(Integer year)
    {
       this.year = year;
    }

    public boolean isCleared()
    {
       return isCleared;
    }

    public void setCleared(boolean cleared)
    {
       isCleared = cleared;
    }

    //////////////////////////////////
    // Helpers
    //////////////////////////////////

    protected MutableDateTime getCurrentDayWithHourAndMin(Integer hour,
                                                          Integer min)
    {
       MutableDateTime date = getCurrentDay();
       date.setHourOfDay(hour);
       date.setMinuteOfHour(min);
       return date;
    }

    protected MutableDateTime getCurrentDay()
    {
       MutableDateTime date = new MutableDateTime();
       date.setYear(year);
       date.setMonthOfYear(monthNumber);
       date.setDayOfMonth(dayNumber);
       date.setHourOfDay(0);
       date.setMinuteOfHour(0);
       date.setSecondOfMinute(0);
       date.setMillisOfSecond(0);

       return date;
    }

    protected boolean lunchAlreadyScheduled()
    {
       return !eventTaskList.values().stream().filter(EventTask::isLunch).collect(Collectors.toList()).isEmpty();
    }

    @Override
    public boolean equals(Object o)
    {
       if (this == o) return true;
       if (o == null || getClass() != o.getClass()) return false;
       Day day = (Day) o;
       return Objects.equals(dayNumber, day.dayNumber) &&
              Objects.equals(monthNumber, day.monthNumber) &&
              Objects.equals(year, day.year);
    }

    @Override
    public int hashCode()
    {

       return Objects.hash(dayNumber, monthNumber, year);
    }
 }
