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

 import com.google.maps.model.GeocodingResult;

 public class GeoCodeData implements Serializable
 {
    GeocodingResult geocodingResult;
    Double lat;
    Double lng;
    String woNumber;
    String fullAddress;
    String street;
    String city;
    String zip;

    public GeoCodeData(GeocodingResult geocodingResult,
                       Double lat,
                       Double lng,
                       String woNumber,
                       String fullAddress,
                       String street,
                       String city,
                       String zip)
    {
       this.geocodingResult = geocodingResult;
       this.lat = lat;
       this.lng = lng;
       this.woNumber = woNumber;
       this.fullAddress = fullAddress;
       this.street = street;
       this.city = city;
       this.zip = zip;
    }

    public GeocodingResult getGeocodingResult()
    {
       return geocodingResult;
    }

    public void setGeocodingResult(GeocodingResult geocodingResult)
    {
       this.geocodingResult = geocodingResult;
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

    public String getFullAddress()
    {
       return fullAddress;
    }

    public void setFullAddress(String fullAddress)
    {
       this.fullAddress = fullAddress;
    }

    public String getStreet()
    {
       return street;
    }

    public void setStreet(String street)
    {
       this.street = street;
    }

    public String getCity()
    {
       return city;
    }

    public void setCity(String city)
    {
       this.city = city;
    }

    public String getZip()
    {
       return zip;
    }

    public void setZip(String zip)
    {
       this.zip = zip;
    }

    @Override
    public String toString()
    {
       return "GeoCodeData{" +
              "geocodingResult=" + geocodingResult +
              ", lat=" + lat +
              ", lng=" + lng +
              ", woNumber='" + woNumber + '\'' +
              ", fullAddress='" + fullAddress + '\'' +
              ", street='" + street + '\'' +
              ", city='" + city + '\'' +
              ", zip='" + zip + '\'' +
              '}';
    }
 }
