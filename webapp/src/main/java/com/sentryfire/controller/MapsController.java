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
 import com.sentryfire.business.schedule.model.GeoCodeData;
 import com.sentryfire.business.utils.SerializerUtils;
 import com.sentryfire.model.WO;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 @WebServlet (name = "MapsController", urlPatterns = "/maps")
 public class MapsController extends HttpServlet
 {
    private static final Logger log = LoggerFactory.getLogger(MapsController.class);

    static Map<String, Map<String, List<WO>>> calMap = CalenderUtils.getCalMap();

    static Map<String, GeoCodeData> geoCodeDataMap = SerializerUtils.deSerializeGeoCodeMap();

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
          handleGetTech(request, response, techID);
       }
       else if (techID != null && dateVal != null)
       {
          handleGetAddress(response, techID, dateVal);
       }
    }

    private void handleGetAddress(HttpServletResponse response,
                                  String techID,
                                  String dateVal) throws IOException
    {
       List<WO> woList = Lists.newArrayList();
       if (dateVal == null || techID == null)
       {
          log.info("Nothing to display - no input.");
       }
       else if (dateVal.equals("ALL"))
       {
          if (calMap.get(techID) != null)
             woList = calMap.get(techID).values().stream().flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList());
       }
       else
       {
          if (calMap.get(techID) != null)
             woList = calMap.get(techID).get(dateVal).stream().filter(Objects::nonNull).sorted(
                Comparator.comparingLong(MapsController::getStartMillis))
                .collect(Collectors.toList());
       }

       response.getWriter().println(WebUtilities.jsonGeoArrayList(woList, geoCodeDataMap));
    }

    private void handleGetTech(HttpServletRequest request,
                               HttpServletResponse response,
                               String techID) throws IOException
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

    protected static Long getStartMillis(WO wo)
    {
       return wo.getMetaData().getItemStatHolderList().get(0).getScheduledStart().getMillis();
    }

 }
