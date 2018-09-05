 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= * Author:    Tony Greway
  * File:      CalenderUtils.java
  * Created:   7/16/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.googlecalendar;

 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.List;
 import java.util.Map;
 import java.util.function.Function;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.stream.Collectors;

 import com.google.api.client.util.DateTime;
 import com.google.api.services.calendar.model.ConferenceData;
 import com.google.api.services.calendar.model.Event;
 import com.google.api.services.calendar.model.EventAttendee;
 import com.google.api.services.calendar.model.EventDateTime;
 import com.google.api.services.calendar.model.EventReminder;
 import com.google.api.services.calendar.model.Events;
 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.sentryfire.business.schedule.SchedulerBuilder;
 import com.sentryfire.business.schedule.model.EventTask;
 import com.sentryfire.business.schedule.model.MonthlyCalendar;
 import com.sentryfire.config.AppConfiguartion;
 import com.sentryfire.config.TechProfileConfiguration;
 import com.sentryfire.model.ItemStatHolder;
 import com.sentryfire.model.WO;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class CalenderUtils
 {
    static Logger log = LoggerFactory.getLogger(CalenderUtils.class);

    // Used to identify events generated by program or user
    public static String AUTO = "AUTOGEN";

    private static final String RECUR_RULE = "RRULE:FREQ=DAILY;COUNT=2";

    protected static Pattern jobPattern = Pattern.compile("JOB-IN2:.*", Pattern.MULTILINE);

    public static Event createEvent(String name,
                                    String location,
                                    String desc,
                                    DateTime start,
                                    DateTime end,
                                    String recurRule,
                                    List<String> emailAttendees,
                                    Integer reminderPopup,
                                    Integer reminderEmail)
    {
       Event event = new Event().setSummary(name).setLocation(location).setDescription(desc);

       EventDateTime eStart = new EventDateTime().setDateTime(start).setTimeZone("America/Denver");
       event.setStart(eStart);

       EventDateTime eEnd = new EventDateTime().setDateTime(end).setTimeZone("America/Denver");
       event.setEnd(eEnd);

       String[] recurrence = new String[]{recurRule};
       event.setRecurrence(Arrays.asList(recurrence));

       if (emailAttendees != null)
       {
          List<EventAttendee> attendeeList = emailAttendees.stream().map(
             s -> new EventAttendee().setEmail(s)).collect(Collectors.toList());
          event.setAttendees(attendeeList);
       }

       if (reminderEmail != null && reminderPopup != null)
       {
          EventReminder[] reminderOverrides = new EventReminder[]{
             new EventReminder().setMethod("email").setMinutes(reminderEmail),
             new EventReminder().setMethod("popup").setMinutes(reminderPopup),
             };
          Event.Reminders reminders = new Event.Reminders()
             .setUseDefault(false)
             .setOverrides(Arrays.asList(reminderOverrides));
          event.setReminders(reminders);
       }

       // No conference
       ConferenceData cd = new ConferenceData();
       cd.setEntryPoints(null);
       cd.setConferenceSolution(null);
       event.setConferenceData(cd);

       return event;
    }

    public static boolean isProtectedEvent(Event e)
    {
       if (e.getDescription() != null && e.getDescription().contains(AUTO))
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

    public static List<EventTask> eventsToEventTaskList(List<Event> events,
                                                        Map<String, WO> in2ToWO)
    {
       List<EventTask> result = Lists.newArrayList();

       for (Event e : events)
       {
          try
          {
             MutableDateTime start;
             if (e.getStart().getDate() != null)
             {
                if (e.getStart().getDate().isDateOnly())
                {
                   start = new MutableDateTime(e.getStart().getDate().getValue());
                   start.setHourOfDay(0);
                   start.addDays(1);
                }
                else
                   start = new MutableDateTime(e.getStart().getDate().getValue());
             }
             else
                start = new MutableDateTime(e.getStart().getDateTime().getValue());

             MutableDateTime end;
             if (e.getEnd().getDate() != null)
             {
                if (e.getEnd().getDate().isDateOnly())
                {
                   end = new MutableDateTime(e.getEnd().getDate().getValue());
                   end.setHourOfDay(23);
                }
                else
                   end = new MutableDateTime(e.getEnd().getDate().getValue());
             }
             else
                end = new MutableDateTime(e.getEnd().getDateTime().getValue());

             // Handle vacation/cleared days (ie cleared so other work can be done like an install)
             if (start.getHourOfDay() <= 5 && end.getHourOfDay() >= 20 ||
                 (start.getDayOfMonth() != end.getDayOfMonth()))
             {
                // Handle google ending all day event on 0:0:0 of the next day
                if (end.getHourOfDay() == 0 && end.getMinuteOfHour() == 0)
                   end.addHours(-1);
                MutableDateTime current = new MutableDateTime(start);
                while (current.getDayOfMonth() <= end.getDayOfMonth())
                {
                   EventTask task = new EventTask(null, current.toDateTime(), current.toDateTime(), false);
                   task.setClearedDay(true);
                   result.add(task);
                   current.addDays(1);
                }
                continue;
             }

             String in2 = getIN2FromEvent(e);

             WO wo = null;
             if (in2 != null && !in2.isEmpty())
                wo = in2ToWO.get(in2);

             boolean isLunch = false;
             if (e.getSummary() != null && e.getSummary().contains("LUNCH"))
                isLunch = true;

             EventTask task = new EventTask(wo, start.toDateTime(), end.toDateTime(), isLunch);
             task.setScheduledEvent(true);
             result.add(task);
          }
          catch (Exception ex)
          {
             log.error("Unable to insert scheduled event as task: " + e, ex);
          }

       }
       return result;
    }

    public static org.joda.time.DateTime getDateFromEvent(Event e)
    {
       if (e.getStart() == null)
          return null;

       DateTime time = e.getStart().getDateTime();
       if (time != null)
          return new org.joda.time.DateTime(time.getValue());

       time = e.getStart().getDate();
       if (time != null)
          return new org.joda.time.DateTime(time.getValue());

       return null;
    }

    public static String getIN2FromEvent(Event event)
    {
       String result = "";
       if (event.getDescription() != null)
       {
          try
          {
             Matcher m = jobPattern.matcher(event.getDescription());
             if (m.find())
             {
                String[] matchArray = m.group().split(":");
                result = matchArray[1].trim();
             }
          }
          catch (Exception e)
          {
             log.error("Failed to parse the description of the event - unable to determine if it has a WO number.", e);
          }
       }
       return result;
    }

    public static List<Event> monthlyCalendarToEvents(MonthlyCalendar calendar)
    {
       List<Event> events = Lists.newArrayList();

       // Get All Event Tasks
       List<EventTask> allEventsTasks = calendar.getAllEventTasks(true);

       for (EventTask task : allEventsTasks)
       {
          // already in the calendar
          if (task.isScheduledEvent())
             continue;

          String title;
          String desc;
          String location;
          if (task.isLunch())
          {
             title = "LUNCH";
             desc = "LUNCH";
             desc += "\n\n" + AUTO;
             location = "LUNCH";
          }
          else
          {
             WO wo = task.getWo();
             title = wo.getNAME() + " : " + wo.getCN() + "-" + wo.getIN2();

             // Build Description
             List<String> itemDesc = wo.getMetaData().getItemStatHolderList().stream().map(
                i -> i.getCount() + "-" + i.getItemCode()).collect(Collectors.toList());

             desc = getLongDescription(wo);
             location = wo.getADR1() + " " + wo.getCITY() + " " + wo.getZIP();
          }
          title = title.replace("_", " ");
          // Description has emails with underscores etc
//          desc = desc.replace("_", " ");
          location = location.replace("_", " ");

          DateTime start = new DateTime(task.getStart().toDate());
          DateTime end = new DateTime(task.getEnd().toDate());
//          List<String> people = Lists.newArrayList(AppConfiguartion.getInstance().getSchedulerEmail());
          List<String> people = null;
          Event event = CalenderUtils.createEvent(title, location, desc, start, end, null, people,
                                                  AppConfiguartion.getInstance().getCalReminderMin(),
                                                  AppConfiguartion.getInstance().getCalReminderMin());
          events.add(event);
       }
       return events;
    }

    public static String getLongDescription(WO wo)
    {
       String desc = "";
       // Build Description
       List<String> itemDesc = wo.getMetaData().getItemStatHolderList().stream().map(
          i -> i.getCount() + "-" + i.getItemCode()).collect(Collectors.toList());

       // Greeting
       desc = AppConfiguartion.getInstance().getEmailDescription();
       // Items
       desc += getShortCustDesc(wo);

       desc += "\n\n" + AUTO;
       return desc;
    }

    public static String getShortCustDesc(WO wo)
    {
       String desc = "";
       // Build Description
       List<String> itemDesc = wo.getMetaData().getItemStatHolderList().stream().map(
          i -> i.getCount() + "-" + i.getItemCode()).collect(Collectors.toList());
       // Items
       desc += "SERVICE ITEMS:\t" + String.join(",", itemDesc);

       if (wo.getSAL() != null && !wo.getSAL().isEmpty())
          desc += "\n\nCONTACT NAME:\t\t" + wo.getSAL();
       if (wo.getCST_TEL() != null && !wo.getCST_TEL().isEmpty())
          desc += "\nCUSTOMER PHONE:\t" + wo.getCST_TEL();
       if (wo.getWIP_TEL() != null && !wo.getWIP_TEL().isEmpty())
          desc += "\nLOCATION PHONE:\t" + wo.getWIP_TEL();
       if (wo.getWIP_CELL() != null && !wo.getWIP_CELL().isEmpty())
          desc += "\nLOCATION CELL:\t\t" + wo.getWIP_CELL();
       if (wo.getCN() != null && !wo.getCN().isEmpty())
          desc += "\nCN:\t\t\t\t\t" + wo.getCN();
       if (wo.getIN2() != null && !wo.getIN2().isEmpty())
          desc += "\nJOB-IN2:\t\t\t" + wo.getIN2();
       if (wo.getEMAIL() != null && !wo.getEMAIL().isEmpty())
          desc += "\nEMAIL:\t\t\t\t" + wo.getEMAIL();

       return desc;
    }

    public static Map<String, Map<String, List<WO>>> getCalMap(org.joda.time.DateTime dateTime)
    {
       List<WO> raw = SchedulerBuilder.getWorkOrderList(dateTime, true);
//       ItemMetaDataUtils.insertItemMeta(raw);

       Map<String, WO> in2ToWO = raw.stream().collect(Collectors.toMap(WO::getIN2, Function.identity()));

       Map<String, Map<String, List<WO>>> result = Maps.newHashMap();

       for (String tech : TechProfileConfiguration.getInstance().getDenTechToProfiles().keySet())
       {
          try
          {
             Events events = CalendarManager.getInstance().listEvents(tech);
             Map<String, List<WO>> dateMap = Maps.newHashMap();
             if (events != null && events.getItems() != null)
             {
                for (Event e : events.getItems())
                {
                   org.joda.time.DateTime time = CalenderUtils.getDateFromEvent(e);
                   String in2 = CalenderUtils.getIN2FromEvent(e);
                   if (in2 == null || time == null)
                      continue;
                   String day = time.getDayOfMonth() + "-" + time.getMonthOfYear() + "-" + time.getYear();
                   WO wo = in2ToWO.get(in2);
                   if (wo != null && wo.getMetaData().getItemStatHolderList() != null && !wo.getMetaData().getItemStatHolderList().isEmpty())
                      wo.getMetaData().getItemStatHolderList().get(0).setScheduledStart(time);
                   dateMap.computeIfAbsent(day, dList -> Lists.newLinkedList()).add(wo);
                }
                result.put(tech, dateMap);
             }
          }
          catch (Exception e)
          {
             log.error("Failed to delete techs calendar events due to: ", e);
          }
       }

       // Sort the work orders by start time
       for (Map<String, List<WO>> woMap : result.values())
       {
          for (List<WO> list : woMap.values())
          {
             list.sort(new WODateComparator());
          }
       }

       return result;
    }

    protected static class WODateComparator implements Comparator<WO>
    {
       @Override
       public int compare(WO o1,
                          WO o2)
       {
          if (o1 == null || o2 == null)
             return 0;

          if (o1.getMetaData() == null || o2.getMetaData() == null)
             return 0;

          if (o1.getMetaData().getItemStatHolderList() == null || o1.getMetaData().getItemStatHolderList().isEmpty() ||
              o2.getMetaData().getItemStatHolderList() == null || o2.getMetaData().getItemStatHolderList().isEmpty())
             return 0;

          ItemStatHolder h1 = o1.getMetaData().getItemStatHolderList().get(0);
          ItemStatHolder h2 = o2.getMetaData().getItemStatHolderList().get(0);

          org.joda.time.DateTime d1 = h1.getScheduledStart();
          org.joda.time.DateTime d2 = h2.getScheduledStart();

          if (d1 == null || d2 == null)
             return 0;

          if (d1.isBefore(d2))
             return -1;
          else if (d1.isAfter(d2))
             return 1;

          return 0;
       }
    }

 }
