 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      HelloWorld.java
  * Created:   7/31/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package org.example;

 import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


 public class HelloWorld extends AbstractHandler
 {

    @Override
    public void handle(String s,
                       Request request,
                       HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException
    {
       httpServletResponse.setContentType("text/html;charset=utf-8");
       httpServletResponse.setStatus(HttpServletResponse.SC_OK);
       request.setHandled(true);
       httpServletResponse.getWriter().println("<h1>Hello World</h1>");
    }

//    public static void main(String[] args) throws Exception
//    {
//       Server server = new Server(8080);
//       server.setHandler(new HelloWorld());
//
//       server.start();
//       server.join();
//    }

//    public static void main(String[] args) {
//        Server server = new Server(3131);
//
//        ServletContextHandler contextHandler
//                = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        contextHandler.setContextPath("/");
//
//        ServletHolder sh = new ServletHolder(new VaadinServlet());
//        contextHandler.addServlet(sh, "/*");
//        contextHandler.setInitParameter("ui", HelloWorldUI.class.getCanonicalName());
//        server.setHandler(contextHandler);
//
//        try {
//            server.start();
//            server.join();
//
//        } catch (Exception ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

 }
