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
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.stream.Collectors;

 import com.google.api.services.calendar.model.Event;
 import com.google.api.services.calendar.model.EventAttendee;
 import com.google.api.services.calendar.model.Events;
 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.google.common.collect.Sets;
 import com.sentryfire.business.schedule.googlecalendar.CalendarManager;
 import com.sentryfire.business.schedule.googlecalendar.CalenderUtils;
 import com.sentryfire.business.schedule.model.EventTask;
 import com.sentryfire.business.schedule.model.MonthlyCalendar;
 import com.sentryfire.business.schedule.model.ScheduleCalendar;
 import com.sentryfire.business.utils.SerializerUtils;
 import com.sentryfire.config.TechProfile;
 import com.sentryfire.config.TechProfileConfiguration;
 import com.sentryfire.model.ItemStatHolder;
 import com.sentryfire.model.SKILL;
 import com.sentryfire.model.WO;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class SchedulerBuilder
 {
    private Logger log = LoggerFactory.getLogger(getClass());

    protected static CalendarManager calendarManager = CalendarManager.getInstance();

    Pattern jobPattern = Pattern.compile("JOB-IN2:.*", Pattern.MULTILINE);


    public void buildAndInsertAllSchedules(org.joda.time.DateTime start)
    {
       // Google Maps stuff
//       GoogleMapsClient googleMapsClient = new GoogleMapsClient();
//       List<WO> rawList = googleMapsClient.route(start);
//       SerializerUtils.serializeList(rawList);

       List<WO> rawList = SerializerUtils.deWOSerializeList();

       // Remove events that need to be rescheduled
       List<Event> confirmed = getConfirmedAndClearUnconfirmed();
       // Filter out raw list
       List<WO> woList = filterRawWOList(rawList, confirmed);


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

          // Assign techs in round robin order unless one is already on site
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

    /////////////////////////////////////
    // Helpers
    /////////////////////////////////////

    protected void submitCalendarToGoogle(ScheduleCalendar calendar)
    {
       for (MonthlyCalendar cal : calendar.getTechCalendars().values())
       {
          {
             List<Event> calendarEvents = CalenderUtils.monthlyCalendarToEvents(cal);
             calendarManager.bulkAddEvents(calendarEvents, cal.getTech());
          }
       }
    }

    protected List<Event> getConfirmedAndClearUnconfirmed()
    {
       List<Event> confirmedEvents = Lists.newArrayList();
       for (String calName : CalendarManager.getInstance().getCalendarNameToID().keySet())
       {
          try
          {
             List<Event> removeEvents = Lists.newArrayList();
             Events events = CalendarManager.getInstance().listEvents(calName);
             for (Event e : events.getItems())
             {
                if (isProtectedEvent(e))
                   confirmedEvents.add(e);
                else
                   removeEvents.add(e);
             }
             CalendarManager.getInstance().deleteEventList(calName, removeEvents);
             log.info("Removed [" + removeEvents.size() + "] events from [" + calName + "].");
          }
          catch (Exception e)
          {
             log.error("Failed to load/delete calendar for tech [" + calName + "].", e);
          }
       }

       return confirmedEvents;
    }

    protected boolean isProtectedEvent(Event e)
    {
//       if (e.getDescription() != null && e.getDescription().contains(AUTO))
       if (e.getDescription() != null)
       {
          if (e.getAttendees() != null)
          {
             for (EventAttendee a : e.getAttendees())
             {
                if (a.getResponseStatus() != null && "accepted".equals(a.getResponseStatus()))
                   return true;
             }
          }
          // Delete unconfirmed auto events
          return false;
       }
       return true;
    }

    protected List<WO> filterRawWOList(List<WO> raw,
                                       List<Event> confirmedEvents)
    {
       List<String> confirmedJobNumbers = Lists.newArrayList();
       for (Event event : confirmedEvents)
       {
          if (event.getDescription() != null)
          {
             try
             {
                Matcher m = jobPattern.matcher(event.getDescription());
                if (m.find())
                {
                   String[] matchArray = m.group().split(":");
                   confirmedJobNumbers.add(matchArray[1].trim());
                }
             }
             catch (Exception e)
             {
                log.error("Failed to parse the description of the event - unable to determine if it has a WO number.", e);
             }
          }
       }
       List<WO> filtered = raw.stream().filter(w -> !confirmedJobNumbers.contains(w.getIN2())).collect(Collectors.toList());
       return filtered.stream().filter(w -> !isMonitoringMonthlyOnly(w)).collect(Collectors.toList());
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


 }
