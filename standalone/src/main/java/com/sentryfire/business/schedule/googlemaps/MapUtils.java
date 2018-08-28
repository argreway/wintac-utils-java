 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      MapUtils.java
  * Created:   7/29/18
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
 import java.util.Objects;
 import java.util.Set;
 import java.util.stream.Collectors;

 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.google.maps.model.GeocodingResult;
 import com.sentryfire.business.schedule.model.DistanceData;
 import com.sentryfire.business.schedule.model.GeoCodeData;
 import com.sentryfire.business.utils.SerializerUtils;
 import com.sentryfire.config.TechProfile;
 import com.sentryfire.model.WO;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class MapUtils
 {
    static Logger log = LoggerFactory.getLogger(MapUtils.class);

    public static final String LOCATIONS_OFFICE = "1294 South Inca St Denver CO 80023";

    /**
     * Will update the map if needed with new addresses if needed
     */
    public static Map<String, GeoCodeData> geoCodeWOList(List<WO> woList)
    {
       Map<String, GeoCodeData> resultIn2ToGeo = Maps.newTreeMap();

       Map<String, GeoCodeData> geoCodeDataMap = SerializerUtils.deSerializeGeoCodeMap();
       if (geoCodeDataMap == null)
          geoCodeDataMap = Maps.newTreeMap();

       woList = addOfficeWO(woList);

       int i = 0;
       for (WO wo : woList)
       {
          if (i % 25 == 0)
             log.info("Geo-coding [" + i + "] of [" + woList.size() + "]");
          i++;
          String addr = wo.getFullAddress();
          GeoCodeData data = geoCodeDataMap.get(addr);
          if (data == null)
          {
             GeocodingResult result = GoogleMapsClient.geocodeAddress(addr);
             if (result == null)
             {
                log.error("Failed to get geometry for address [" + addr + "] in2 [" + wo.getIN2() + "]");
                continue;
             }
             data = new GeoCodeData(result, result.geometry.location.lat, result.geometry.location.lng,
                                    wo.getIN2(), addr, wo.getADR1(), wo.getCITY(), wo.getZIP());
             geoCodeDataMap.put(addr, data);
          }
          resultIn2ToGeo.put(wo.getIN2(), data);
       }
       SerializerUtils.serializeGeoCodeMap(geoCodeDataMap);
       return resultIn2ToGeo;
    }

    public static Map<String, GeoCodeData> geoCodeTechTerritory(Map<String, TechProfile> profileMap)
    {
       Map<String, GeoCodeData> resultMap = Maps.newTreeMap();

       Map<String, GeoCodeData> geoCodeDataMap = SerializerUtils.deSerializeGeoCodeMap();
       if (geoCodeDataMap == null)
          geoCodeDataMap = Maps.newTreeMap();

       for (TechProfile tech : profileMap.values())
       {
          String addr = tech.getTerritory();
          if (addr == null || addr.isEmpty())
             continue;

          GeoCodeData data = geoCodeDataMap.get(tech.getName());
          if (data == null)
          {
             GeocodingResult result = GoogleMapsClient.geocodeAddress(addr);
             if (result == null)
             {
                log.error("Failed to get geometry for tech [" + tech.getName() + "] address [" + addr + "]");
                continue;
             }
             data = new GeoCodeData(result, result.geometry.location.lat, result.geometry.location.lng,
                                    null, null, null, null, null);
          }
          geoCodeDataMap.put(tech.getName(), data);
          resultMap.put(tech.getName(), data);
       }
       SerializerUtils.serializeGeoCodeMap(geoCodeDataMap);
       return resultMap;
    }

    public static Map<String, Map<String, Double>> calculateDistanceMatrix(Map<String, GeoCodeData> sourceMap,
                                                                           Map<String, GeoCodeData> destMap)
    {
       Map<String, Map<String, Double>> result = Maps.newTreeMap();

       log.info("Begin distance computation");
       for (Map.Entry<String, GeoCodeData> p1 : sourceMap.entrySet())
       {
          Map<String, Double> row = result.computeIfAbsent(p1.getKey(), r -> Maps.newTreeMap());
          for (Map.Entry<String, GeoCodeData> p2 : destMap.entrySet())
          {
             double distance = distance(p1.getValue().getLat(), p1.getValue().getLng(),
                                        p2.getValue().getLat(), p2.getValue().getLng());
             row.put(p2.getKey(), distance);
          }
       }
       log.info("End distance computation");

       // Sort closest to farthest
       for (Map.Entry<String, Map<String, Double>> entry : result.entrySet())
       {
          Map<String, Double> sortedMap = MapUtils.sortByClosestDistanceFirstDouble(entry.getValue());
          result.put(entry.getKey(), sortedMap);
       }
       return result;
    }

    public static Map<String, Map<String, Double>> filterFullMatrix(List<WO> woList,
                                                                    Map<String, Map<String, Double>> fullMatrix)
    {
       Map<String, Map<String, Double>> filteredMatrix = Maps.newTreeMap();
       Set<String> woNumbers = woList.stream().map(WO::getIN2).filter(Objects::nonNull).collect(Collectors.toSet());

       for (Map.Entry<String, Map<String, Double>> entry : fullMatrix.entrySet())
       {
          if (woNumbers.contains(entry.getKey()) || entry.getKey().equals("0"))
          {
             Map<String, Double> row = filteredMatrix.computeIfAbsent(entry.getKey(), e -> Maps.newTreeMap());
             for (Map.Entry<String, Double> entry2 : entry.getValue().entrySet())
             {
                if (woNumbers.contains(entry2.getKey()) || entry2.getKey().equals("0"))
                   row.put(entry2.getKey(), entry2.getValue());
             }
          }
       }

       return filteredMatrix;
    }

    public static Map<String, Double> sortByClosestDistanceFirstDouble(Map<String, Double> map)
    {
       List<Map.Entry<String, Double>> list = new ArrayList<>(map.entrySet());

       list.sort(Map.Entry.comparingByValue());

       Map<String, Double> result = new LinkedHashMap<>();
       for (Map.Entry<String, Double> entry : list)
       {
          result.put(entry.getKey(), entry.getValue());
       }

       return result;
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

    public static Map<String, Double> sortByFarthestDistanceFirst(Map<String, Double> map)
    {
       List<Map.Entry<String, Double>> list = new ArrayList<>(map.entrySet());

       list.sort(Map.Entry.comparingByValue((f1, f2) -> Double.compare(f2, f1)));

       Map<String, Double> result = new LinkedHashMap<>();
       for (Map.Entry<String, Double> entry : list)
       {
          result.put(entry.getKey(), entry.getValue());
       }

       return result;
    }

    public static String findClosestAssignedTech(Map<String, WO> in2ToWO,
                                                 Map<String, Double> row)
    {
       WO result = null;

       int i = 0;
       for (String in2 : row.keySet())
       {
          if (i++ == 0)
             continue;
          WO temp = in2ToWO.get(in2);
          if (temp != null && temp.getMetaData().getTechsOnSite() != null &&
              temp.getMetaData().getTechsOnSite().size() > 0)
          {
             result = temp;
             break;
          }
       }

       if (result == null)
          return null;

       return result.getMetaData().getTechsOnSite().iterator().next();
    }

    //////////////

    protected static List<WO> addOfficeWO(List<WO> wos)
    {
       List<WO> result = Lists.newArrayList();

       WO shopWO = new WO();
       shopWO.setIN2("0");
       shopWO.setADR1("1294 South Inca St");
       shopWO.setCITY("Denver");
       shopWO.setZIP("80223");

       result.add(shopWO);
       result.addAll(wos);
       return result;
    }

    /**
     * Returns meters unit
     */
    public static double distance(double lat1,
                                  double lon1,
                                  double lat2,
                                  double lon2)
    {
       double theta = lon1 - lon2;
       double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
       dist = Math.acos(dist);
       dist = rad2deg(dist);

       // Miles
       dist = dist * 60 * 1.1515;

       // Meters
       dist = dist * 1609.344;

       return (dist);
    }

    private static double deg2rad(double deg)
    {
       return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad)
    {
       return (rad * 180 / Math.PI);
    }
 }
