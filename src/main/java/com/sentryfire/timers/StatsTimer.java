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
 import java.util.concurrent.TimeUnit;

 import com.sentryfire.SentryConfiguartion;
 import com.sentryfire.persistance.DAOFactory;
 import org.joda.time.DateTimeZone;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class StatsTimer
 {
    Logger log = LoggerFactory.getLogger(getClass());

    // 15 mins
    protected long delay = 900000;
    // 24 hours
    protected long interval = 86400000;

    Timer timer = new Timer(true);

    public void startTimer()
    {
       MutableDateTime next = new MutableDateTime(DateTimeZone.UTC);
       next.addDays(1);
       next.setHourOfDay(3);
       next.setMinuteOfHour(0);
       next.setSecondOfMinute(0);

       long period = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

       StatsTimerTask timerTask = new StatsTimerTask();
//       timer.scheduleAtFixedRate(timerTask, delay, interval);
       // Every day at 3:00 AM
       timer.schedule(timerTask, next.toDate(), period);

       log.info("TimerTask started - connecting to DB.");

       DAOFactory.sqlDB().connectToDB(
          SentryConfiguartion.getInstance().getServer(),
          SentryConfiguartion.getInstance().getDatabase(),
          SentryConfiguartion.getInstance().getUser(),
          SentryConfiguartion.getInstance().getPassword());

       // Start timer run every day at 3am
       log.info("Timer to run at : " + next + ", interval: " + period);
    }

    public void cancelTimer()
    {
       log.info("Cancelling timer.");
       timer.cancel();
       log.info("Disposed of timer.");
    }

 }
