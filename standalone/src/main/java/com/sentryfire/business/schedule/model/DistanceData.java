 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      DistanceData.java
  * Created:   8/14/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.model;

 import java.io.Serializable;

 public class DistanceData implements Serializable
 {
    long distance;
    long duration;

    public DistanceData(long distance,
                        long duration)
    {
       this.distance = distance;
       this.duration = duration;
    }

    public long getDistance()
    {
       return distance;
    }

    public void setDistance(long distance)
    {
       this.distance = distance;
    }

    public long getDuration()
    {
       return duration;
    }

    public void setDuration(long duration)
    {
       this.duration = duration;
    }

    @Override
    public String toString()
    {
       return "DistanceData{" +
              "distance=" + distance +
              ", duration=" + duration +
              '}';
    }
 }
