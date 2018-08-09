 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      ConsoleWebsocket.java
  * Created:   8/7/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.websocket;


 import java.io.IOException;
 import java.util.Set;

 import javax.websocket.OnClose;
 import javax.websocket.OnMessage;
 import javax.websocket.OnOpen;
 import javax.websocket.Session;
 import javax.websocket.server.PathParam;
 import javax.websocket.server.ServerEndpoint;

 import com.google.common.collect.Sets;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;


 @ServerEndpoint ("/websocket/console")
 public class ConsoleWebsocket
 {
    Logger log = LoggerFactory.getLogger(ConsoleWebsocket.class);

    private static Set<Session> consoles = Sets.newHashSet();

    public static Set<Session> getConsoles()
    {
       return consoles;
    }

    @OnMessage
    public void onMessage(String message,
                          Session session) throws IOException,
                                                  InterruptedException
    {
       if (message != null && message.equals("ping"))
          log.debug("Received keep-alive");
       else
          log.info("Client browser sent message: " + message);
    }

    @OnOpen
    public void onOpen(Session session,
                       @PathParam ("username")
                          String username)
    {
       consoles.add(session);
       log.info("Client connected");
    }

    @OnClose
    public void onClose()
    {
       log.info("Connection closed");
    }
 }
