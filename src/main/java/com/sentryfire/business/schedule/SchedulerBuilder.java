 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      SchedulerBuilder.java
  * Created:   7/16/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule;

 import java.util.List;
 import java.util.stream.Collectors;

 import com.google.api.client.util.DateTime;
 import com.google.api.services.calendar.model.Event;
 import com.google.common.collect.Lists;
 import com.sentryfire.SentryAppConfiguartion;
 import com.sentryfire.business.schedule.googlecalendar.CalendarManager;
 import com.sentryfire.business.schedule.googlecalendar.CalenderUtils;
 import com.sentryfire.business.schedule.googlemaps.GoogleMapsClient;
 import com.sentryfire.model.ItemStatHolder;
 import com.sentryfire.model.WO;
 import org.joda.time.DateTimeConstants;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class SchedulerBuilder
 {
    private Logger log = LoggerFactory.getLogger(getClass());

    protected static CalendarManager calendarManager = CalendarManager.getInstance();

    public void buildAndInsertAllSchedules()
    {
       // Google Maps stuff
       GoogleMapsClient googleMapsClient = new GoogleMapsClient();
       List<WO> woList = googleMapsClient.route();

       List<WO> denver = woList.stream().filter(w -> w.getDEPT().equals("DENVER")).collect(Collectors.toList());
       List<WO> greeley = woList.stream().filter(w -> w.getDEPT().equals("GREELEY")).collect(Collectors.toList());
       List<WO> cosprings = woList.stream().filter(w -> w.getDEPT().equals("CO_SPRINGS")).collect(Collectors.toList());

       List<Event> calendarEvents = buildSchedule(cosprings);
       calendarManager.bulkAddEvents(calendarEvents, CalendarManager.CAL_NAME_FIP);
       calendarEvents = buildSchedule(greeley);
       calendarManager.bulkAddEvents(calendarEvents, CalendarManager.CAL_NAME_GREELEY);
       calendarEvents = buildSchedule(denver);
       calendarManager.bulkAddEvents(calendarEvents, CalendarManager.CAL_NAME_DENVER);
    }

    public List<Event> buildSchedule(List<WO> woList)
    {
       List<Event> events = Lists.newArrayList();
       try
       {
          // Start Date/Time
          MutableDateTime startDt = new MutableDateTime();
          startDt.setMonthOfYear(7);
          startDt.setHourOfDay(SentryAppConfiguartion.getInstance().getBeginDayHour());
          startDt.setMinuteOfHour(SentryAppConfiguartion.getInstance().getBeginDayMin());

          MutableDateTime endDt = new MutableDateTime(startDt);

          for (WO wo : woList)
          {

             Integer workTimeMins = wo.getMetaData().getItemStatHolderList().stream().mapToInt(ItemStatHolder::getMin).sum();

             if (workTimeMins <= 0)
             {
                log.error("WO has 0 workTime - jobNumber: " + wo.getIN2());
                // Still schedule them for now so they show in calendar and are not forgotten
//                continue;
             }

             // Don't schedule past 4 or on the weekend
             if (startDt.getDayOfWeek() == DateTimeConstants.SATURDAY || startDt.getDayOfWeek() == DateTimeConstants.SUNDAY)
             {
                startDt.setDayOfWeek(DateTimeConstants.MONDAY);
                endDt.setDayOfWeek(DateTimeConstants.MONDAY);
             }
             if (startDt.getHourOfDay() + (workTimeMins / 60.0) > SentryAppConfiguartion.getInstance().getEndDayHour())
             {
                startDt.setHourOfDay(SentryAppConfiguartion.getInstance().getBeginDayHour());
                startDt.setMinuteOfHour(SentryAppConfiguartion.getInstance().getBeginDayMin());
                startDt.addDays(1);
                endDt.setHourOfDay(SentryAppConfiguartion.getInstance().getBeginDayHour());
                endDt.setMinuteOfHour(SentryAppConfiguartion.getInstance().getBeginDayMin());
                endDt.addDays(1);
             }

             endDt.addMinutes(workTimeMins);

             List<String> itemDesc = wo.getMetaData().getItemStatHolderList().stream().map(
                i -> i.getItemCode() + ":" + i.getCount()).collect(Collectors.toList());

             DateTime start = new DateTime(startDt.toDate());
             DateTime end = new DateTime(endDt.toDate());
             List<String> people = Lists.newArrayList("tony.greway@sentryfire.net");
             Event event = CalenderUtils.createEvent(wo.getNAME() + " : " + wo.getCN() + "-" + wo.getIN2(),
                                                     wo.getADR1() + " " + wo.getCITY() + " " + wo.getZIP(), String.join(",", itemDesc),
                                                     start, end, null, people, SentryAppConfiguartion.getInstance().getCalReminderMin(),
                                                     null);

             events.add(event);

             // Add Drive time
             startDt.addMinutes(workTimeMins);
             startDt.addMinutes(SentryAppConfiguartion.getInstance().getDriveTime());
             endDt.addMinutes(SentryAppConfiguartion.getInstance().getDriveTime());
          }

       }
       catch (Exception e)
       {
          log.error("Failed to build schedule due to: ", e);
       }

       events.forEach(e -> log.info(e.getStart() + " : " + e.getEnd() + ": " + e.getSummary()));
       return events;
    }
 }
