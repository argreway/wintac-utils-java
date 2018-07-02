 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      SentryMain.java
  * Created:   5/24/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire;

 import java.util.List;

 import com.google.common.collect.Lists;
 import com.sentryfire.gui.GUIManager;
 import com.sentryfire.persistance.DAOFactory;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class SentryMain
 {
    private static Logger log = LoggerFactory.getLogger(SentryMain.class);

    protected static boolean isCli = true;

    public static void main(String[] args)
    {

       if (args != null)
       {
          List<String> cli = Lists.newArrayList();
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
       }
       else
       {
          log.info("Launching server mode.");
       }

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
//       aggregator.laborEfficencyRatios();

       // Google Maps stuff
//       GoogleMapsClient googleMapsClient = new GoogleMapsClient();
//       googleMapsClient.route();

//       WOHistory woHistory = new WOHistory();
//       woHistory.updateMonthlyWOCount();


       DAOFactory.shutdown();
    }

 }
