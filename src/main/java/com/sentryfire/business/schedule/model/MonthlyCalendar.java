 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      MonthlyCalendar.java
  * Created:   7/19/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.model;

 import java.util.Map;
 import java.util.TreeMap;

 import org.joda.time.DateTime;
 import org.joda.time.DateTimeConstants;
 import org.joda.time.MutableDateTime;

 public class MonthlyCalendar
 {
    private Integer monthNumber;
    private Integer year;
    private String tech;

    private TreeMap<Integer, Day> calendarDays = new TreeMap<>();

    public MonthlyCalendar(DateTime monthStart,
                           String tech)
    {
       this.monthNumber = monthStart.getMonthOfYear();
       this.year = monthStart.getYear();
       this.tech = tech;
       populateMonthlyCalendar(monthStart);
    }


    ///// Public Business

    /**
     * Will return null when the calendar is full for this tech
     */
    public EventTask getNextAvailableSlot()
    {
       for (Day day : calendarDays.values())
       {
          if (day.isWorkDay())
          {
             EventTask nextSlot = day.getNextAvailableEventTask();
             if (nextSlot == null)
             {
                // Day is full go to next working day.
                continue;
             }
             return nextSlot;
          }
       }

       return null;
    }

    public Integer getMonthNumber()
    {
       return monthNumber;
    }

    public void setMonthNumber(Integer monthNumber)
    {
       this.monthNumber = monthNumber;
    }

    public Map<Integer, Day> getCalendarDays()
    {
       return calendarDays;
    }

    public void setCalendarDays(TreeMap<Integer, Day> calendarDays)
    {
       this.calendarDays = calendarDays;
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
          calendarDays.put(i, day);
          current.addDays(1);
       }
    }

    public String printCalendar()
    {
       StringBuffer buffer = new StringBuffer();
       buffer.append("======= Monthly Calendar For [").append(tech).append("], Month [").append(monthNumber).
          append(", Year [").append(year).append(" ========\n\n");
       for (Day day : calendarDays.values())
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
                else
                {
                   buffer.append("\t\tTask: ").append(task.getStart()).append("\t").append(task.getEnd()).append("\t");
                   if (task.getWo() == null)
                      buffer.append("NULL WO\n");
                   else
                      buffer.append(task.getWo().getNAME()).append("\n");
                }
             }
          }
       }

       return buffer.toString();
    }


    @Override
    public String toString()
    {
       return "MonthlyCalendar{" +
              "monthNumber=" + monthNumber +
              ", year=" + year +
              ", tech='" + tech + '\'' +
              ", calendarDays=" + calendarDays +
              '}';
    }
 }
