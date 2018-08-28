 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      GraphData.java
  * Created:   8/21/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import java.util.List;

 public class GraphData
 {
    protected String type;
    protected Boolean showInLegend;
    protected String name;
    protected String axisYType;
    protected String color;
    protected List<DataPoint> dataPoints;

    public String getType()
    {
       return type;
    }

    public void setType(String type)
    {
       this.type = type;
    }

    public Boolean getShowInLegend()
    {
       return showInLegend;
    }

    public void setShowInLegend(Boolean showInLegend)
    {
       this.showInLegend = showInLegend;
    }

    public String getName()
    {
       return name;
    }

    public void setName(String name)
    {
       this.name = name;
    }

    public String getAxisYType()
    {
       return axisYType;
    }

    public void setAxisYType(String axisYType)
    {
       this.axisYType = axisYType;
    }

    public String getColor()
    {
       return color;
    }

    public void setColor(String color)
    {
       this.color = color;
    }

    public List<DataPoint> getDataPoints()
    {
       return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints)
    {
       this.dataPoints = dataPoints;
    }

    @Override
    public String toString()
    {
       return "GraphData{" +
              "type='" + type + '\'' +
              ", showInLegend=" + showInLegend +
              ", name='" + name + '\'' +
              ", axisYType='" + axisYType + '\'' +
              ", color='" + color + '\'' +
              ", dataPoints=" + dataPoints +
              '}';
    }
 }
