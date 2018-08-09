 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WebConsoleLogger.java
  * Created:   8/8/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.console;

 import javax.websocket.Session;

 import com.sentryfire.AppAppender;
 import com.sentryfire.websocket.ConsoleWebsocket;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class WebConsoleLogger implements AppAppender.LoggableApp
 {
    static Logger log = LoggerFactory.getLogger(WebConsoleLogger.class);

    @Override
    public void writeMessage(String message)
    {
       if (ConsoleWebsocket.getConsoles() != null)
       {
          for (Session s : ConsoleWebsocket.getConsoles())
          {
             try
             {
                s.getBasicRemote().sendText(message);
             }
             catch (Exception e)
             {
                e.printStackTrace();
                System.out.println("Failed to send console message due to: " + e);
             }
          }
       }
    }
 }
