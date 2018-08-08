 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      EventTask.java
  * Created:   7/19/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.model;

 import java.io.Serializable;

 import com.sentryfire.model.WO;
 import org.joda.time.DateTime;

 public class EventTask implements Serializable
 {
    private WO wo;
    private DateTime start;
    private DateTime end;
    private boolean isLunch;
    private boolean lunchBuildIn = false;
    private boolean isScheduledEvent = false;
    private boolean clearedDay = false;

    public EventTask()
    {
    }

    public EventTask(WO wo,
                     DateTime start,
                     DateTime end,
                     boolean isLunch)
    {
       this.wo = wo;
       this.start = start;
       this.end = end;
       this.isLunch = isLunch;
    }

    public WO getWo()
    {
       return wo;
    }

    public void setWo(WO wo)
    {
       this.wo = wo;
    }

    public DateTime getStart()
    {
       return start;
    }

    public void setStart(DateTime start)
    {
       this.start = start;
    }

    public DateTime getEnd()
    {
       return end;
    }

    public void setEnd(DateTime end)
    {
       this.end = end;
    }

    public boolean isLunch()
    {
       return isLunch;
    }

    public void setLunch(boolean lunch)
    {
       isLunch = lunch;
    }

    public boolean isLunchBuildIn()
    {
       return lunchBuildIn;
    }

    public void setLunchBuildIn(boolean lunchBuildIn)
    {
       this.lunchBuildIn = lunchBuildIn;
    }

    public boolean isScheduledEvent()
    {
       return isScheduledEvent;
    }

    public void setScheduledEvent(boolean scheduledEvent)
    {
       isScheduledEvent = scheduledEvent;
    }

    public boolean isClearedDay()
    {
       return clearedDay;
    }

    public void setClearedDay(boolean clearedDay)
    {
       this.clearedDay = clearedDay;
    }

    public Integer getDayOfMonth()
    {
       Integer dayOfMonth = 0;
       if (start != null)
          dayOfMonth = start.getDayOfMonth();
       else if (end != null)
          dayOfMonth = end.getDayOfMonth();
       return dayOfMonth;
    }

    @Override
    public String toString()
    {
       return "EventTask{" +
              "wo=" + wo +
              ", start=" + start +
              ", end=" + end +
              ", isLunch=" + isLunch +
              ", lunchBuildIn=" + lunchBuildIn +
              ", isScheduledEvent=" + isScheduledEvent +
              ", clearedDay=" + clearedDay +
              '}';
    }
 }
