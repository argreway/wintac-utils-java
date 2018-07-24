 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      ItemStatHolder.java
  * Created:   7/12/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import java.io.Serializable;

 import com.sentryfire.business.schedule.model.EventTask;

 public class ItemStatHolder implements Serializable
 {
    protected Integer min;
    protected Integer count;
    protected String itemCode;
    protected SKILL skill;
    protected String tech;
    protected EventTask eventTask;

    public ItemStatHolder(Integer min,
                          Integer count,
                          String itemCode,
                          SKILL skill)
    {
       this.min = min;
       this.count = count;
       this.itemCode = itemCode;
       this.skill = skill;
    }

    public Integer getMin()
    {
       return min;
    }

    public void setMin(Integer min)
    {
       this.min = min;
    }

    public Integer getCount()
    {
       return count;
    }

    public void setCount(Integer count)
    {
       this.count = count;
    }

    public String getItemCode()
    {
       return itemCode;
    }

    public void setItemCode(String itemCode)
    {
       this.itemCode = itemCode;
    }

    public SKILL getSkill()
    {
       return skill;
    }

    public void setSkill(SKILL skill)
    {
       this.skill = skill;
    }

    public String getTech()
    {
       return tech;
    }

    public void setTech(String tech)
    {
       this.tech = tech;
    }

    public EventTask getEventTask()
    {
       return eventTask;
    }

    public void setEventTask(EventTask eventTask)
    {
       this.eventTask = eventTask;
    }

    @Override
    public String toString()
    {
       return "ItemStatHolder{" +
              "min=" + min +
              ", count=" + count +
              ", itemCode='" + itemCode + '\'' +
              ", skill=" + skill +
              ", tech='" + tech + '\'' +
              ", eventTask=" + eventTask +
              '}';
    }
 }
