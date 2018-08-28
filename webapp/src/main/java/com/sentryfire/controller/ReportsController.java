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
 import java.util.List;
 import java.util.Random;

 import javax.servlet.ServletException;
 import javax.servlet.annotation.WebServlet;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;

 import com.google.common.collect.Lists;
 import com.google.gson.Gson;
 import com.google.gson.GsonBuilder;
 import com.sentryfire.WebUtilities;
 import com.sentryfire.config.TechProfileConfiguration;
 import com.sentryfire.model.DataPoint;
 import com.sentryfire.model.GraphData;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 @WebServlet (name = "ReportsController", urlPatterns = "/reports")
 public class ReportsController extends HttpServlet
 {
    private static final Logger log = LoggerFactory.getLogger(ReportsController.class);

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

//       String action = request.getParameter("action");
       handleAction(request, response);
    }

    private void handleAction(HttpServletRequest request,
                              HttpServletResponse response) throws IOException
    {
       GraphData graphData = new GraphData();
       graphData.setAxisYType("secondary");
       graphData.setColor("#7E8F74");
       graphData.setName("Work Orders");
       graphData.setShowInLegend(true);
       graphData.setType("stackedBar");
       List<DataPoint> dataPointList = Lists.newArrayList();
       graphData.setDataPoints(dataPointList);

       for (String name : TechProfileConfiguration.getInstance().getDenTechToProfiles().keySet())
       {
          Random rand = new Random();
          DataPoint point = new DataPoint(rand.nextInt(30), name);
          dataPointList.add(point);
       }


       GsonBuilder builder = new GsonBuilder();
       Gson gson = builder.create();

       String result = gson.toJson(graphData);
       response.getWriter().println(result);
    }

 }
