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
 import java.util.function.Function;
 import java.util.stream.Collectors;

 import com.google.api.services.calendar.model.Event;
 import com.google.api.services.calendar.model.Events;
 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.google.common.collect.Sets;
 import com.sentryfire.business.schedule.googlecalendar.CalendarManager;
 import com.sentryfire.business.schedule.googlecalendar.CalenderUtils;
 import com.sentryfire.business.schedule.googlemaps.MapUtils;
 import com.sentryfire.business.schedule.model.EventTask;
 import com.sentryfire.business.schedule.model.GeoCodeData;
 import com.sentryfire.business.schedule.model.MonthlyCalendar;
 import com.sentryfire.business.schedule.model.ScheduleCalendar;
 import com.sentryfire.business.utils.SerializerUtils;
 import com.sentryfire.config.AppConfiguartion;
 import com.sentryfire.config.TechProfile;
 import com.sentryfire.config.TechProfileConfiguration;
 import com.sentryfire.model.ItemStatHolder;
 import com.sentryfire.model.SKILL;
 import com.sentryfire.model.WO;
 import com.sentryfire.persistance.DAOFactory;
 import org.joda.time.DateTime;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class SchedulerBuilder
 {
    private Logger log = LoggerFactory.getLogger(getClass());

    protected static CalendarManager calendarManager = CalendarManager.getInstance();

    protected static final int MIN_PER_DAY = 8 * 60;

    public void buildAndInsertAllSchedules(org.joda.time.DateTime start)
    {
       List<WO> woList = getWorkOrderList(start, true);

       List<WO> denver = woList.stream().filter(w -> w.getDEPT().equals("DENVER")).collect(Collectors.toList());
       ItemMetaDataUtils.calculateWorkLoad(denver);
       List<WO> greeley = woList.stream().filter(w -> w.getDEPT().equals("GREELEY")).collect(Collectors.toList());
       ItemMetaDataUtils.calculateWorkLoad(greeley);
       List<WO> cosprings = woList.stream().filter(w -> w.getDEPT().equals("CO_SPRINGS")).collect(Collectors.toList());
       ItemMetaDataUtils.calculateWorkLoad(cosprings);

       List<WO> emptyMetaData = woList.stream().filter(w -> w.getMetaData().getItemStatHolderList().size() == 0).collect(Collectors.toList());
       if (emptyMetaData.size() > 0)
          log.error("Found the following WO with missing meta data: " + emptyMetaData);

       try
       {
          buildRouteAndInsert(TechProfileConfiguration.getInstance().getDenTechToProfiles(), start, denver);
//       buildRouteAndInsert(TechProfileConfiguration.getInstance().getGreTechToProfiles(), start, greeley);
//       buildRouteAndInsert(TechProfileConfiguration.getInstance().getFipTechToProfiles(), start, cosprings);

          // Serialize after we have inserted the metadata info
          SerializerUtils.serializeWOList(start, woList);
       }
       catch (Exception e)
       {
          log.error("Catastrophic failure, failed to schedule due to: ", e);
       }

    }

    private void buildRouteAndInsert(Map<String, TechProfile> profileMap,
                                     DateTime start,
                                     List<WO> rawList) throws Exception
    {
       Map<String, WO> in2ToWOList = rawList.stream().collect(Collectors.toMap(WO::getIN2, Function.identity()));

       // Remove events that need to be rescheduled
       Map<String, List<Event>> confirmed = getConfirmedAndClearUnconfirmed();

       // Filter out raw list
       List<WO> unassignedList = rawList.stream().filter(this::isMonitoringMonthlyOnly).collect(Collectors.toList());
       List<WO> woList = filterRawWOList(rawList, confirmed.values().stream().flatMap(List::stream).collect(Collectors.toList()));

       // Geo Code if Needed
       Map<String, GeoCodeData> geoCodeMap = MapUtils.geoCodeWOList(woList);
       Map<String, Map<String, Double>> distanceMatrix = MapUtils.calculateDistanceMatrix(geoCodeMap, geoCodeMap);
       Map<String, GeoCodeData> geoCodeTechMap = MapUtils.geoCodeTechTerritory(profileMap);
       Map<String, Map<String, Double>> territoryMatrix = MapUtils.calculateDistanceMatrix(geoCodeMap, geoCodeTechMap);


       ScheduleCalendar calendar = buildSchedule(profileMap, woList, unassignedList, start, in2ToWOList, distanceMatrix, territoryMatrix, confirmed);

       submitCalendarToGoogle(calendar);
    }

    ////////////////
    // Helpers
    ////////////////

    protected ScheduleCalendar buildSchedule(final Map<String, TechProfile> techToProfile,
                                             final List<WO> rawList,
                                             final List<WO> unassignedList,
                                             final org.joda.time.DateTime calStart,
                                             final Map<String, WO> in2ToWOMap,
                                             final Map<String, Map<String, Double>> distanceMatrix,
                                             final Map<String, Map<String, Double>> territoryMatrix,
                                             final Map<String, List<Event>> confirmedScheduled) throws Exception
    {

       ScheduleCalendar scheduleCalendar = new ScheduleCalendar(techToProfile.keySet(), calStart);

       // Update scheduled events (manually entered in google calendar or confirmed)
       scheduleCalendar.insertScheduledEvents(confirmedScheduled, in2ToWOMap);

       List<WO> masterMonthList = Lists.newArrayList(rawList);
       Set<WO> completedList = Sets.newHashSet();

       Map<String, List<WO>> distributedWOList = distributeWorkLoad(in2ToWOMap, calStart, masterMonthList, territoryMatrix, distanceMatrix, techToProfile.values());

       for (MonthlyCalendar techCal : scheduleCalendar.getTechCalendars().values())
       {
          String tech = techCal.getTech();

          List<WO> unScheduledList = Lists.newArrayList(distributedWOList.get(tech));
          List<WO> completedWO = scheduleTechForMonth(techCal, distanceMatrix, distributedWOList.get(tech), in2ToWOMap);

          masterMonthList.removeAll(completedWO);
          unScheduledList.removeAll(completedWO);
          log.info("TECH: [" + tech + "] Total [" + distributedWOList.get(tech).size() + "] Completed WO [" + completedWO.size() + "]" +
                   " NOT Scheduled [" + unScheduledList.size() + "]");
          completedList.addAll(completedWO);
       }

       log.info("TOTAL: Completed WO [" + completedList.size() + "] - not Scheduled [" + masterMonthList.size() + "]");
       log.error("Unscheduled WO: ");
       masterMonthList.forEach(
          w -> log.error("\t " + w.getIN2() + " " + w.getNAME() + " " + w.getMetaData()));

       // TODO add this when we are ready?? when should we do it
       updateTechsInDB(distributedWOList);
       distributedWOList.clear();
       unassignedList.addAll(masterMonthList);
       distributedWOList.put("", unassignedList);
       updateTechsInDB(distributedWOList);

       return scheduleCalendar;
    }

    /**
     * Algorithm:
     * <p>
     * 1 - Schedule Items that are already have another tech first to try and guarantee free
     * 2 - Schedule Items that are longer than 4 hours to a free day first (or configured to be early morning)
     * 3 - Find closest WOs that would complete the day
     * 4 - Schedule the rest of the WO from the farthest WO away then back to the shop
     */

    protected List<WO> scheduleTechForMonth(MonthlyCalendar calendar,
                                            Map<String, Map<String, Double>> distanceMatrix,
                                            final List<WO> woList,
                                            final Map<String, WO> in2ToWOMap)
    {
       log.info("Scheduling Tech: " + calendar.getTech());
       log.info("Cities to visit: " + woList.stream().map(WO::getCITY).collect(Collectors.toSet()));
       if (woList.isEmpty())
          return woList;

       // Keep track of what we scheduled
       List<WO> completedWOList = Lists.newArrayList();
       List<WO> unScheduled = Lists.newArrayList(woList);

       Map<String, Map<String, Double>> matrix = MapUtils.filterFullMatrix(woList, distanceMatrix);

       // Sort WO Longest to Shortest
       List<WO> techMasterList = woList.stream().sorted(
          (w1, w2) -> Integer.compare(
             w2.getMetaData().getWorkLoadMinutes(calendar.getTech()), w1.getMetaData().getWorkLoadMinutes(calendar.getTech()))).
          collect(Collectors.toList());

       List<WO> workingList = techMasterList.stream().filter(
          w -> w.getMetaData().getItemStatHolderList().stream().anyMatch(i -> i.getScheduledStart() != null)).
          collect(Collectors.toList());

       // Schedule WOs that already have a tech on site first.
       for (WO wo : workingList)
       {
          DateTime scheduledStart = wo.getMetaData().getItemStatHolderList().stream().filter(
             i -> i.getScheduledStart() != null).findFirst().get().getScheduledStart();
          EventTask timeSlot = calendar.scheduleWorkOrder(wo, scheduledStart);
          if (timeSlot == null)
          {
             log.error("Could not schedule wo " + wo.getIN2() + " time: " + wo.getMetaData().getWorkLoadMinutes());
             continue;
          }
          completedWOList.add(wo);
          unScheduled.remove(wo);
       }

       // Schedule WOs that are longer than 4 hours
       Set<WO> largeWO = unScheduled.stream().filter(
          w -> w.getMetaData().getWorkLoadMinutes(calendar.getTech()) >= 4 * 60).collect(Collectors.toSet());

       Set<WO> earlyWO = unScheduled.stream().filter(
          w -> w.getMetaData().hasEarlyItem(calendar.getTech())).collect(Collectors.toSet());

       largeWO.addAll(earlyWO);

       for (WO wo : largeWO)
       {
          List<WO> done = scheduleDayFromOrigin(wo, calendar, in2ToWOMap, matrix);
          if (done == null)
          {
             break;
          }
          unScheduled.removeAll(done);
          completedWOList.addAll(done);
       }

       // Going reverse order distance from the shop
       Map<String, Double> shopDistance = MapUtils.sortByFarthestDistanceFirst(matrix.get("0"));
       List<String> unScheduledIn2 = unScheduled.stream().map(WO::getIN2).collect(Collectors.toList());
       shopDistance.keySet().retainAll(unScheduledIn2);

       unScheduled = shopDistance.keySet().stream().map(in2ToWOMap::get).collect(Collectors.toList());
       workingList = shopDistance.keySet().stream().map(in2ToWOMap::get).collect(Collectors.toList());

       while (workingList.size() > 0)
       {
          List<WO> scheduledList = scheduleDayFromOrigin(unScheduled.get(0), calendar, in2ToWOMap, matrix);
          if (scheduledList == null)
          {
             workingList.remove(unScheduled.get(0));
             break;
          }
          workingList.removeAll(scheduledList);
          unScheduled.removeAll(scheduledList);
          completedWOList.addAll(scheduledList);
       }

       // TODO make this place the WO near other ones
       // Try to insert any uncompleted WO's anywhere in the schedule
       workingList.clear();
       workingList.addAll(unScheduled);
       for (WO wo : workingList)
       {
          EventTask task = calendar.scheduleWorkOrder(wo);
          if (task != null)
          {
             unScheduled.remove(wo);
             completedWOList.add(wo);
          }
       }

       log.info(calendar.printCalendar(false));

       return completedWOList;
    }

    private List<WO> scheduleDayFromOrigin(WO originWO,
                                           MonthlyCalendar calendar,
                                           Map<String, WO> in2ToWOMap,
                                           Map<String, Map<String, Double>> matrix)
    {
       int currentMinsLeft = MIN_PER_DAY;

       List<WO> dayToSchedule = Lists.newArrayList();
       Set<String> currentIn2 = Sets.newHashSet();

       WO currentWO = originWO;
       while (currentWO != null)
       {
          currentIn2.add(currentWO.getIN2());
          dayToSchedule.add(currentWO);
          currentMinsLeft -= currentWO.getMetaData().getWorkLoadMinutes(calendar.getTech());
          currentMinsLeft -= AppConfiguartion.getInstance().getDriveTime();
          currentWO = getClosestWorkOrder(currentWO, currentIn2, calendar, in2ToWOMap, matrix, currentMinsLeft);
       }

       if (!calendar.scheduleFreeDay(dayToSchedule))
       {
          log.error("Failed to calculate route!" + dayToSchedule);
          return null;
       }

       return dayToSchedule;
    }

    private WO getClosestWorkOrder(WO originWO,
                                   Set<String> currentIn2,
                                   MonthlyCalendar calendar,
                                   Map<String, WO> in2ToWOMap,
                                   Map<String, Map<String, Double>> matrix,
                                   Integer currentMinsLeft)
    {
       if (matrix.get(originWO.getIN2()) == null)
          return null;

       for (Map.Entry<String, Double> entry : matrix.get(originWO.getIN2()).entrySet())
       {
          // Skip items in the current day already scheduled
          if (currentIn2.contains(entry.getKey()) || entry.getKey().equals("0"))
             continue;

          // Break we have scheduled the whole day
          if (currentMinsLeft < 60)
             break;

//           Skip self and any WO farther than ~15 miles away
//          if (entry.getKey().equals(originWO.getIN2()) || entry.getKey().equals("0") || entry.getValue() > 25000)
//             continue;

          // Longer than time left in day
          WO destWO = in2ToWOMap.get(entry.getKey());
          if (destWO.getMetaData().getWorkLoadMinutes(calendar.getTech()) > currentMinsLeft)
             continue;

          if (destWO.getMetaData().getItemStatHolderList(calendar.getTech()) == null
              || destWO.getMetaData().getItemStatHolderList(calendar.getTech()).isEmpty())
          {
             log.error("This shouldn't be seen in prod only when we are caching google data.");
             continue;
          }

          // Already scheduled
          if (destWO.getMetaData().getItemStatHolderList(calendar.getTech()).get(0).getScheduledStart() != null)
             continue;

          // If distance is greater than 15 miles from shop take closest WO
          double shopToOrig = matrix.get("0").get(originWO.getIN2());
          double shopToDest = matrix.get("0").get(destWO.getIN2());
          if (shopToOrig > 25000)
             return destWO;

          // If distance is less than 15 miles from shop start working back towards the shop
          if (shopToDest > shopToOrig)
             continue;

          return destWO;
       }
       return null;
    }

    /**
     * Separate out work by Skills and GEO Location
     */
    protected Map<String, List<WO>> distributeWorkLoad(final Map<String, WO> in2ToWOMap,
                                                       DateTime start,
                                                       List<WO> masterMonthList,
                                                       Map<String, Map<String, Double>> territoryMatrix,
                                                       Map<String, Map<String, Double>> fullMatrix,
                                                       Collection<TechProfile> techProfileList)
    {
       Map<String, List<WO>> result = SerializerUtils.deSerializeDistributionMap(start);
       if (result != null)
          return result;

       result = Maps.newTreeMap();

       // Sort tech list alphabetical for deterministic results
       List<TechProfile> sortedTechs = techProfileList.stream().sorted(
          (f1, f2) -> f1.getName().compareTo(f2.getName())).collect(Collectors.toList());

       for (TechProfile techProfile : sortedTechs)
       {
          result.put(techProfile.getName(), Lists.newArrayList());
       }

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

       for (Map.Entry<SKILL, List<WO>> entry : skillMap.entrySet())
       {
          // Do FE last as filler
          if (SKILL.FE.equals(entry.getKey()))
             continue;
          distributeBySkill(sortedTechs, fullMatrix, territoryMatrix, result, in2ToWOMap, entry.getKey(), entry.getValue());
       }
       distributeBySkill(sortedTechs, fullMatrix, territoryMatrix, result, in2ToWOMap, SKILL.FE, skillMap.get(SKILL.FE));

       SerializerUtils.serializeDistributionMap(start, result);
       return result;
    }

    private void distributeBySkill(Collection<TechProfile> techProfileList,
                                   Map<String, Map<String, Double>> fullMatrix,
                                   Map<String, Map<String, Double>> territoryMatrix,
                                   Map<String, List<WO>> result,
                                   final Map<String, WO> in2ToWOMap,
                                   SKILL skill,
                                   List<WO> wos)
    {
       if (wos.isEmpty())
          return;

       List<TechProfile> availableTechs = techProfileList.stream().filter(p -> p.getScheduleSkills().contains(skill)).collect(Collectors.toList());
       List<String> availableTechStringList = availableTechs.stream().map(TechProfile::getName).collect(Collectors.toList());
       if (availableTechs.size() == 0)
       {
          log.error("Skill required for WO but not available [" + skill + "]");
          return;
       }

       int techIdx = 0;
       for (WO wo : wos)
       {
          List<ItemStatHolder> itemsForSkill = wo.getMetaData().getItemStatHolderList().stream().filter(i -> skill.equals(i.getSkill())).collect(Collectors.toList());
          Set<String> availableAlreadyOnSite = availableTechs.stream().map(TechProfile::getName).collect(Collectors.toSet());
          availableAlreadyOnSite.retainAll(wo.getMetaData().getTechsOnSite());

          //  Assign WO by territory
          if (shouldAssignWO(wo, skill))
          {
             Map<String, Double> techList = territoryMatrix.get(wo.getIN2());

             String tech = null;
             for (Map.Entry<String, Double> entry : techList.entrySet())
             {
                if (availableTechStringList.contains(entry.getKey()))
                {
                   tech = entry.getKey();
                   break;
                }
             }
             log.info("Assigning " + tech + " to " + wo.getIN2());
             for (ItemStatHolder ih : itemsForSkill)
             {
                ih.setTech(tech);
             }
             result.get(tech).add(wo);
          }
          // Give to tech on site if assignment not required
          else if (!availableAlreadyOnSite.isEmpty())
          {
             String tech = availableAlreadyOnSite.iterator().next();
             itemsForSkill.forEach(i -> i.setTech(tech));
          }
          // Give to tech by customer preference
          else if (TechProfileConfiguration.getInstance().getAllCustomerPreferences().contains(wo.getCN()))
          {
             String tech = techProfileList.stream().filter(p -> p.getCustomerPref().contains(wo.getCN())).findFirst().get().getName();
             itemsForSkill.forEach(i -> i.setTech(tech));
             if (!result.get(tech).contains(wo))
                result.get(tech).add(wo);
          }
          // Fill in FE work giving to tech with closest assigned job
          else if (skill == SKILL.FE)
          {
             // Find closest assigned job to fill in FE work
             String tech = MapUtils.getClosestAssignedTech(result, in2ToWOMap, fullMatrix.get(wo.getIN2()));
             if (tech == null)
                log.error("TECH IS NULL!");

             itemsForSkill.forEach(i -> i.setTech(tech));
             if (!result.get(tech).contains(wo))
                result.get(tech).add(wo);
          }
          // Otherwise round robin for FA/SP work
          else
          {
             String tech = availableTechs.get(techIdx).getName();
             itemsForSkill.forEach(i -> i.setTech(tech));
             if (!result.get(tech).contains(wo))
                result.get(tech).add(wo);
             // Round robin
             techIdx = ++techIdx % availableTechs.size();
          }
       }
    }

    /////////////////////////////////////
    // Helpers
    /////////////////////////////////////

    protected void updateTechsInDB(Map<String, List<WO>> distributionMap)
    {
       log.info("BEGIN: Writing techs in to the db.");
       for (Map.Entry<String, List<WO>> entry : distributionMap.entrySet())
       {
          String tech = entry.getKey();
          log.info("Updating [" + entry.getValue().size() + "] WOs for tech [" + tech + "].");
          for (WO wo : entry.getValue())
          {
             DAOFactory.sqlDB().updateWOTech(tech, wo);
          }
       }

       log.info("END: Writing techs in to the db.");
    }

    protected static boolean shouldAssignWO(WO wo,
                                            SKILL skill)
    {
       if (skill == SKILL.KH)
          return true;

       if (skill == SKILL.FE)
       {
          long numTags = wo.getMetaData().getItemStatHolderList().stream().
             filter(i -> "TAG".equals(i.getItemCode())).mapToInt(ItemStatHolder::getCount).sum();
          if (numTags >= 10)
             return true;
       }
       return false;

    }

    public static List<WO> getWorkOrderList(DateTime start,
                                            boolean full)
    {
       MutableDateTime end = new MutableDateTime(start);
       end.setDayOfMonth(start.dayOfMonth().getMaximumValue());

       if (full)
       {
          List<WO> result = SerializerUtils.deSerializeWOList(start);
          if (result == null)
             result = DAOFactory.getWipDao().getHistoryWOAndItems(start.toDateTime(), end.toDateTime());
          return result;
       }
       return SerializerUtils.deSerializeWOList(start).stream().limit(2).collect(Collectors.toList());
    }

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

    protected Map<String, List<Event>> getConfirmedAndClearUnconfirmed()
    {
       Map<String, List<Event>> confirmedEvents = Maps.newHashMap();
       for (String calName : CalendarManager.getInstance().getCalendarNameToID().keySet())
       {
          try
          {
             List<Event> removeEvents = Lists.newArrayList();
             Events events = CalendarManager.getInstance().listEvents(calName);
             for (Event e : events.getItems())
             {
                if (CalenderUtils.isProtectedEvent(e))
                   confirmedEvents.computeIfAbsent(calName, eList -> Lists.newArrayList()).add(e);
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

    protected List<WO> filterRawWOList(List<WO> raw,
                                       List<Event> confirmedEvents)
    {
       List<String> confirmedJobNumbers = Lists.newArrayList();
       confirmedEvents.forEach(e -> confirmedJobNumbers.add(CalenderUtils.getIN2FromEvent(e)));
       List<WO> filtered = raw.stream().filter(w -> !confirmedJobNumbers.contains(w.getIN2())).collect(Collectors.toList());
       return filtered.stream().filter(w -> !isMonitoringMonthlyOnly(w)).collect(Collectors.toList());
    }

    protected boolean isMonitoringMonthlyOnly(WO wo)
    {
       if (wo != null)
       {
          if (wo.getMetaData().getWorkLoadMinutes() <= 0)
          {
             log.warn("WO [" + wo.getIN2() + "] has workload time <= 0 " + wo.getMetaData());
             return true;
          }
          for (ItemStatHolder item : wo.getMetaData().getItemStatHolderList())
          {
             if (item.getItemCode().equals("MONITORING") || item.getItemCode().equals("SC")
                 || item.getItemCode().equals("MONTHLY_BILLING_NT") || item.getItemCode().equals("PERMIT"))
                continue;
             else
                return false;
          }
          return true;
       }
       return false;
    }

 }
