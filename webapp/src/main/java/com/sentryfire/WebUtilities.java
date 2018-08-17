 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WebUtilities.java
  * Created:   8/7/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire;

 import java.util.List;
 import java.util.Objects;
 import java.util.stream.Collectors;

 import javax.swing.table.DefaultTableModel;

 import com.google.api.client.util.DateTime;
 import com.google.gson.Gson;
 import com.google.gson.GsonBuilder;
 import com.sentryfire.business.schedule.SchedulerBuilder;
 import com.sentryfire.business.schedule.googlecalendar.CalendarManager;
 import com.sentryfire.config.TechProfileConfiguration;
 import com.sentryfire.model.WO;
 import com.sentryfire.persistance.DAOFactory;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class WebUtilities
 {
    private static Logger log = LoggerFactory.getLogger(WebUtilities.class);

    SchedulerBuilder schedulerBuilder;

    public String getActivityLog(String startStr,
                                 String endStr)
    {
       Long startUtc = Long.parseLong(startStr);
       Long endUtc = Long.parseLong(endStr);
       MutableDateTime start = new MutableDateTime(startUtc);
       start.setHourOfDay(0);

       MutableDateTime end = new MutableDateTime(endUtc);
       end.setHourOfDay(23);

       DefaultTableModel model = DAOFactory.sqlDB().getUserActivityLog(start.toDateTime(), end.toDateTime());
       return "RowCount = " + model.getRowCount();
    }

    public String buildSchedule(String startStr,
                                String endStr)
    {
       if (schedulerBuilder == null)
          schedulerBuilder = new SchedulerBuilder();
       Long startUtc = Long.parseLong(startStr);
       Long endUtc = Long.parseLong(endStr);
       MutableDateTime start = new MutableDateTime(startUtc);
       start.setHourOfDay(0);

       MutableDateTime end = new MutableDateTime(endUtc);
       end.setHourOfDay(23);

       schedulerBuilder.buildAndInsertAllSchedules(start.toDateTime());
       return "Schedule Building Done";
    }

    public String deleteSchedule(String startStr,
                                 String endStr)
    {
       if (schedulerBuilder == null)
          schedulerBuilder = new SchedulerBuilder();

       Long startUtc = Long.parseLong(startStr);
       Long endUtc = Long.parseLong(endStr);
       MutableDateTime start = new MutableDateTime(startUtc);
       start.setHourOfDay(0);

       MutableDateTime end = new MutableDateTime(endUtc);
       end.setHourOfDay(23);

       for (String tech : TechProfileConfiguration.getInstance().getDenTechToProfiles().keySet())
       {
          try
          {
             log.info("Deleting " + tech);
             CalendarManager.getInstance().deleteAllCalendarEvents(tech, new DateTime(start.getMillis()), new DateTime(end.getMillis()));
          }
          catch (Exception e)
          {
             log.error("Failed to delete techs calendar events due to: ", e);
          }
       }
       return "Schedule Building Done";

    }

    public static String jsonArrayList(List<WO> wos)
    {

       List<String> addressList = wos.stream().map(WO::getFullAddress).filter(Objects::nonNull).collect(Collectors.toList());
       GsonBuilder builder = new GsonBuilder();
       Gson gson = builder.create();

       return gson.toJson(addressList);
    }

 }
