 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      GoogleMapsClient.java
  * Created:   5/25/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.googlemaps;

 import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import com.sentryfire.business.schedule.model.DistanceData;
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

    public static Map<String, Map<String, DistanceData>> getFullMeshMatrix(List<WO> woList)
    {
       Map<String, Map<String, DistanceData>> matrix = Maps.newHashMap();

       List<String> locations = Lists.newArrayList(MapUtils.LOCATIONS_OFFICE);
       List<String> woNums = Lists.newArrayList("0");


       for (WO wo : woList)
       {
          String location = wo.getFullAddress();
          locations.add(location);
          woNums.add(wo.getIN2());
       }

       List<String> orig = Lists.newArrayList(locations);
       int origIdx = 0;
       while (orig.size() > 0)
       {
          int origChunk = orig.size() >= 10 ? 10 : orig.size();
          List<String> currentOrig = orig.subList(0, origChunk);
          orig = orig.subList(origChunk, orig.size());
          log.info("Fetching distance for chunk [" + origIdx + "] to [" + (origIdx + origChunk) + "].");

          List<String> dest = Lists.newArrayList(locations);
          int destIdx = 0;
          while (dest.size() > 0)
          {
             int destChunk = dest.size() >= 10 ? 10 : dest.size();
             List<String> currentDest = dest.subList(0, destChunk);
             dest = dest.subList(destChunk, dest.size());

             DistanceMatrix googleMatrix = getDistanceMatrix(currentOrig, currentDest);
             if (googleMatrix != null)
             {
                int dOrigIdx = origIdx;
                for (DistanceMatrixRow gRow : googleMatrix.rows)
                {
                   String origWO = woNums.get(dOrigIdx);
                   Map<String, DistanceData> row = matrix.get(origWO);
                   if (row == null)
                   {
                      row = Maps.newHashMap();
                      matrix.put(origWO, row);
                   }

                   int dDistIdx = destIdx;
                   for (DistanceMatrixElement e : gRow.elements)
                   {
                      String destWO = woNums.get(dDistIdx);
                      if (row.containsKey(destWO))
                         log.error("Duplicate " + destWO);
                      row.put(destWO, new DistanceData(e.distance.inMeters, e.duration.inSeconds));
                      dDistIdx++;
                   }
                   dOrigIdx++;
                }
             }

             destIdx += destChunk;
          }
          origIdx += origChunk;
       }

       // Sort closest to farthest
       for (Map.Entry<String, Map<String, DistanceData>> entry : matrix.entrySet())
       {
          Map<String, DistanceData> sortedMap = sortByClosestDistanceFirst(entry.getValue());
          matrix.put(entry.getKey(), sortedMap);
       }
       return matrix;
    }

    public static Map<String, DistanceData> sortByClosestDistanceFirst(Map<String, DistanceData> map)
    {
       List<Map.Entry<String, DistanceData>> list = new ArrayList<>(map.entrySet());

       list.sort(Map.Entry.comparingByValue(Comparator.comparingLong(DistanceData::getDistance)));

       Map<String, DistanceData> result = new LinkedHashMap<>();
       for (Map.Entry<String, DistanceData> entry : list)
       {
          result.put(entry.getKey(), entry.getValue());
       }

       return result;
    }

    public static Map<String, DistanceData> sortByFarthestDistanceFirst(Map<String, DistanceData> map)
    {
       List<Map.Entry<String, DistanceData>> list = new ArrayList<>(map.entrySet());

       list.sort(Map.Entry.comparingByValue((f1, f2) -> Long.compare(f2.getDistance(), f1.getDistance())));

       Map<String, DistanceData> result = new LinkedHashMap<>();
       for (Map.Entry<String, DistanceData> entry : list)
       {
          result.put(entry.getKey(), entry.getValue());
       }

       return result;
    }

    public static DistanceMatrix getDistanceMatrix(List<String> origins,
                                                   List<String> destinations)
    {
       try
       {
          GeoApiContext context = new GeoApiContext.Builder()
             .apiKey(ExternalConfiguartion.getInstance().getGoogleMapApiKey()).readTimeout(20, TimeUnit.SECONDS).build();

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
