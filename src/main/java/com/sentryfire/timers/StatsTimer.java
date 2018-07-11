 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      StatsTimer.java
  * Created:   6/29/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.timers;


 import java.util.Timer;

 import com.sentryfire.SentryConfiguartion;
 import com.sentryfire.persistance.DAOFactory;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class StatsTimer
 {
    Logger log = LoggerFactory.getLogger(getClass());

    // 15 mins
    protected long delay = 10000;
//    protected long delay = 900000;
    // 24 hours
    protected long interval = 86400000;
    Timer timer = new Timer(true);

    public void startTimer()
    {
       StatsTimerTask timerTask = new StatsTimerTask();
       timer.scheduleAtFixedRate(timerTask, delay, interval);
       log.info("TimerTask started");

       DAOFactory.sqlDB().connectToDB(
          SentryConfiguartion.getInstance().getServer(),
          SentryConfiguartion.getInstance().getDatabase(),
          SentryConfiguartion.getInstance().getUser(),
          SentryConfiguartion.getInstance().getPassword());

       // Start timer run every 24 hours
       log.info("Starting timer with delay: " + delay + ", interval: " + interval);
    }

    public void cancelTimer()
    {
       log.info("Cancelling timer.");
       timer.cancel();
       log.info("Disposed of timer.");
    }

 }
