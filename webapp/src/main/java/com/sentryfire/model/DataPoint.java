 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      DataPoint.java
  * Created:   8/21/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 public class DataPoint
 {
    protected int y;
    protected String label;

    public DataPoint(int y,
                     String label)
    {
       this.y = y;
       this.label = label;
    }

    public int getY()
    {
       return y;
    }

    public void setY(int y)
    {
       this.y = y;
    }

    public String getLabel()
    {
       return label;
    }

    public void setLabel(String label)
    {
       this.label = label;
    }

    @Override
    public String toString()
    {
       return "DataPoint{" +
              "y=" + y +
              ", label='" + label + '\'' +
              '}';
    }
 }
