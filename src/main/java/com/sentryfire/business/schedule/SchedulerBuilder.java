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
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.api.services.calendar.model.Event;
import com.google.common.collect.Lists;
import com.sentryfire.business.schedule.googlecalendar.CalendarManager;
import com.sentryfire.business.schedule.googlemaps.GoogleMapsClient;
import com.sentryfire.business.schedule.model.Day;
import com.sentryfire.business.schedule.model.EventTask;
import com.sentryfire.business.schedule.model.MonthlyCalendar;
import com.sentryfire.business.schedule.model.ScheduleCalendar;
import com.sentryfire.config.TechProfile;
import com.sentryfire.config.TechProfileConfiguration;
import com.sentryfire.model.ItemStatHolder;
import com.sentryfire.model.SKILL;
import com.sentryfire.model.WO;
import org.joda.time.MutableDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class SchedulerBuilder
 {
    private Logger log = LoggerFactory.getLogger(getClass());

    protected static CalendarManager calendarManager = CalendarManager.getInstance();

    public void buildAndInsertAllSchedules(org.joda.time.DateTime start)
    {
       // Google Maps stuff
       GoogleMapsClient googleMapsClient = new GoogleMapsClient();
       List<WO> rawList = googleMapsClient.route(start);

       // Filter out WO that have no known work
       List<WO> woList = rawList.stream().filter(w -> !isMonitoringMonthlyOnly(w)).collect(Collectors.toList());

       List<WO> denver = woList.stream().filter(w -> w.getDEPT().equals("DENVER")).collect(Collectors.toList());
       List<WO> greeley = woList.stream().filter(w -> w.getDEPT().equals("GREELEY")).collect(Collectors.toList());
       List<WO> cosprings = woList.stream().filter(w -> w.getDEPT().equals("CO_SPRINGS")).collect(Collectors.toList());

       ScheduleCalendar denCalendar = buildSchedule(TechProfileConfiguration.getInstance().getDenTechToProfiles(), denver, start);
       ScheduleCalendar greCalendar = buildSchedule(TechProfileConfiguration.getInstance().getGreTechToProfiles(), greeley, start);
       ScheduleCalendar fipCalendar = buildSchedule(TechProfileConfiguration.getInstance().getFipTechToProfiles(), cosprings, start);

       List<Event> calendarEvents = scheduleCalendarToEvents(fipCalendar);
//       calendarManager.bulkAddEvents(calendarEvents, CalendarManager.CAL_NAME_FIP);
       calendarEvents = scheduleCalendarToEvents(greCalendar);
//       calendarManager.bulkAddEvents(calendarEvents, CalendarManager.CAL_NAME_GREELEY);
       calendarEvents = scheduleCalendarToEvents(denCalendar);
//       calendarManager.bulkAddEvents(calendarEvents, CalendarManager.CAL_NAME_DENVER);
    }

    ////////////////
    // Helpers
    ////////////////

    protected ScheduleCalendar buildSchedule(final Map<String, TechProfile> techToSkill,
                                             final List<WO> woList,
                                             final org.joda.time.DateTime calStart)
    {
       ScheduleCalendar scheduleCalendar = new ScheduleCalendar(techToSkill.keySet(), calStart);

       List<WO> masterMonthList = Lists.newArrayList(woList);

       for (MonthlyCalendar techCal : scheduleCalendar.getTechCalendars().values())
       {
          String tech = techCal.getTech();
          List<SKILL> skills = techToSkill.get(tech).getSkills();
          List<WO> completedWO = scheduleTechForMonth(tech, skills, techCal, masterMonthList);
          masterMonthList.removeAll(completedWO);
       }

       return scheduleCalendar;
    }

    protected List<WO> scheduleTechForMonth(String tech,
                                            List<SKILL> skills,
                                            MonthlyCalendar calendar,
                                            final List<WO> woList)
    {
       List<WO> completedWOList = Lists.newArrayList();

       // Separate by city first
       List<WO> techMasterList = woList.stream().filter(w -> hasRequiredSkills(w, skills)).collect(Collectors.toList());


       List<EventTask> eventTasks = Lists.newArrayList();
       EventTask nextSlot = calendar.getNextAvailableSlot();
       while (nextSlot != null && !techMasterList.isEmpty())
       {

          Day taskDay = calendar.getCalendarDays().get(nextSlot.getDayOfMonth());
          Long gapInMinutes = (nextSlot.getEnd().getMillis() - nextSlot.getStart().getMillis()) / 1000 / 60;
//          gapInMinutes -= AppConfiguartion.getInstance().getDriveTime();

          WO wo = getNextBestWorkOrder(techMasterList, Math.toIntExact(gapInMinutes));
          if (wo == null)
          {
             // No suitable work to match slot - mark free
             nextSlot.setFree(true);
          }
          else
          {
             completedWOList.add(wo);
             techMasterList.remove(wo);

             Integer workTimeMins = wo.getMetaData().getItemStatHolderList().stream().mapToInt(ItemStatHolder::getMin).sum();

             if (workTimeMins <= 0)
             {
                // Still schedule them for now so they show in calendar and are not forgotten
                log.error("WO has 0 workTime - jobNumber: " + wo.getIN2());
             }

             MutableDateTime endTime = new MutableDateTime(nextSlot.getStart());
             endTime.addMinutes(workTimeMins);
             nextSlot.setEnd(endTime.toDateTime());
             nextSlot.setWo(wo);
          }

          taskDay.addEventTask(nextSlot);
          nextSlot = calendar.getNextAvailableSlot();
       }

       log.info("Calendar Complete For Tech [" + tech + "]");
       log.info(calendar.printCalendar());
       eventTasks.forEach(e -> log.info(e.getStart() + " : " + e.getEnd()));
//       events.forEach(e -> log.info(e.getStart() + " : " + e.getEnd() + ": " + e.getSummary()));
//       return events;
       return completedWOList;
    }

    /**
     * We need to be smart about how close they are to each other and the time available.
     *
     * @return null, will return null if there is no more work to be done
     */
    protected WO getNextBestWorkOrder(List<WO> availableWO,
                                      int gapInMinutes)
    {
//       Map<String, List<WO>> cityToWO = availableWO.stream().collect(Collectors.groupingBy(w -> w.getCITY() == null ? "unknown" : w.getCITY(), Collectors.toList()));
       Map<Integer, List<WO>> workTimeToWO = availableWO.stream().collect(Collectors.groupingBy(
          w -> w.getMetaData().getItemStatHolderList().stream().mapToInt(ItemStatHolder::getMin).sum(), Collectors.toList()
       ));

       int currentSelection = 0;
       // Get closest by time gap trying to fill the entire window
       for (Integer size : workTimeToWO.keySet())
       {
          if (size > gapInMinutes)
          {
             continue;
          }
          else if (size == gapInMinutes)
          {
             currentSelection = size;
             break;
          }
          else if (size > currentSelection)
          {
             currentSelection = size;
             continue;
          }
       }

       if (currentSelection < 0 || workTimeToWO.get(currentSelection) == null || workTimeToWO.get(currentSelection).isEmpty())
       {
          log.error("No suitable WO for slot - " + gapInMinutes);
          return null;
       }

       return workTimeToWO.get(currentSelection).get(0);
    }


    // TODO
    // PROBABLY DELETE THIS
    protected ScheduleCalendar buildSchedulePerTech(Map<String, List<SKILL>> techToSkill,
                                                    List<WO> woList,
                                                    org.joda.time.DateTime start)
    {
       // Separate by city first
       Map<String, List<WO>> cityToWO = woList.stream().collect(Collectors.groupingBy(w -> w.getCITY() == null ? "unknown" : w.getCITY(), Collectors.toList()));
       Map<Integer, List<WO>> workTimeToWO = woList.stream().collect(Collectors.groupingBy(
          w -> w.getMetaData().getItemStatHolderList().stream().mapToInt(ItemStatHolder::getMin).sum(), Collectors.toList()
       ));
       Map<Integer, List<WO>> sortedWorkTimeToWO = new TreeMap<>(workTimeToWO);

//       sortedWorkTimeToWO.values().

       System.out.println("Test");

       return null;

    }

    /////////////////////////////////////
    // Helpers
    /////////////////////////////////////

    protected List<Event> scheduleCalendarToEvents(ScheduleCalendar calendar)
    {
       List<Event> events = Lists.newArrayList();

//       List<String> itemDesc = wo.getMetaData().getItemStatHolderList().stream().map(
//          i -> i.getItemCode() + ":" + i.getCount()).collect(Collectors.toList());
//
//       DateTime start = new DateTime(startDt.toDate());
//       DateTime end = new DateTime(endDt.toDate());
//       List<String> people = Lists.newArrayList("tony.greway@sentryfire.net");
//       Event event = CalenderUtils.createEvent(wo.getNAME() + " : " + wo.getCN() + "-" + wo.getIN2(),
//                                               wo.getADR1() + " " + wo.getCITY() + " " + wo.getZIP(), String.join(",", itemDesc),
//                                               start, end, null, people, AppConfiguartion.getInstance().getCalReminderMin(),
//                                               null);
//
//       events.add(event);
//
//       // Add Drive time
//       startDt.addMinutes(workTimeMins);
//       startDt.addMinutes(AppConfiguartion.getInstance().getDriveTime());
//       endDt.addMinutes(AppConfiguartion.getInstance().getDriveTime());
//
       return events;
    }

    protected boolean isMonitoringMonthlyOnly(WO wo)
    {
       if (wo != null)
       {
          for (ItemStatHolder item : wo.getMetaData().getItemStatHolderList())
          {
             if (item.getItemCode().equals("MONITORING") || item.getItemCode().equals("SC")
                 || item.getItemCode().equals("MONTHLY_BILLING_NT"))
                continue;
             else
                return false;
          }
          return true;
       }
       return false;
    }


    protected boolean hasRequiredSkills(WO wo,
                                        List<SKILL> skills)
    {
       for (ItemStatHolder holder : wo.getMetaData().getItemStatHolderList())
       {
          if (skills.contains(holder.getSkill()))
             return true;
       }

       return false;
    }

 }
