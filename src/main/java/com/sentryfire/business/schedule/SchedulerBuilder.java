 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      SchedulerBuilder.java
  * Created:   7/16/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule;

 import java.util.Collection;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.TreeMap;
 import java.util.stream.Collectors;

 import com.google.api.client.util.DateTime;
 import com.google.api.services.calendar.model.Event;
 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.google.common.collect.Sets;
 import com.sentryfire.business.schedule.googlecalendar.CalendarManager;
 import com.sentryfire.business.schedule.googlecalendar.CalenderUtils;
 import com.sentryfire.business.schedule.model.Day;
 import com.sentryfire.business.schedule.model.EventTask;
 import com.sentryfire.business.schedule.model.MonthlyCalendar;
 import com.sentryfire.business.schedule.model.ScheduleCalendar;
 import com.sentryfire.business.utils.SerializerUtils;
 import com.sentryfire.config.AppConfiguartion;
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
//       GoogleMapsClient googleMapsClient = new GoogleMapsClient();
//       List<WO> rawList = googleMapsClient.route(start);
//       SerializerUtils.serializeList(rawList);

       List<WO> rawList = SerializerUtils.deWOSerializeList();
       // Filter out raw list
       List<WO> woList = filterRawWOList(rawList);


       List<WO> denver = woList.stream().filter(w -> w.getDEPT().equals("DENVER")).collect(Collectors.toList());
       List<WO> greeley = woList.stream().filter(w -> w.getDEPT().equals("GREELEY")).collect(Collectors.toList());
       List<WO> cosprings = woList.stream().filter(w -> w.getDEPT().equals("CO_SPRINGS")).collect(Collectors.toList());

       ScheduleCalendar denCalendar = buildSchedule(TechProfileConfiguration.getInstance().getDenTechToProfiles(), denver, start);
//       ScheduleCalendar greCalendar = buildSchedule(TechProfileConfiguration.getInstance().getGreTechToProfiles(), greeley, start);
//       ScheduleCalendar fipCalendar = buildSchedule(TechProfileConfiguration.getInstance().getFipTechToProfiles(), cosprings, start);

       submitCalendarToGoogle(denCalendar);
//       List<Event> calendarEvents = scheduleCalendarToEvents(fipCalendar);
//       calendarManager.bulkAddEvents(calendarEvents, CalendarManager.CAL_NAME_FIP);
//       calendarEvents = scheduleCalendarToEvents(greCalendar);
//       calendarManager.bulkAddEvents(calendarEvents, CalendarManager.CAL_NAME_GREELEY);
    }

    ////////////////
    // Helpers
    ////////////////

    protected ScheduleCalendar buildSchedule(final Map<String, TechProfile> techToProfile,
                                             final List<WO> rawList,
                                             final org.joda.time.DateTime calStart)
    {
       ScheduleCalendar scheduleCalendar = new ScheduleCalendar(techToProfile.keySet(), calStart);


       List<WO> masterMonthList = Lists.newArrayList(rawList);
       Set<WO> completedList = Sets.newHashSet();

       Map<String, List<WO>> distributedWOList = distributeWorkLoad(masterMonthList, techToProfile.values());

       for (MonthlyCalendar techCal : scheduleCalendar.getTechCalendars().values())
       {
          String tech = techCal.getTech();

          List<WO> unScheduledList = distributedWOList.get(tech);
//          List<SKILL> skills = techToProfile.get(tech).getSkills();
//          List<WO> completedWO = scheduleTechForMonth(tech, skills, techCal, distributedWOList.get(tech));
          List<WO> completedWO = scheduleTechForMonth(techCal, distributedWOList.get(tech));


          masterMonthList.removeAll(completedWO);
          unScheduledList.removeAll(completedWO);
          log.info("TECH: [" + tech + "] Total [" + distributedWOList.get(tech).size() + "] Completed WO [" + completedWO.size() + "]" +
                   " NOT Scheduled [" + unScheduledList.size() + "]");
          completedList.addAll(completedWO);
       }

       log.info("TOTAL: Completed WO [" + completedList.size() + "] - not Scheduled [" + masterMonthList.size() + "]");
       log.error("Unscheduled WO: ");
       masterMonthList.forEach(w -> log.error("\t " + w));
       return scheduleCalendar;
    }

    protected List<WO> scheduleTechForMonth(MonthlyCalendar calendar,
                                            final List<WO> woList)
    {
       log.info("Cities to visit: " + woList.stream().map(WO::getCITY).collect(Collectors.toSet()));
       // Keep track of what we scheduled
       List<WO> completedWOList = Lists.newArrayList();

       // Sort WO Longest to Shortest
       List<WO> techMasterList = woList.stream().sorted(
          (w1, w2) -> Integer.compare(w2.getMetaData().getWorkLoadMinutes(), w1.getMetaData().getWorkLoadMinutes())).
          collect(Collectors.toList());

       for (WO wo : techMasterList)
       {
          EventTask timeSlot = calendar.scheduleWorkOrder(wo);
          if (timeSlot == null)
          {
             log.error("Could not schedule wo " + wo.getIN2() + " time: " + wo.getMetaData().getWorkLoadMinutes());
             continue;
          }
          completedWOList.add(wo);
       }

       log.info(calendar.printCalendar(false));

       return completedWOList;
    }

    @Deprecated
    protected List<WO> scheduleTechForMonthOld(String tech,
                                               List<SKILL> skills,
                                               MonthlyCalendar calendar,
                                               final List<WO> woList)
    {
       List<WO> completedWOList = Lists.newArrayList();

       // Separate by city first
       List<WO> techMasterList = woList.stream().filter(w -> hasRequiredSkills(w, skills)).collect(Collectors.toList());


       EventTask nextSlot = calendar.getNextAvailableSlot();
       while (nextSlot != null && !techMasterList.isEmpty())
       {

          Day taskDay = calendar.getMasterCalendar().get(nextSlot.getDayOfMonth());
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

             Integer workTimeMins = wo.getMetaData().getWorkLoadMinutes();

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
       log.info(calendar.printCalendar(false));
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
          w -> w.getMetaData().getWorkLoadMinutes(), Collectors.toList()
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
          log.error("No suitable WO for slot - " + gapInMinutes + " keys - " + workTimeToWO.keySet());
          return null;
       }

       return workTimeToWO.get(currentSelection).get(0);
    }

    /**
     * Separate out work by Skills and GEO Location
     */
    protected Map<String, List<WO>> distributeWorkLoad(List<WO> masterMonthList,
                                                       Collection<TechProfile> techProfileList)
    {
       // Sort tech list alphabetical for deterministic results
       List<TechProfile> sortedTechs = techProfileList.stream().sorted(
          (f1, f2) -> f1.getName().compareTo(f2.getName())).collect(Collectors.toList());
       Map<String, List<WO>> result = Maps.newHashMap();
       sortedTechs.forEach(t -> result.put(t.getName(), Lists.newArrayList()));

       // Broken Up By Skill Set
       Map<SKILL, List<WO>> skillMap = Maps.newHashMap();
       for (SKILL s : SKILL.values())
       {
          List<WO> wos = masterMonthList.stream().filter(
             w -> w.getMetaData().getSkillsRequired().contains(s)).sorted(
             (f1, f2) -> Integer.compare(f2.getMetaData().getWorkLoadMinutes(), f1.getMetaData().getWorkLoadMinutes())).
             collect(Collectors.toList());
          skillMap.put(s, wos);
       }

       // TODO Break Up By GEO Area / Each Sub Category and match to tech

       for (Map.Entry<SKILL, List<WO>> entry : skillMap.entrySet())
       {
          // Do FE last as filler
          if (SKILL.FE.equals(entry.getKey()))
             continue;
          updateMapBySkill(sortedTechs, result, entry.getKey(), entry.getValue());
       }
       updateMapBySkill(sortedTechs, result, SKILL.FE, skillMap.get(SKILL.FE));

       return result;
    }

    private void updateMapBySkill(Collection<TechProfile> techProfileList,
                                  Map<String, List<WO>> result,
                                  SKILL skill,
                                  List<WO> wos)
    {
       if (wos.isEmpty())
          return;

       List<TechProfile> availableTechs = techProfileList.stream().filter(p -> p.getScheduleSkills().contains(skill)).collect(Collectors.toList());
       if (availableTechs.size() == 0)
       {
          log.error("Skill required for WO but not available [" + skill + "]");
          return;
       }

       int techIdx = 0;
       for (WO wo : wos)
       {
          List<ItemStatHolder> items = wo.getMetaData().getItemStatHolderList().stream().filter(i -> skill.equals(i.getSkill())).collect(Collectors.toList());

          // Assign techs in round robin order unless there one is already on site
          Set<String> availableAlreadyOnSite = availableTechs.stream().map(TechProfile::getName).collect(Collectors.toSet());
          availableAlreadyOnSite.retainAll(wo.getMetaData().getTechsOnSite());

          if (!availableAlreadyOnSite.isEmpty())
          {
             String tech = availableAlreadyOnSite.iterator().next();
             items.forEach(i -> i.setTech(tech));
//             result.get(tech).add(wo);
          }
          else if (TechProfileConfiguration.getInstance().getAllCustomerPreferences().contains(wo.getCN()))
          {
             String tech = techProfileList.stream().filter(p -> p.getCustomerPref().contains(wo.getCN())).findFirst().get().getName();
             items.forEach(i -> i.setTech(tech));
             result.get(tech).add(wo);
          }
          else
          {
             String tech = availableTechs.get(techIdx).getName();
             items.forEach(i -> i.setTech(tech));
             result.get(tech).add(wo);
             // Round robin
             techIdx = ++techIdx % availableTechs.size();
          }
       }
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

    protected void submitCalendarToGoogle(ScheduleCalendar calendar)
    {

       for (MonthlyCalendar cal : calendar.getTechCalendars().values())
       {
//          if (cal.getTech().equals("BR") || cal.getTech().equals("ID") || cal.getTech().equals("MH"))
          {
             List<Event> calendarEvents = monlthlyCalendarToEvents(cal);
             calendarManager.bulkAddEvents(calendarEvents, cal.getTech());
          }
       }
    }

    protected List<Event> monlthlyCalendarToEvents(MonthlyCalendar calendar)
    {
       List<Event> events = Lists.newArrayList();

       // Get All Event Tasks
       List<EventTask> allEventsTasks = calendar.getAllEventTasks(true);

       for (EventTask task : allEventsTasks)
       {
          String title;
          String desc;
          String location;
          if (task.isLunch())
          {
             title = "LUNCH";
             desc = "LUNCH";
             location = "LUNCH";
          }
          else
          {
             WO wo = task.getWo();
             title = wo.getNAME() + " : " + wo.getCN() + "-" + wo.getIN2();

             List<String> itemDesc = wo.getMetaData().getItemStatHolderList().stream().map(
                i -> i.getItemCode() + ":" + i.getCount()).collect(Collectors.toList());
             desc = String.join(",", itemDesc);
             location = wo.getADR1() + " " + wo.getCITY() + " " + wo.getZIP();
          }

          DateTime start = new DateTime(task.getStart().toDate());
          DateTime end = new DateTime(task.getEnd().toDate());
          List<String> people = Lists.newArrayList("tony.greway@sentryfire.net");
          Event event = CalenderUtils.createEvent(title, location, desc, start, end, null, people,
                                                  AppConfiguartion.getInstance().getCalReminderMin(),
                                                  null);
          events.add(event);
       }
       return events;
    }

    protected List<WO> filterRawWOList(List<WO> raw)
    {
       return raw.stream().filter(w -> !isMonitoringMonthlyOnly(w)).collect(Collectors.toList());
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
