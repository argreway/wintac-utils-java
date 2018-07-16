 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      SentryMain.java
  * Created:   5/24/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire;

 import java.util.Arrays;
 import java.util.List;
 import java.util.stream.Collectors;

 import com.google.common.collect.Lists;
 import com.sentryfire.business.schedule.googlecalendar.CalendarManager;
 import com.sentryfire.business.schedule.googlemaps.GoogleMapsClient;
 import com.sentryfire.gui.GUIManager;
 import com.sentryfire.model.WO;
 import com.sentryfire.persistance.DAOFactory;
 import com.sentryfire.timers.StatsTimer;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class SentryMain
 {
    private static Logger log = LoggerFactory.getLogger(SentryMain.class);

    protected static boolean isCli = true;

    protected static StatsTimer timer;

    public static void main(String[] args)
//    public static void test(String[] args)
    {
       try
       {
          SentryAppConfiguartion.getInstance();
          DAOFactory.sqlDB().connectToDB(
             SentryConfiguartion.getInstance().getServer(),
             SentryConfiguartion.getInstance().getDatabase(),
             SentryConfiguartion.getInstance().getUser(),
             SentryConfiguartion.getInstance().getPassword());

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


          // Labor Effeciencies
//       QueryAggregator aggregator = new QueryAggregator();
//       aggregator.laborEfficencyRatiosYearly();

          // Google Maps stuff
          GoogleMapsClient googleMapsClient = new GoogleMapsClient();
          List<WO> woMetaList = googleMapsClient.route();

          CalendarManager calendarManager = new CalendarManager();
//       calendarManager.bulkUpdateWorkOrders(null);
          woMetaList = woMetaList.stream().limit(10).collect(Collectors.toList());
          calendarManager.bulkUpdateWorkOrders(woMetaList);
//          calendarManager.deleteAllEvents();

//       WOHistoryManager woHistory = new WOHistoryManager();
//       woHistory.updateMonthlyWOCount();
          DAOFactory.shutdown();
       }
       catch (Exception e)
       {
          log.error("Failed to run main scheduling app ", e);
       }
    }

    public static void test2(String[] args)
//    public static void main(String[] args)
    {
       log.info("CLI Args " + Arrays.toString(args));

       if (args != null)
       {
          List<String> cli = Lists.newArrayList(args);
          for (String opt : cli)
          {
             if (opt != null && opt.equals("-server"))
             {
                isCli = false;
             }
          }
       }

       if (isCli)
       {
          log.info("Launching cli mode.");
          GUIManager.launchGui();
          DAOFactory.shutdown();
       }
       else
       {
          log.info("Launching server mode.");
          timer = new StatsTimer();
          timer.startTimer();
       }
    }

 }
