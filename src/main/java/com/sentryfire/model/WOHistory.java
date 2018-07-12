 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WOHistory.java
  * Created:   7/10/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import org.influxdb.annotation.Column;
 import org.influxdb.annotation.Measurement;

 @Measurement (name = "wo_history")
 public class WOHistory
 {

    @Column (name = "time")
    protected String time;

    @Column (name = "year")
    protected String year;
    @Column (name = "month")
    protected String month;
    @Column (name = "count")
    protected Integer count;
    @Column (name = "jobs")
    protected String jobs;

    public String getTime()
    {
       return time;
    }

    public void setTime(String time)
    {
       this.time = time;
    }

    public String getYear()
    {
       return year;
    }

    public void setYear(String year)
    {
       this.year = year;
    }

    public String getMonth()
    {
       return month;
    }

    public void setMonth(String month)
    {
       this.month = month;
    }

    public Integer getCount()
    {
       return count;
    }

    public void setCount(Integer count)
    {
       this.count = count;
    }

    public String getJobs()
    {
       return jobs;
    }

    public void setJobs(String jobs)
    {
       this.jobs = jobs;
    }

    @Override
    public String toString()
    {
       return "WOHistory{" +
              "time='" + time + '\'' +
              ", year='" + year + '\'' +
              ", month='" + month + '\'' +
              ", count=" + count +
              ", jobs='" + jobs + '\'' +
              '}';
    }
 }
