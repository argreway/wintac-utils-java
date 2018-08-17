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
 import com.google.maps.model.DistanceMatrix;
 import com.google.maps.model.DistanceMatrixElement;
 import com.sentryfire.business.schedule.googlecalendar.CalendarManager;
 import com.sentryfire.business.schedule.googlecalendar.CalenderUtils;
 import com.sentryfire.business.schedule.googlemaps.GoogleMapsClient;
 import com.sentryfire.business.schedule.model.DistanceData;
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
       WorkLoadCalculator.calculateWorkLoad(denver);
       List<WO> greeley = woList.stream().filter(w -> w.getDEPT().equals("GREELEY")).collect(Collectors.toList());
       WorkLoadCalculator.calculateWorkLoad(greeley);
       List<WO> cosprings = woList.stream().filter(w -> w.getDEPT().equals("CO_SPRINGS")).collect(Collectors.toList());
       WorkLoadCalculator.calculateWorkLoad(cosprings);

       // Serialize after we insert line item info - for remote debugging
       SerializerUtils.serializeList(woList);

       try
       {
          buildRouteAndInsert(TechProfileConfiguration.getInstance().getDenTechToProfiles(), start, denver);
//       buildRouteAndInsert(TechProfileConfiguration.getInstance().getGreTechToProfiles(), start, greeley);
//       buildRouteAndInsert(TechProfileConfiguration.getInstance().getFipTechToProfiles(), start, cosprings);
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
       List<WO> woList = filterRawWOList(rawList, confirmed.values().stream().flatMap(List::stream).collect(Collectors.toList()));

       ScheduleCalendar calendar = buildSchedule(TechProfileConfiguration.getInstance().getDenTechToProfiles(), woList, start, in2ToWOList, confirmed);
       submitCalendarToGoogle(calendar);
    }

    ////////////////
    // Helpers
    ////////////////

    protected ScheduleCalendar buildSchedule(final Map<String, TechProfile> techToProfile,
                                             final List<WO> rawList,
                                             final org.joda.time.DateTime calStart,
                                             final Map<String, WO> in2ToWOMap,
                                             final Map<String, List<Event>> confirmedScheduled) throws Exception
    {
       ScheduleCalendar scheduleCalendar = new ScheduleCalendar(techToProfile.keySet(), calStart);

       // Update scheduled events (manually entered in google calendar or confirmed)
       scheduleCalendar.insertScheduledEvents(confirmedScheduled, in2ToWOMap);

       List<WO> masterMonthList = Lists.newArrayList(rawList);
       Set<WO> completedList = Sets.newHashSet();

       Map<String, List<WO>> distributedWOList = distributeWorkLoad(masterMonthList, techToProfile.values());

       for (MonthlyCalendar techCal : scheduleCalendar.getTechCalendars().values())
       {
          String tech = techCal.getTech();

          List<WO> unScheduledList = distributedWOList.get(tech);
          List<WO> completedWO = scheduleTechForMonth(techCal, distributedWOList.get(tech), in2ToWOMap);

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

    /**
     * Algorithm:
     * <p>
     * 1 - Schedule Items that are already have another tech first to try and guarantee free
     * 2 - Schedule Items that are longer than 4 hours to a free day first
     * -> Find closest WOs that would complete the day
     * 3 - Schedule the rest of the WO from the farthest WO away then back to the shop
     */

    protected List<WO> scheduleTechForMonth(MonthlyCalendar calendar,
                                            final List<WO> woList,
                                            final Map<String, WO> in2ToWOMap) throws Exception
    {
       log.info("Cities to visit: " + woList.stream().map(WO::getCITY).collect(Collectors.toSet()));

       // Keep track of what we scheduled
       List<WO> completedWOList = Lists.newArrayList();
       List<WO> unScheduled = Lists.newArrayList(woList);

       // Compute Distances Between WOs
//       Map<String, Map<String, DistanceData>> matrix = SerializerUtils.deSerializeDistanceData();
       Map<String, Map<String, DistanceData>> matrix = GoogleMapsClient.getFullMeshMatrix(woList);
       SerializerUtils.serializeDistanceData(matrix);

       // Sort WO Longest to Shortest
       List<WO> techMasterList = woList.stream().sorted(
          (w1, w2) -> Integer.compare(
             w2.getMetaData().getWorkLoadMinutes(calendar.getTech()), w1.getMetaData().getWorkLoadMinutes(calendar.getTech()))).
          collect(Collectors.toList());

       List<WO> scheduledList = techMasterList.stream().filter(
          w -> w.getMetaData().getItemStatHolderList().stream().anyMatch(i -> i.getScheduledStart() != null)).
          collect(Collectors.toList());

       // Schedule WOs that already have a tech on site first.
       for (WO wo : scheduledList)
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
       scheduledList = unScheduled.stream().filter(
          w -> w.getMetaData().getWorkLoadMinutes(calendar.getTech()) >= 4 * 60).collect(Collectors.toList());

       for (WO wo : scheduledList)
       {
          unScheduled.removeAll(scheduleDayFromOrigin(wo, calendar, in2ToWOMap, matrix));
       }

       // Going reverse order distance from the shop
       Map<String, DistanceData> shopDistance = GoogleMapsClient.sortByFarthestDistanceFirst(matrix.get("0"));
       List<String> unScheduledIn2 = unScheduled.stream().map(WO::getIN2).collect(Collectors.toList());
       shopDistance.keySet().retainAll(unScheduledIn2);

       unScheduled = shopDistance.keySet().stream().map(in2ToWOMap::get).collect(Collectors.toList());

       while (unScheduled.size() > 0)
       {
          scheduledList = scheduleDayFromOrigin(unScheduled.get(0), calendar, in2ToWOMap, matrix);
          unScheduled.removeAll(scheduledList);
       }

       log.info(calendar.printCalendar(false));

       return completedWOList;
    }

    private List<WO> scheduleDayFromOrigin(WO originWO,
                                           MonthlyCalendar calendar,
                                           Map<String, WO> in2ToWOMap,
                                           Map<String, Map<String, DistanceData>> matrix) throws Exception
    {
       int currentMinsLeft = MIN_PER_DAY;

       List<WO> dayToSchedule = Lists.newArrayList();

       WO currentWO = originWO;
       while (currentWO != null)
       {
          dayToSchedule.add(currentWO);
          currentMinsLeft -= currentWO.getMetaData().getWorkLoadMinutes(calendar.getTech());
          currentMinsLeft -= AppConfiguartion.getInstance().getDriveTime();
          currentWO = getClosestWorkOrder(currentWO, calendar, in2ToWOMap, matrix, currentMinsLeft);
       }

       if (!calendar.scheduleFreeDay(dayToSchedule))
          throw new Exception("Failed to calculate route!");

       return dayToSchedule;
    }

    private WO getClosestWorkOrder(WO originWO,
                                   MonthlyCalendar calendar,
                                   Map<String, WO> in2ToWOMap,
                                   Map<String, Map<String, DistanceData>> matrix,
                                   Integer currentMinsLeft)
    {
       for (Map.Entry<String, DistanceData> entry : matrix.get(originWO.getIN2()).entrySet())
       {
          // Break we have scheduled the whole day
          if (currentMinsLeft < 60)
             break;

          // Skip self and any WO farther than 25 minutes away
          if (entry.getKey().equals(originWO.getIN2()) || entry.getKey().equals("0") || entry.getValue().getDuration() / 60 > 25)
             continue;

          // Longer than time left in day
          WO destWO = in2ToWOMap.get(entry.getKey());
          if (destWO.getMetaData().getWorkLoadMinutes(calendar.getTech()) > currentMinsLeft)
             continue;

          // Already scheduled
          if (destWO.getMetaData().getItemStatHolderList(calendar.getTech()).get(0).getScheduledStart() != null)
             continue;

          // Distance must be less than from shop to orig
          long shopToOrig = matrix.get("0").get(originWO.getIN2()).getDistance();
          long shopToDest = matrix.get("0").get(destWO.getIN2()).getDistance();
          if (shopToDest > shopToOrig)
             continue;

          return destWO;
       }
       return null;
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


//       Map<String, Map<String, DistanceData>> matrix = null;
//       // Build matrix if necessary
//       if (skill == SKILL.KH)
//       {
//          List<String> origLocations = wos.stream().map(WO::getFullAddress).collect(Collectors.toList());
//          List<String> destTerritories = availableTechs.stream().map(TechProfile::getTerritory).collect(Collectors.toList());
//          matrix = GoogleMapsClient.getFullMeshMatrix(origLocations, destTerritories);
//
//       }


       int techIdx = 0;
       for (WO wo : wos)
       {
          List<ItemStatHolder> itemsForSkill = wo.getMetaData().getItemStatHolderList().stream().filter(i -> skill.equals(i.getSkill())).collect(Collectors.toList());

          //  Group WO by territory
          if (skill == SKILL.KH)
          {
             String tech = getClosestTech(wo, availableTechs);
             itemsForSkill.forEach(i -> i.setTech(tech));
             result.get(tech).add(wo);
          }

          // Assign techs in round robin order unless one is already on site
          Set<String> availableAlreadyOnSite = availableTechs.stream().map(TechProfile::getName).collect(Collectors.toSet());
          availableAlreadyOnSite.retainAll(wo.getMetaData().getTechsOnSite());

          if (!availableAlreadyOnSite.isEmpty())
          {
             String tech = availableAlreadyOnSite.iterator().next();
             itemsForSkill.forEach(i -> i.setTech(tech));
          }
          else if (TechProfileConfiguration.getInstance().getAllCustomerPreferences().contains(wo.getCN()))
          {
             String tech = techProfileList.stream().filter(p -> p.getCustomerPref().contains(wo.getCN())).findFirst().get().getName();
             itemsForSkill.forEach(i -> i.setTech(tech));
             if (!result.get(tech).contains(wo))
                result.get(tech).add(wo);
          }
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

    protected String getClosestTech(WO wo,
                                    List<TechProfile> availableTechs)
    {
       List<String> destList = availableTechs.stream().filter(p -> p.getTerritory() != null && !p.getTerritory().isEmpty()).
          map(TechProfile::getTerritory).collect(Collectors.toList());
       List<String> techList = availableTechs.stream().filter(p -> p.getTerritory() != null && !p.getTerritory().isEmpty()).
          map(TechProfile::getName).collect(Collectors.toList());

       String origin = wo.getFullAddress();
       List<String> origList = Lists.newArrayList(origin);

       DistanceMatrix matrix = GoogleMapsClient.getDistanceMatrix(origList, destList);

       int closest = -1;
       if (matrix != null && matrix.rows.length > 0)
       {
          long closestDist = Long.MAX_VALUE;
          int idx = 0;
          for (DistanceMatrixElement element : matrix.rows[0].elements)
          {
             if (element.distance == null)
             {
                log.error("Distance is null? " + origList);
                continue;
             }
             if (element.distance.inMeters < closestDist)
             {
                closest = idx;
                closestDist = element.distance.inMeters;
             }
             idx++;
          }
       }

       if (closest == -1)
          return null;

       String closetTech = techList.get(closest);
       log.info("Assigning " + origin + " to " + closetTech);
       return closetTech;
    }

    public static List<WO> getWorkOrderList(DateTime start,
                                            boolean full)
    {
       MutableDateTime end = new MutableDateTime(start);
       end.setDayOfMonth(start.dayOfMonth().getMaximumValue());

       if (full)
       {
//          return DAOFactory.getWipDao().getHistoryWOAndItems(start.toDateTime(), end.toDateTime());
          return SerializerUtils.deWOSerializeList();
       }
       return SerializerUtils.deWOSerializeList().stream().limit(2).collect(Collectors.toList());
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
