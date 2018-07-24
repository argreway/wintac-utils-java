 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      TechProfile.java
  * Created:   7/24/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.config;

 import java.util.List;

 import com.sentryfire.model.SKILL;

 public class TechProfile
 {
    protected String name;
    protected String division;
    protected List<SKILL> skills;
    protected List<SKILL> scheduleSkills;
    protected String home;
    protected String territory;
    protected List<String> customerPref;

    public String getName()
    {
       return name;
    }

    public void setName(String name)
    {
       this.name = name;
    }

    public String getDivision()
    {
       return division;
    }

    public void setDivision(String division)
    {
       this.division = division;
    }

    public List<SKILL> getSkills()
    {
       return skills;
    }

    public void setSkills(List<SKILL> skills)
    {
       this.skills = skills;
    }

    public List<SKILL> getScheduleSkills()
    {
       return scheduleSkills;
    }

    public void setScheduleSkills(List<SKILL> scheduleSkills)
    {
       this.scheduleSkills = scheduleSkills;
    }

    public String getHome()
    {
       return home;
    }

    public void setHome(String home)
    {
       this.home = home;
    }

    public String getTerritory()
    {
       return territory;
    }

    public void setTerritory(String territory)
    {
       this.territory = territory;
    }

    public List<String> getCustomerPref()
    {
       return customerPref;
    }

    public void setCustomerPref(List<String> customerPref)
    {
       this.customerPref = customerPref;
    }

    @Override
    public String toString()
    {
       return "TechProfile{" +
              "name='" + name + '\'' +
              ", division='" + division + '\'' +
              ", skills=" + skills +
              ", scheduleSkills=" + scheduleSkills +
              ", home='" + home + '\'' +
              ", territory='" + territory + '\'' +
              ", customerPref=" + customerPref +
              '}';
    }
 }
