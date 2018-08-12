 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      GoogleMapsClient.java
  * Created:   5/25/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.googlemaps;

 import java.util.List;

 import com.google.common.collect.Lists;
 import com.google.gson.Gson;
 import com.google.gson.GsonBuilder;
 import com.google.maps.DirectionsApi;
 import com.google.maps.DistanceMatrixApi;
 import com.google.maps.DistanceMatrixApiRequest;
 import com.google.maps.GeoApiContext;
 import com.google.maps.GeocodingApi;
 import com.google.maps.model.DistanceMatrix;
 import com.google.maps.model.DistanceMatrixElement;
 import com.google.maps.model.DistanceMatrixRow;
 import com.google.maps.model.GeocodingResult;
 import com.google.maps.model.TravelMode;
 import com.google.maps.model.Unit;
 import com.sentryfire.config.ExternalConfiguartion;
 import com.sentryfire.model.WO;
 import com.sun.tools.javac.util.Pair;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class GoogleMapsClient
 {
    static Logger log = LoggerFactory.getLogger(GoogleMapsClient.class);

    public void route(List<WO> woList)
    {

    }

    public static void geocodingApi()
    {
       try
       {
          GeoApiContext context = new GeoApiContext.Builder()
             .apiKey(ExternalConfiguartion.getInstance().getGoogleMapApiKey()).build();
          //           PlacesApi.nearbySearchNextPage();
          //
          GeocodingResult[] results = GeocodingApi.geocode(
             context,
             "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
          Gson gson = new GsonBuilder().setPrettyPrinting().create();
          System.out.println(gson.toJson(results[0].addressComponents));
       }
       catch (Exception e)
       {
          log.error("Failed to query googlemaps api: ", e);
       }


    }

    static boolean print = false;


    public static List<Pair<String, String>> getLatLongForAddress()
    {
       List<Pair<String, String>> latLongList = Lists.newArrayList();
       return latLongList;
    }

    public static DistanceMatrix getDistanceMatrix(List<String> origins,
                                                   List<String> destinations)
    {
       try
       {
          GeoApiContext context = new GeoApiContext.Builder()
             .apiKey(ExternalConfiguartion.getInstance().getGoogleMapApiKey()).build();

          DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(context);

          DistanceMatrix matrix = req.origins(origins.toArray(new String[0]))
             .destinations(destinations.toArray(new String[0]))
             .mode(TravelMode.DRIVING)
             .units(Unit.IMPERIAL)
             .avoid(DirectionsApi.RouteRestriction.TOLLS).await();

          if (print)
          {
             int oIdx = 0;
             for (DistanceMatrixRow row : matrix.rows)
             {
                int dIdx = 0;
                for (DistanceMatrixElement element : row.elements)
                {
                   log.error("From: " + origins.get(oIdx) + " To: " + destinations.get(dIdx));
                   log.error("Distance:  " + element.distance);
                   log.error("Duration: " + element.duration);
                   log.error("InTraffic: " + element.durationInTraffic);
                   log.error("Fare: " + element.fare);
                   log.error("Status: " + element.status);
                   log.error("------------------------------");
                   dIdx++;
                }
                oIdx++;
             }
          }
          return matrix;
       }
       catch (Exception e)
       {
          log.error("Failed to query matrix API: ", e);
       }
       return null;
    }

 }
