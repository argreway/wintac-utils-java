 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      GeoCodeData.java
  * Created:   8/20/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.model;

 import java.io.Serializable;

 public class GeoCodeData implements Serializable
 {
    Double lat;
    Double lng;
    String woNumber;
    String address;

    public GeoCodeData(Double lat,
                       Double lng,
                       String woNumber,
                       String address)
    {
       this.lat = lat;
       this.lng = lng;
       this.woNumber = woNumber;
       this.address = address;
    }

    public Double getLat()
    {
       return lat;
    }

    public void setLat(Double lat)
    {
       this.lat = lat;
    }

    public Double getLng()
    {
       return lng;
    }

    public void setLng(Double lng)
    {
       this.lng = lng;
    }

    public String getWoNumber()
    {
       return woNumber;
    }

    public void setWoNumber(String woNumber)
    {
       this.woNumber = woNumber;
    }

    public String getAddress()
    {
       return address;
    }

    public void setAddress(String address)
    {
       this.address = address;
    }

    @Override
    public String toString()
    {
       return "GeoCodeData{" +
              "lat='" + lat + '\'' +
              ", lng='" + lng + '\'' +
              ", woNumber='" + woNumber + '\'' +
              ", address='" + address + '\'' +
              '}';
    }
 }
