 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      SentryContextListener.java
  * Created:   8/8/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire;

 import javax.servlet.ServletContextEvent;
 import javax.servlet.ServletContextListener;

 import com.sentryfire.console.WebConsoleLogger;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class SentryContextListener implements ServletContextListener
 {
    Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
       log.info("Servlet Starting!");
       AppAppender.registerAppLog(new WebConsoleLogger());
       log.info("Servlet Started!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
       log.info("Servlet Shutdown!");
    }
 }
