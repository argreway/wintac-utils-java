 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      MonthlyCalendar.java
  * Created:   7/19/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.model;

 import java.util.List;
 import java.util.Map;
 import java.util.Objects;
 import java.util.Set;
 import java.util.TreeMap;
 import java.util.stream.Collectors;

 import com.google.api.services.calendar.model.Event;
 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.google.common.collect.Sets;
 import com.sentryfire.business.schedule.googlecalendar.CalenderUtils;
 import com.sentryfire.model.ItemStatHolder;
 import com.sentryfire.model.WO;
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeConstants;
 import org.joda.time.MutableDateTime;
 import org.joda.time.format.DateTimeFormat;
 import org.joda.time.format.DateTimeFormatter;

 public class MonthlyCalendar
 {
    private Integer monthNumber;
    private Integer year;
    private String tech;

    // Actual Master Calendar
    private TreeMap<Integer, Day> masterCalendar = new TreeMap<>();

    protected DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");

    // Helper maps
    private Map<String, Set<Day>> daysAtLocation = Maps.newHashMap();
    private List<Day> freeDays = Lists.newArrayList();

    public MonthlyCalendar(DateTime monthStart,
                           String tech)
    {
       this.monthNumber = monthStart.getMonthOfYear();
       this.year = monthStart.getYear();
       this.tech = tech;
       populateMonthlyCalendar(monthStart);
    }

    protected static final double DAY_IN_MINS_DBL = 8.0 * 60.0;
    protected static final int DAY_IN_MINS = 8 * 60;

    //////////////////////////////////////////
    // Public Business
    //////////////////////////////////////////

    /**
     * Will return null if calendar can not find a slot or is full
     */
    public EventTask scheduleWorkOrder(WO wo)
    {

       return scheduleWorkOrder(wo, null);
    }

    public EventTask scheduleWorkOrder(WO wo,
                                       DateTime scheduledStart)
    {
       Integer timeForTechsItems = wo.getMetaData().getItemStatHolderList().stream().
          filter(i -> tech.equals(i.getTech())).mapToInt(ItemStatHolder::getMin).sum();

       if (scheduledStart != null)
       {
          Day d = masterCalendar.get(scheduledStart.getDayOfMonth());
          MutableDateTime endTime = new MutableDateTime(scheduledStart);
          endTime.addMinutes(timeForTechsItems);

          EventTask scheduledTask = new EventTask(wo, scheduledStart, endTime.toDateTime(), false);
          scheduledTask.setEnd(endTime.toDateTime());
          d.addEventTask(scheduledTask);
          updateMaps(scheduledTask, d, wo);
          return scheduledTask;
       }

       // More than one day of work
       double daysOfWork = ((double) timeForTechsItems) / (DAY_IN_MINS_DBL);
       if (daysOfWork >= 1.0)
       {
          return scheduleMultipleDayJob(wo, timeForTechsItems);
       }

       // Otherwise find next best free slot
       return scheduleDailyTask(wo, timeForTechsItems);
    }

    public void insertEventList(List<Event> events,
                                Map<String, WO> in2ToWO)
    {
       List<EventTask> eventTaskList = CalenderUtils.eventsToEventTaskList(events, in2ToWO);
       for (EventTask task : eventTaskList)
       {
          Day d = masterCalendar.get(task.getStart().getDayOfMonth());
          d.addEventTask(task);
          updateMaps(task, d, task.getWo());
       }
    }

    //////////////////////////////////////////
    // Private Business Helpers
    //////////////////////////////////////////

    private EventTask scheduleDailyTask(WO wo,
                                        Integer timeForTechsItems)
    {
       EventTask scheduledTask;
       // Find days with free slots where we are already close to that location or
       // schedule location to a new free day if available
       Set<Day> locationDays = daysAtLocation.get(wo.getCITY());
       if (locationDays == null)
          locationDays = Sets.newHashSet();

       List<Day> locationDaysToTry = Lists.newArrayList(locationDays);
       if (!freeDays.isEmpty())
          locationDaysToTry.add(freeDays.get(0));

       for (Day d : locationDaysToTry)
       {
          scheduledTask = d.scheduleEventTaskMins(wo, timeForTechsItems);
          if (scheduledTask != null)
          {
             updateMaps(scheduledTask, d, wo);
             return scheduledTask;
          }
       }

       // Otherwise find next best free slot anywhere in the calendar
       for (Day d : masterCalendar.values())
       {
          scheduledTask = d.scheduleEventTaskMins(wo, timeForTechsItems);
          if (scheduledTask != null)
          {
             updateMaps(scheduledTask, d, wo);
             return scheduledTask;
          }
       }

       // Could not schedule it
       return null;
    }

    // TODO Make this better by only looking for consecutive hours that can span multiple days instead
    // TODO of just completely free days.
    private EventTask scheduleMultipleDayJob(WO wo,
                                             Integer timeForTechsItems)
    {
       Integer daysNeeded = (int) Math.ceil(((double) timeForTechsItems) / (DAY_IN_MINS_DBL));

       List<Day> consecutiveDays = getConsecutiveDays(daysNeeded);
       if (consecutiveDays == null)
          return null;

       // Schedule full day wo, lunch, and into next days as needed
       EventTask result = null;
       Integer timeLeft = timeForTechsItems;
       for (Day d : consecutiveDays)
       {
          if (timeLeft >= DAY_IN_MINS)
          {
             result = d.scheduleEventTaskMins(wo, DAY_IN_MINS);
             timeLeft -= DAY_IN_MINS;
          }
          else
             result = d.scheduleEventTaskMins(wo, timeLeft);

          updateMaps(result, d, wo);
       }
       return result;
    }

    public List<EventTask> getAllEventTasks(boolean includeLunch)
    {
       List<EventTask> result = Lists.newArrayList();

       for (Day d : masterCalendar.values())
       {
          if (includeLunch)
             result.addAll(d.getEventTaskList().values());
          else
             result.addAll(d.getEventTaskList().values().stream().filter(e -> !e.isLunch()).collect(Collectors.toList()));
       }
       return result;
    }

    //////////////////////////////////////////
    // Getter/Setters
    //////////////////////////////////////////

    public Integer getMonthNumber()
    {
       return monthNumber;
    }

    public void setMonthNumber(Integer monthNumber)
    {
       this.monthNumber = monthNumber;
    }

    public Map<Integer, Day> getMasterCalendar()
    {
       return masterCalendar;
    }

    public void setMasterCalendar(TreeMap<Integer, Day> masterCalendar)
    {
       this.masterCalendar = masterCalendar;
    }

    public Integer getYear()
    {
       return year;
    }

    public void setYear(Integer year)
    {
       this.year = year;
    }

    public String getTech()
    {
       return tech;
    }

    public void setTech(String tech)
    {
       this.tech = tech;
    }

    //////////////////////////////////////////
    // Helpers
    //////////////////////////////////////////

    protected void updateMaps(EventTask scheduledTask,
                              Day d,
                              WO wo)
    {
       if (scheduledTask == null)
          return;

       freeDays.remove(d);

       if (wo != null)
       {
          Set<Day> atLoc = daysAtLocation.get(scheduledTask.getWo().getCITY());
          if (atLoc == null)
          {
             atLoc = Sets.newHashSet();
             daysAtLocation.put(scheduledTask.getWo().getCITY(), atLoc);
          }
          atLoc.add(d);

          // Add Event to Items that were scheduled
          List<ItemStatHolder> techItems = wo.getMetaData().getItemStatHolderList().stream().filter(
             i -> i.getTech() != null && i.getTech().equals(tech) && i.getScheduledStart() == null).collect(Collectors.toList());
          techItems.forEach(i -> i.setScheduledStart(scheduledTask.getStart()));
       }
    }

    protected void populateMonthlyCalendar(DateTime start)
    {
       MutableDateTime current = new MutableDateTime(start);
       current.setDayOfMonth(1);

       for (int i = 1; i <= current.dayOfMonth().getMaximumValue(); i++)
       {
          boolean workDay = true;
          if (current.getDayOfWeek() == DateTimeConstants.SATURDAY || current.getDayOfWeek() == DateTimeConstants.SUNDAY)
             workDay = false;

          Day day = new Day(i, monthNumber, year, workDay);
          masterCalendar.put(i, day);
          if (workDay)
             freeDays.add(day);
          current.addDays(1);
       }
    }

    protected Long getCompletedWO()
    {
       Long total = 0L;
       for (Day d : masterCalendar.values())
       {
          total += d.getEventTaskList().values().stream().filter(e -> e.getWo() != null).count();
       }
       return total;
    }

    public String printCalendar(boolean shortVersion)
    {
       StringBuffer buffer = new StringBuffer();
       buffer.append("\n======= Monthly Calendar For [").append(tech).append("], Month [").append(monthNumber).
          append("], Year [").append(year).append("], WO Completed [").append(getCompletedWO()).append("] ========\n");
       if (shortVersion)
          return buffer.toString();

       for (Day day : masterCalendar.values())
       {
          buffer.append("Day [").append(day.getDayNumber()).append("]");
          if (!day.isWorkDay())
             buffer.append("  -> NOT A WORK DAY\n");
          else
          {
             buffer.append("  -> SCHEDULED\n");
             for (EventTask task : day.getEventTaskList().values())
             {
                if (task.isLunch())
                   buffer.append("\t\tTask: LUNCH\n");
                else if (task.isFree())
                   buffer.append("\t\tTask: FREE\n");
                else
                {
                   buffer.append("\t\tTask: ").append(formatter.print(task.getStart())).append("\t").
                      append(formatter.print(task.getEnd())).append("\t");
                   if (task.getWo() == null)
                      buffer.append("NULL WO\n");
                   else
                   {
                      buffer.append(task.getWo().getNAME()).append("\t").append(task.getWo().getADR1()).append("\t").
                         append(task.getWo().getCITY()).append("\n");
                      if (task.isLunchBuildIn())
                         buffer.append("\t\t\t-- LUNCH BUILT IN + 30min\n");
                   }
                }
             }
          }
       }

       return buffer.toString();
    }

    protected List<Day> getConsecutiveDays(Integer daysNeeded)
    {
       if (freeDays.isEmpty() || freeDays.size() < daysNeeded)
          return null;

       List<Day> consecutiveDays = Lists.newArrayList();

       Integer nextDay = null;
       for (Day d : freeDays)
       {
          if (nextDay == null || d.getDayNumber().equals(nextDay))
          {
             nextDay = d.getDayNumber() + 1;
             consecutiveDays.add(d);
          }
          else if (d.getDayNumber().equals(nextDay))
          {
             consecutiveDays.clear();
             nextDay = d.getDayNumber() + 1;
             consecutiveDays.add(d);
          }

          if (consecutiveDays.size() == daysNeeded)
             return consecutiveDays;

       }
       return null;
    }

    //////////////////////////////////////////
    // Overrides
    //////////////////////////////////////////

    @Override
    public String toString()
    {
       return "MonthlyCalendar{" +
              "monthNumber=" + monthNumber +
              ", year=" + year +
              ", tech='" + tech + '\'' +
              ", masterCalendar=" + masterCalendar +
              '}';
    }

    @Override
    public boolean equals(Object o)
    {
       if (this == o) return true;
       if (o == null || getClass() != o.getClass()) return false;
       MonthlyCalendar calendar = (MonthlyCalendar) o;
       return Objects.equals(monthNumber, calendar.monthNumber) &&
              Objects.equals(year, calendar.year);
    }

    @Override
    public int hashCode()
    {

       return Objects.hash(monthNumber, year);
    }
 }
