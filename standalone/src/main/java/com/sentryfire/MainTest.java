 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      MainTest.java
  * Created:   8/9/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire;

 import com.sentryfire.business.schedule.SchedulerBuilder;
 import com.sentryfire.persistance.DAOFactory;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class MainTest
 {

    static Logger log = LoggerFactory.getLogger(MainTest.class);

    public static void main(String[] args)
    {
       try
       {
          // Robo Dialer
//       DailerManager manager = new DailerManager();
//       manager.start();
//       TwilioDailer twilioDailer = new TwilioDailer();
//       twilioDailer.sendCall();

          // Excel Spread Sheet Test
//       List<AccountRecievable> result = DAOFactory.getArDao().getFilteredARRecordsOlderThan2Years();
//       List<List<Object>> rows = Lists.newArrayList();
//       List<String> columns = result.get(0).getColumnNames();
//       result.forEach(ar -> rows.add(ar.getAllValuesAsList()));
//       ExcelWritter excelWritter = new ExcelWritter();
//       excelWritter.writeSpreadSheet(columns, rows);

//          Events es = CalendarManager.getInstance().listEvents("DH");
//          for (String tech : TechProfileConfiguration.getInstance().getDenTechToProfiles().keySet())
//          {
//
//             if (!tech.equals("CG"))
//             {
//                log.info("Deleting " + tech);
//                CalendarManager.getInstance().deleteAllCalendarEvents(tech);
//             }
//          }

//          GoogleMapsClient maps = new GoogleMapsClient();
//          maps.matrix();

          MutableDateTime start = new MutableDateTime();
          start.setYear(2018);
          start.setMonthOfYear(9);
          start.setDayOfMonth(1);
          start.setHourOfDay(0);
          start.setMinuteOfHour(0);
          start.setSecondOfMinute(0);
          start.setMillisOfSecond(0);

          SchedulerBuilder schedulerBuilder = new SchedulerBuilder();
//          List<WO> wos = SchedulerBuilder.getWorkOrderList(start.toDateTime(), true);
          schedulerBuilder.buildAndInsertAllSchedules(start.toDateTime(), "TG");

          DAOFactory.shutdown();
       }
       catch (Exception e)
       {
          log.error("Failed to run main scheduling app ", e);
       }
    }

 }
