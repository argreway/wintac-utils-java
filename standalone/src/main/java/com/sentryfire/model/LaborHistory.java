 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      LaborHistory.java
  * Created:   6/20/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import org.influxdb.annotation.Column;

 public class LaborHistory
 {

    @Column (name = "time")
    protected String time;

    @Column (name = "fixedLabor")
    private Double fixedLabor;

    @Column (name = "directLabor")
    private Double directLabor;

    @Column (name = "totalLabor")
    private Double totalLabor;

    @Column (name = "totalCost")
    private Double totalCost;

    @Column (name = "totalRevenue")
    private Double totalRevenue;

    @Column (name = "grossProfit")
    private Double grossProfit;

    @Column (name = "netProfit")
    private Double netProfit;

    @Column (name = "contribMargin")
    private Double contribMargin;

    public String getTime()
    {
       return time;
    }

    public void setTime(String time)
    {
       this.time = time;
    }

    public Double getFixedLabor()
    {
       return fixedLabor;
    }

    public void setFixedLabor(Double fixedLabor)
    {
       this.fixedLabor = fixedLabor;
    }

    public Double getDirectLabor()
    {
       return directLabor;
    }

    public void setDirectLabor(Double directLabor)
    {
       this.directLabor = directLabor;
    }

    public Double getTotalLabor()
    {
       return totalLabor;
    }

    public void setTotalLabor(Double totalLabor)
    {
       this.totalLabor = totalLabor;
    }

    public Double getTotalCost()
    {
       return totalCost;
    }

    public void setTotalCost(Double totalCost)
    {
       this.totalCost = totalCost;
    }

    public Double getTotalRevenue()
    {
       return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue)
    {
       this.totalRevenue = totalRevenue;
    }

    public Double getGrossProfit()
    {
       return grossProfit;
    }

    public void setGrossProfit(Double grossProfit)
    {
       this.grossProfit = grossProfit;
    }

    public Double getNetProfit()
    {
       return netProfit;
    }

    public void setNetProfit(Double netProfit)
    {
       this.netProfit = netProfit;
    }

    public Double getContribMargin()
    {
       return contribMargin;
    }

    public void setContribMargin(Double contribMargin)
    {
       this.contribMargin = contribMargin;
    }

    @Override
    public String toString()
    {
       return "LaborHistory{" +
              "time='" + time + '\'' +
              ", fixedLabor=" + fixedLabor +
              ", directLabor=" + directLabor +
              ", totalLabor=" + totalLabor +
              ", totalCost=" + totalCost +
              ", totalRevenue=" + totalRevenue +
              ", grossProfit=" + grossProfit +
              ", netProfit=" + netProfit +
              ", contribMargin=" + contribMargin +
              '}';
    }
 }
