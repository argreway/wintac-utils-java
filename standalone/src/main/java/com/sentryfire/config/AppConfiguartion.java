 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      ExternalConfiguartion.java
  * Created:   6/1/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.config;

 import java.io.InputStream;
 import java.util.Arrays;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;

 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.sentryfire.model.SKILL;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class AppConfiguartion extends BaseConfiguration
 {
    Logger log = LoggerFactory.getLogger(AppConfiguartion.class);

    //*******************//
    // Local
    //*******************//
    protected Map<String, Integer> itemsToTime;
    protected Map<String, SKILL> itemsToSkill;

    //*******************//
    // File Location
    //*******************//
    public static final String FILE = "sentry-app.properties";
    public static final String CONFIG_FILE = "conf/" + FILE;

    //*******************//
    // Properties
    //*******************//

    protected static final String ITEMS_TIME_MINS = "itemsTimeMin";
    protected static final String ITEM_SKILL = "itemSkill";

    protected static final String DRIVE_TIME = "driveTime";

    protected static final String LUNCH_TIME = "lunchTime";
    protected static final String LUNCH_WINDOW_BEGIN_HR = "lunchWindowBeginHr";
    protected static final String LUNCH_WINDOW_END_HR = "lunchWindowEndHr";

    protected static final String EMPTY_TIME_SLOT_MINIMUM_MIN = "emtpyTimeSlotMinimumMin";

    protected static final String BEGIN_TIME_MIN = "beginTimeMin";
    protected static final String BEGIN_TIME_HR = "beginTimeHr";
    protected static final String END_TIME_HR = "endTimeHr";
    protected static final String END_TIME_MIN = "endTimeMin";

    protected static final String CAL_REMINDER_MIN = "calReminderMins";
    protected static final String CAL_EMAIL_DESC = "calEmailDesc";
    protected static final String CAL_SCHEDULER_EMAIL = "calSchedulerEmail";

    protected static final String DATA_DIR_BASE = "dataDirBase";

    //*******************//
    // Constructors
    //*******************//

    private static AppConfiguartion instance;

    public static AppConfiguartion getInstance()
    {
       if (instance == null)
       {
          instance = new AppConfiguartion();
          instance.getItemTimeMinsMap();
          instance.getItemToSkill();
       }
       return instance;
    }

    public AppConfiguartion()
    {
       properties = new Properties();

       try
       {
          InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
          if (input == null)
             input = getClass().getClassLoader().getResourceAsStream(FILE);
          properties.load(input);
       }
       catch (Exception e)
       {
          log.error("Failed to load the configuration file: " + CONFIG_FILE + ".", e);
       }
    }

    //*******************//
    // Getters
    //*******************//
    public Integer getDriveTime()
    {
       String val = getString(DRIVE_TIME, "45");
       return Integer.parseInt(val);
    }

    public Integer getLunchTime()
    {
       String val = getString(LUNCH_TIME, "30");
       return Integer.parseInt(val);
    }

    public Integer getBeginDayHour()
    {
       String val = getString(BEGIN_TIME_HR, "7");
       return Integer.parseInt(val);
    }

    public Integer getBeginDayMin()
    {
       String val = getString(BEGIN_TIME_MIN, "30");
       return Integer.parseInt(val);
    }

    public Integer getEndDayHour()
    {
       String val = getString(END_TIME_HR, "16");
       return Integer.parseInt(val);
    }

    public Integer getEndDayMin()
    {
       String val = getString(END_TIME_MIN, "0");
       return Integer.parseInt(val);
    }

    public Integer getLunchWindowBeginHr()
    {
       String val = getString(LUNCH_WINDOW_BEGIN_HR, "11");
       return Integer.parseInt(val);
    }

    public Integer getLunchWindowEndHr()
    {
       String val = getString(LUNCH_WINDOW_END_HR, "13");
       return Integer.parseInt(val);
    }

    public Integer getEmptyTimeSlotMinimumMin()
    {
       String val = getString(EMPTY_TIME_SLOT_MINIMUM_MIN, "30");
       return Integer.parseInt(val);
    }


    public Integer getCalReminderMin()
    {
       String val = getString(CAL_REMINDER_MIN, "30");
       return Integer.parseInt(val);
    }

    public String getEmailDescription()
    {
       return getString(CAL_EMAIL_DESC, "Hello,\n\n" +

                                        "This is Tony with Sentry Fires & Safety.\n\n" +

                                        "We are your fire protection specialist that comes to inspect and service your fire protection " +
                                        "equipment every year.  Your equipment is due for inspection or service at this time and " +
                                        "we would like to schedule a technician to come out to your location and complete this work.  " +
                                        "The items that are due for service are listed below at the bottom of this invite.\n\n" +

                                        "Please accept this invite if the time will work for you, otherwise please reject it and " +
                                        "propose a better time that suits you.  Don't hesitate to call or email with any questions.\n\n" +

                                        "We look forward to working with you!\n\n" +

                                        "Tony Greway\n" +
                                        "Sentry Fire & Safety\n" +
                                        "info@com.sentryfire.net\n" +
                                        "(303)-294-0708\n\n" +
                                        "-------- Customer Location Info ---------\n\n");

    }

    public String getSchedulerEmail()
    {
       return getString(CAL_SCHEDULER_EMAIL, "scheduler@com.sentryfire.com");
    }

    public String getDataDirBase()
    {
       return getString(DATA_DIR_BASE, "/opt/sentry/data/");
    }

    public Map<String, Integer> getItemTimeMinsMap()
    {
       if (itemsToTime != null)
          return itemsToTime;

       try
       {
          itemsToTime = Maps.newHashMap();
          List<String> itemList = getStringList(ITEMS_TIME_MINS, Lists.newArrayList());
          for (String itemTime : itemList)
          {
             String[] strAry = itemTime.split("=");

             if (strAry.length == 2)
                itemsToTime.put(strAry[0], Integer.parseInt(strAry[1].trim()));
             else
                log.error("Improperly formatted config " + itemTime);
          }
       }
       catch (Exception e)
       {
          log.error("Failed to parse config file correctly for items to time.", e);
       }
       return itemsToTime;
    }

    public Map<String, SKILL> getItemToSkill()
    {
       if (itemsToSkill != null)
          return itemsToSkill;

       try
       {
          itemsToSkill = Maps.newHashMap();
          List<String> itemList = getStringList(ITEM_SKILL, Lists.newArrayList());
          for (String itemTime : itemList)
          {
             String[] strAry = itemTime.split("=");

             if (strAry.length == 2)
             {
                try
                {
                   SKILL skill = SKILL.valueOf(strAry[1].trim());
                   itemsToSkill.put(strAry[0], skill);
                }
                catch (Exception e)
                {
                   log.error("Failed to convert skill. " + Arrays.toString(strAry));
                }
             }
             else
                log.error("Improperly formatted config " + itemTime);
          }
       }
       catch (Exception e)
       {
          log.error("Failed to parse config file correctly for items to skill.", e);
       }
       return itemsToSkill;
    }

 }
