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
 import java.util.Comparator;
 import java.util.List;
 import java.util.Map;
 import java.util.Objects;
 import java.util.stream.Collectors;

 import javax.servlet.ServletException;
 import javax.servlet.annotation.WebServlet;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;

 import com.google.common.collect.Lists;
 import com.sentryfire.WebUtilities;
 import com.sentryfire.business.schedule.googlecalendar.CalenderUtils;
 import com.sentryfire.model.WO;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 @WebServlet (name = "MapsController", urlPatterns = "/maps")
 public class MapsController extends HttpServlet
 {
    private static final Logger log = LoggerFactory.getLogger(MapsController.class);

    static Map<String, Map<String, List<WO>>> calMap = CalenderUtils.getCalMap();

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

       String techID = request.getParameter("tech");
       String dateVal = request.getParameter("date");

       if (techID != null && dateVal == null)
       {
          response.getWriter().println("");
          Map<String, List<WO>> techMap = calMap.get(techID);

          List<String> dateList = Lists.newArrayList();
          if (techMap != null)
             dateList.addAll(techMap.keySet());
          request.setAttribute("dateList", dateList);

          dateList.sort(new DateComparator());
          for (String date : dateList)
          {
             response.getWriter().println("<option>" + date + "</option>");
          }
          response.getWriter().println("<option>ALL</option>");
       }
       else if (techID != null && dateVal != null)
       {
          List<WO> woList;
          if (dateVal.equals("ALL"))
             woList = calMap.get(techID).values().stream().flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList());
          else
             woList = calMap.get(techID).get(dateVal).stream().filter(Objects::nonNull).sorted(
                Comparator.comparingLong(MapsController::getStartMillis))
                .collect(Collectors.toList());

          response.getWriter().println(WebUtilities.jsonArrayList(woList));
       }
    }

    protected static Long getStartMillis(WO wo)
    {
       return wo.getMetaData().getItemStatHolderList().get(0).getScheduledStart().getMillis();
    }

    class DateComparator implements Comparator<String>
    {

       @Override
       public int compare(String o1,
                          String o2)
       {
          if (o1 == null && o2 != null)
             return 1;
          else if (o1 != null && o2 == null)
             return -1;

          String[] p1 = o1.split("-");
          String[] p2 = o2.split("-");

          Integer p1year = Integer.parseInt(p1[2]);
          Integer p2year = Integer.parseInt(p2[2]);
          Integer p1mon = Integer.parseInt(p1[1]);
          Integer p2mon = Integer.parseInt(p2[1]);
          Integer p1day = Integer.parseInt(p1[0]);
          Integer p2day = Integer.parseInt(p2[0]);

          if (p1year > p2year)
             return -1;
          if (p1year < p2year)
             return 1;

          if (p1mon > p2mon)
             return 1;
          if (p1mon < p2mon)
             return -1;

          if (p1day > p2day)
             return 1;
          if (p1day < p2day)
             return -1;

          return 0;
       }

       @Override
       public boolean equals(Object obj)
       {
          return false;
       }
    }

 }
