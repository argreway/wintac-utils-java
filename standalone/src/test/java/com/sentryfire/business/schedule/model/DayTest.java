 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      DayTest.java
  * Created:   7/27/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.model;

 import java.util.Map;

 import org.joda.time.DateTime;
 import org.joda.time.MutableDateTime;
 import org.testng.Assert;
 import org.testng.annotations.Test;

 public class DayTest
 {
    @Test
    public void testGetTimeGaps()
    {
       System.out.println("Test testGetTimeGaps.");

       Day day = new Day(1, 7, 2018, true);

       MutableDateTime timeStart = new MutableDateTime(day.BEGIN_OF_DAY);
       MutableDateTime timeEnd = new MutableDateTime(day.BEGIN_OF_DAY);
       timeEnd.addMinutes(30);
       // 7:30-8
       EventTask task = new EventTask(null, timeStart.toDateTime(), timeEnd.toDateTime(), false);
       day.addEventTask(task);
       timeStart.addMinutes(30);
       timeEnd.addMinutes(30);
       // 8 - 8:30
       task = new EventTask(null, timeStart.toDateTime(), timeEnd.toDateTime(), false);
       day.addEventTask(task);

       Map<DateTime, Integer> result = day.getTimeGaps();

       Assert.assertEquals(result.size(), 1, "Should be 1 gap from 9 to 4:00");
       Assert.assertEquals((int) result.entrySet().iterator().next().getValue(), 420, "Should be a 420 minute gap from 9 to 4:00");
    }

    @Test
    public void testGetTimeGapsBeginDay()
    {
       System.out.println("Test testGetTimeGaps beginning of day.");

       Day day = new Day(1, 7, 2018, true);

       MutableDateTime timeStart = new MutableDateTime(day.BEGIN_OF_DAY);
       MutableDateTime timeEnd = new MutableDateTime(day.BEGIN_OF_DAY);
       timeEnd.addMinutes(30);
       // 7:30-8
       EventTask task = new EventTask(null, timeStart.toDateTime(), timeEnd.toDateTime(), false);
       day.addEventTask(task);
       timeStart.addMinutes(30);
       timeEnd.addMinutes(30);
       // 8 - 8:30

       Map<DateTime, Integer> result = day.getTimeGaps();

       Assert.assertEquals(result.size(), 1, "Should be 1 gap from 9 to 4:00");
       assertGap(result, timeEnd.toDateTime(), 450);
    }

    @Test
    public void testGetTimeGapsEventMiddleDay()
    {
       System.out.println("Test testGetTimeGaps beginning of day.");

       Day day = new Day(1, 7, 2018, true);

       MutableDateTime timeStart = new MutableDateTime(day.BEGIN_OF_DAY);
       timeStart.setHourOfDay(12);
       timeStart.setMinuteOfHour(0);
       timeStart.setSecondOfMinute(0);

       MutableDateTime timeEnd = new MutableDateTime(timeStart);
       timeEnd.addMinutes(60);
       // 12:00-1
       EventTask task = new EventTask(null, timeStart.toDateTime(), timeEnd.toDateTime(), false);
       day.addEventTask(task);

       MutableDateTime testStart = new MutableDateTime(day.BEGIN_OF_DAY);
       // 730 - 11:300

       Map<DateTime, Integer> result = day.getTimeGaps();

       Assert.assertEquals(result.size(), 2, "Should be 2 gaps from 730 to 11:30 and 1:30-4");
       assertGap(result, testStart.toDateTime(), 240);

       testStart.setHourOfDay(13);
       testStart.setMinuteOfHour(30);
       assertGap(result, testStart.toDateTime(), 150);
    }


    @Test
    public void testGetTimeGapsEventsAtBeingThruEnd()
    {
       System.out.println("Test testGetTimeGaps beginning of day and end day full.");

       Day day = new Day(1, 7, 2018, true);

       MutableDateTime timeStart = new MutableDateTime(day.BEGIN_OF_DAY);
       timeStart.setHourOfDay(12);
       timeStart.setMinuteOfHour(0);
       timeStart.setSecondOfMinute(0);

       MutableDateTime timeEnd = new MutableDateTime(timeStart);
       timeEnd.setHourOfDay(16);
       timeEnd.setMinuteOfHour(0);

       // 12:00-4
       EventTask task = new EventTask(null, timeStart.toDateTime(), timeEnd.toDateTime(), false);
       day.addEventTask(task);

       // 730-8
       timeStart.setHourOfDay(7);
       timeStart.setMinuteOfHour(30);
       timeEnd.setHourOfDay(8);
       timeEnd.setMinuteOfHour(0);
       task = new EventTask(null, timeStart.toDateTime(), timeEnd.toDateTime(), false);
       day.addEventTask(task);

       // Test Vars Should be Gap of
       // 8:30 - 11:300
       MutableDateTime testStart = new MutableDateTime(day.BEGIN_OF_DAY);
       testStart.setHourOfDay(8);
       testStart.setMinuteOfHour(30);

       Map<DateTime, Integer> result = day.getTimeGaps();

       Assert.assertEquals(result.size(), 1, "Should be 1 gap from 830 to 11:30");
       assertGap(result, testStart.toDateTime(), 180);

    }

    //////////////////////////////////////
    // Helpers
    //////////////////////////////////////
    protected void assertGap(Map<DateTime, Integer> result,
                             DateTime start,
                             Integer mins)
    {
       for (Map.Entry<DateTime, Integer> entry : result.entrySet())
       {
          if (entry.getKey().equals(start) && entry.getValue().equals(mins))
          {
             Assert.assertTrue(true, "Found " + start + " - " + mins);
             return;
          }
       }
       Assert.assertTrue(false, "Could not find gap " + start + " - " + mins);
    }
 }
