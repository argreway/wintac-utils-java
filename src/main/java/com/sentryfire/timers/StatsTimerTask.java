 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      StatsTimerTask.java
  * Created:   6/29/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.timers;

 import java.util.TimerTask;

 import com.sentryfire.business.utils.WIPUtils;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class StatsTimerTask extends TimerTask
 {
    Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void run()
    {
       log.info("Timer fired updating stats...");
       try
       {
          WIPUtils.insertAllStats();
       }
       catch (Exception e)
       {
          log.info("Failed to insert all stats: ", e);
       }
    }
 }
