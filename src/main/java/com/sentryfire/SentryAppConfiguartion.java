 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      SentryConfiguartion.java
  * Created:   6/1/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire;

 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;

 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class SentryAppConfiguartion
 {
    Logger log = LoggerFactory.getLogger(SentryAppConfiguartion.class);

    //*******************//
    // Local
    //*******************//
    private String fileName;
    protected Properties properties;

    //*******************//
    // File Location
    //*******************//
    public static final String CONFIG_FILE = "sentry-app.properties";

    //*******************//
    // Properties
    //*******************//

    protected static final String ITEMS_TIME_MINS = "itemsTimeMin";
    protected static final String DRIVE_TIME = "driveTime";
    protected static final String LUNCH_TIME = "lunchTime";
    protected static final String BEGIN_TIME_MIN = "beginTimeMin";
    protected static final String BEGIN_TIME_HR = "beginTimeHr";
    protected static final String END_TIME_HR = "endTimeHr";
    protected static final String CAL_REMINDER_MIN = "calReminderMins";

    //*******************//
    // Constructors
    //*******************//
    private static SentryAppConfiguartion instance;

    public static SentryAppConfiguartion getInstance()
    {
       if (instance == null)
       {
          instance = new SentryAppConfiguartion(CONFIG_FILE);
       }
       return instance;
    }

    public SentryAppConfiguartion() throws FileNotFoundException, IOException
    {
       this.fileName = CONFIG_FILE;
       properties = new Properties();
       FileInputStream input = new FileInputStream(fileName);
       properties.load(input);
    }

    public SentryAppConfiguartion(final String fileName)
    {
       this.fileName = fileName;

       properties = new Properties();

       try
       {
          FileInputStream input = new FileInputStream(fileName);
          properties.load(input);
       }
       catch (Exception e)
       {
          try
          {
             InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
             properties.load(input);
          }
          catch (Exception e2)
          {
             log.error("Failed to load the configuration file: " + fileName + ".", e);
             log.error("Failed to load the configuration file from classpath: " + fileName + ".", e2);
          }
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

    public Integer getCalReminderMin()
    {
       String val = getString(CAL_REMINDER_MIN, "30");
       return Integer.parseInt(val);
    }

    public Map<String, Integer> getItemTimeMinsMap()
    {
       Map<String, Integer> result = Maps.newHashMap();
       try
       {
          List<String> itemList = getStringList(ITEMS_TIME_MINS, Lists.newArrayList());
          for (String itemTime : itemList)
          {
             String[] strAry = itemTime.split("=");

             if (strAry.length == 2)
                result.put(strAry[0], Integer.parseInt(strAry[1].trim()));
             else
                log.error("Improperly formatted config " + itemTime);

          }
       }
       catch (Exception e)
       {
          log.error("Failed to parse config file correctly for items to time.", e);
       }
       return result;
    }


    //*******************//
    // Protected Utils
    //*******************//

    public void rewriteProperyFile() throws IOException
    {
       if (fileName == null)
       {
          return;
       }
       OutputStream out = new FileOutputStream(fileName);
       properties.store(out, "sentry configuration file");
    }

    public String getString(final String property)
    {
       return this.properties.getProperty(property);
    }

    public String getString(final String property,
                            final String defaultStr)
    {
       return this.properties.getProperty(property, defaultStr);
    }

    public List<String> getStringList(final String property,
                                      final List<String> defaultStrList)
    {
       String prop = this.properties.getProperty(property, null);
       if (prop == null)
       {
          return defaultStrList;
       }
       return Lists.newArrayList(prop.split(","));
    }

 }
