 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      StatsTimer.java
  * Created:   6/29/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.timers;


 import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
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

    ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    public void startTimer()
    {
       MutableDateTime next = new MutableDateTime();
       next.addDays(1);
       next.setHourOfDay(3);
       next.setMinuteOfHour(0);
       next.setSecondOfMinute(0);

       long period = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

       DateTime now = new DateTime();

       long delay = next.getMillis() - now.getMillis();

       HistoryStatsRunnable runnable = new HistoryStatsRunnable();
//       timer.scheduleAtFixedRate(timerTask, delay, interval);
       // Every day at 3:00 AM
       timer.scheduleAtFixedRate(runnable, delay, period, TimeUnit.MILLISECONDS);

       log.info("TimerTask started - connecting to DB.");

       // Start timer run every day at 3am
       log.info("Timer to run at : " + next + ", interval: " + period + " delay(ms): " + delay);
    }

 }
