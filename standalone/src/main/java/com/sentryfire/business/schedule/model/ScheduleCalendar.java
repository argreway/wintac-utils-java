 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      ScheduleCalendar.java
  * Created:   7/19/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.model;

 import java.util.List;
 import java.util.Map;
 import java.util.Set;

 import com.google.api.services.calendar.model.Event;
 import com.google.common.collect.Maps;
 import com.sentryfire.model.WO;
 import org.joda.time.DateTime;

 public class ScheduleCalendar
 {
    private Map<String, MonthlyCalendar> techCalendars;

    public ScheduleCalendar(Set<String> techs,
                            DateTime start)
    {
       techCalendars = Maps.newHashMap();
       techs.forEach(t -> techCalendars.put(t, new MonthlyCalendar(start, t)));
    }

    public Map<String, MonthlyCalendar> getTechCalendars()
    {
       return techCalendars;
    }

    public void setTechCalendars(Map<String, MonthlyCalendar> techCalendars)
    {
       this.techCalendars = techCalendars;
    }

    public void insertScheduledEvents(Map<String, List<Event>> techToEvents,
                                      Map<String, WO> in2toWO)
    {
       for (Map.Entry<String, List<Event>> entry : techToEvents.entrySet())
       {
          MonthlyCalendar techCal = techCalendars.get(entry.getKey());
          if (techCal != null)
             techCal.insertEventList(entry.getValue(), in2toWO);
       }
    }

    @Override
    public String toString()
    {
       return "ScheduleCalendar{" +
              "techCalendars=" + techCalendars +
              '}';
    }
 }
