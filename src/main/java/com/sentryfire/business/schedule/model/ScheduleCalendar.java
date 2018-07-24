 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      ScheduleCalendar.java
  * Created:   7/19/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.model;

 import java.util.Map;
 import java.util.Set;

 import com.google.common.collect.Maps;
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

    @Override
    public String toString()
    {
       return "ScheduleCalendar{" +
              "techCalendars=" + techCalendars +
              '}';
    }
 }
