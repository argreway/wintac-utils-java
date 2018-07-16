 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      CalenderUtils.java
  * Created:   7/16/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.googlecalendar;

 import java.util.Arrays;
 import java.util.List;
 import java.util.stream.Collectors;

 import com.google.api.client.util.DateTime;
 import com.google.api.services.calendar.model.Event;
 import com.google.api.services.calendar.model.EventAttendee;
 import com.google.api.services.calendar.model.EventDateTime;
 import com.google.api.services.calendar.model.EventReminder;

 public class CalenderUtils
 {
    private static final String RECUR_RULE = "RRULE:FREQ=DAILY;COUNT=2";

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

       return event;
    }

 }
