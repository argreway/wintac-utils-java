 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      ActivityLog.java
  * Created:   7/17/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import java.util.List;

 public class ActivityLog
 {

    protected String name;
    protected String date;
    protected String time;
    protected String action;

    protected String cn;
    protected String in;

    protected List<String> invDate;
    protected List<String> jDate;
    protected List<String> dDate;

    public String getName()
    {
       return name;
    }

    public void setName(String name)
    {
       this.name = name;
    }

    public String getDate()
    {
       return date;
    }

    public void setDate(String date)
    {
       this.date = date;
    }

    public String getTime()
    {
       return time;
    }

    public void setTime(String time)
    {
       this.time = time;
    }

    public String getAction()
    {
       return action;
    }

    public void setAction(String action)
    {
       this.action = action;
    }

    public String getCn()
    {
       return cn;
    }

    public void setCn(String cn)
    {
       this.cn = cn;
    }

    public String getIn()
    {
       return in;
    }

    public void setIn(String in)
    {
       this.in = in;
    }

    public List<String> getInvDate()
    {
       return invDate;
    }

    public void setInvDate(List<String> invDate)
    {
       this.invDate = invDate;
    }

    public List<String> getjDate()
    {
       return jDate;
    }

    public void setjDate(List<String> jDate)
    {
       this.jDate = jDate;
    }

    public List<String> getdDate()
    {
       return dDate;
    }

    public void setdDate(List<String> dDate)
    {
       this.dDate = dDate;
    }

    @Override
    public String toString()
    {
       return "ActivityLog{" +
              "name='" + name + '\'' +
              ", date='" + date + '\'' +
              ", time='" + time + '\'' +
              ", action='" + action + '\'' +
              ", cn='" + cn + '\'' +
              ", in='" + in + '\'' +
              ", invDate=" + invDate +
              ", jDate=" + jDate +
              ", dDate=" + dDate +
              '}';
    }
 }
