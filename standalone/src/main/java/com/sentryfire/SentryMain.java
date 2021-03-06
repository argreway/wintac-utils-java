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

 import com.google.common.collect.Lists;
 import com.sentryfire.gui.GUIManager;
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
