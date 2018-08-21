 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      MapsController.java
  * Created:   8/12/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.controller;

 import java.io.IOException;
 import java.util.Arrays;

 import javax.servlet.ServletException;
 import javax.servlet.annotation.WebServlet;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;

 import com.sentryfire.WebUtilities;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 @WebServlet (name = "ScheduleController", urlPatterns = "/schedule")
 public class ScheduleController extends HttpServlet
 {
    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);

    WebUtilities webUtilities = new WebUtilities();

    /**
     * Simply selects the home view to render by returning its name.
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
       throws ServletException, IOException
    {

       processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
       throws ServletException, IOException
    {

       processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request,
                                HttpServletResponse response)
       throws ServletException, IOException
    {

       String[] techs = request.getParameterValues("tech[]");
       String action = request.getParameter("action");
       String begingDate = request.getParameter("beginDate");
       String endDate = request.getParameter("endDate");

       if (techs != null && action != null && begingDate != null && endDate != null)
       {
          handleAction(request, response, action, techs, begingDate, endDate);
       }
       else
       {
          response.getWriter().println("Please provide tech, action, start, and end date!");
       }
    }

    private void handleAction(HttpServletRequest request,
                              HttpServletResponse response,
                              String action,
                              String[] techs,
                              String begingDate,
                              String endDate) throws IOException
    {

       if (action.equals("load"))
       {
//          webUtilities.getActivityLog(begingDate, endDate);
       }
       else if (action.equals("build"))
       {
//          webUtilities.buildSchedule(begingDate, endDate);
       }
       else if (action.equals("delete"))
       {
//          webUtilities.deleteSchedule(begingDate, endDate);
       }
       else if (action.equals("clear"))
       {
          //
       }

       response.getWriter().println("Got [" + Arrays.toString(techs) + "] [" + action + "][" + begingDate + "][" + endDate + "]");
    }

 }
