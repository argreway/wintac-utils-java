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
 import java.util.TreeMap;
 import java.util.stream.Collectors;

 import com.sentryfire.config.AppConfiguartion;
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

    private DateTime END_OF_DAY;
    private DateTime LUNCH_WINDOW_BEGIN;
    private DateTime LUNCH_WINDOW_END;

    // Can be used for vacation or weekends
    private boolean isWorkDay = false;

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

    /**
     * Get the next available time window that is greater than N minutes.
     * Schedule lunch break if possible.
     */
    public EventTask getNextAvailableEventTask()
    {
       Long minSlotGap = Integer.toUnsignedLong(AppConfiguartion.getInstance().getEmptyTimeSlotMinimumMin());

       MutableDateTime start = getCurrentDayWithHourAndMin(
          AppConfiguartion.getInstance().getBeginDayHour(),
          AppConfiguartion.getInstance().getBeginDayMin());

       if (eventTaskList.isEmpty())
       {
          MutableDateTime end = new MutableDateTime(LUNCH_WINDOW_END);
          end.addMinutes(-AppConfiguartion.getInstance().getLunchTime());
          return new EventTask(null, start.toDateTime(), end.toDateTime(), false);
       }

       Iterator<EventTask> itr = eventTaskList.values().iterator();
       EventTask currentTask = itr.next();
       EventTask futureTask = null;

       while (currentTask != null)
       {
          if (itr.hasNext())
          {
             futureTask = itr.next();
          }

          // No more tasks for the day
          if (futureTask == null)
          {
             // Check if we need to schedule a lunch
             if (lunchAlreadyScheduled())
             {
                Long gapInMinutes = (END_OF_DAY.getMillis() - currentTask.getEnd().getMillis()) / 1000 / 60;
                if (gapInMinutes < minSlotGap)
                   return null;
                // We must be past the lunch event so we have the rest of the day to schedule
                return new EventTask(null, currentTask.getEnd(), END_OF_DAY, false);
             }
             else
             {
                if (LUNCH_WINDOW_BEGIN.isBefore(currentTask.getEnd()) && LUNCH_WINDOW_END.isAfter(currentTask.getEnd()))
                {
                   DateTime startLunch = currentTask.getEnd();
                   MutableDateTime endLunch = new MutableDateTime(startLunch);
                   endLunch.addMinutes(AppConfiguartion.getInstance().getLunchTime());

                   EventTask lunch = new EventTask(null, startLunch, endLunch.toDateTime(), true);
                   addEventTask(lunch);
                   currentTask = lunch;
                   futureTask = null;
                   continue;
                }
                else if (LUNCH_WINDOW_BEGIN.isAfter(currentTask.getEnd()))
                {
                   // We have an available block up until the end of the lunch window minus lunch time
                   MutableDateTime endSlot = new MutableDateTime(LUNCH_WINDOW_END);
                   endSlot.addMinutes(-AppConfiguartion.getInstance().getLunchTime());
                   return new EventTask(null, currentTask.getEnd(), endSlot.toDateTime(), false);
                }
                else
                {
                   Long gapInMinutes = (END_OF_DAY.getMillis() - currentTask.getEnd().getMillis()) / 1000 / 60;
                   if (gapInMinutes < minSlotGap)
                      return null;
                   // We are past lunch just schedule to the end of the day
                   return new EventTask(null, currentTask.getEnd(), END_OF_DAY, false);
                }
             }
          }
          else
          {
             // Check for available gaps
             MutableDateTime nextSlotBegin = new MutableDateTime(currentTask.getEnd());
             MutableDateTime nextSlotEnd = new MutableDateTime(futureTask.getStart());

             Long gapInMinutes = (nextSlotEnd.getMillis() - nextSlotBegin.getMillis()) / 1000 / 60;

             if (gapInMinutes < minSlotGap)
             {
                // Can't do anything interesting move on to the next slot
                currentTask = futureTask;
                futureTask = null;
                continue;
             }
             else
             {
                if (lunchAlreadyScheduled())
                {
                   // If we have a lunch scheduled already just try to fill the whole block
                   return new EventTask(null, nextSlotBegin.toDateTime(), nextSlotEnd.toDateTime(), false);
                }
                else
                {
                   if (LUNCH_WINDOW_BEGIN.isBefore(nextSlotEnd) && LUNCH_WINDOW_END.isAfter(nextSlotEnd))
                   {
                      DateTime startLunch = nextSlotBegin.toDateTime();
                      MutableDateTime endLunch = new MutableDateTime(startLunch);
                      endLunch.addMinutes(AppConfiguartion.getInstance().getLunchTime());

                      EventTask lunch = new EventTask(null, startLunch, endLunch.toDateTime(), true);
                      addEventTask(lunch);
                      currentTask = lunch;
                      futureTask = null;
                      continue;
                   }
                   else if (LUNCH_WINDOW_BEGIN.isAfter(nextSlotBegin))
                   {
                      // We have an available block up until the end of the lunch window minus lunch time
                      MutableDateTime endSlot = new MutableDateTime(LUNCH_WINDOW_END);
                      endSlot.addMinutes(-AppConfiguartion.getInstance().getLunchTime());
                      return new EventTask(null, nextSlotBegin.toDateTime(), endSlot.toDateTime(), false);
                   }
                   else
                   {
                      // Just schedule the whole block
                      return new EventTask(null, nextSlotBegin.toDateTime(), nextSlotEnd.toDateTime(), false);
                   }
                }
             }
          }
       }

       // Return null if no slot available;
       return null;
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
          log.error("This should never happen but we have the same key in the map " + key);

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

       return date;
    }

    protected boolean lunchAlreadyScheduled()
    {
       return !eventTaskList.values().stream().filter(EventTask::isLunch).collect(Collectors.toList()).isEmpty();
    }
 }
