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
 import java.io.OutputStream;
 import java.util.List;
 import java.util.Properties;

 import com.google.common.collect.Lists;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class SentryConfiguartion
 {
    Logger log = LoggerFactory.getLogger(SentryConfiguartion.class);

    //*******************//
    // Local
    //*******************//
    private String fileName;
    protected Properties properties;

    //*******************//
    // File Location
    //*******************//
    public static final String CONFIG_FILE = "/opt/sentry/conf/sentry-config.properties";

    //*******************//
    // Properties
    //*******************//
    public static final String EXCLUSION_LIST = "exclusionlist";

    protected static final String DATABASE = "database";
    protected static final String SERVER = "server";
    protected static final String USER = "user";
    protected static final String PASSWORD = "password";

    protected static final String GOOGLE_MAP_API_KEY = "googleApiKey";

    //*******************//
    // Constructors
    //*******************//
    private static SentryConfiguartion instance;

    public static SentryConfiguartion getInstance()
    {
       if (instance == null)
       {
          instance = new SentryConfiguartion(CONFIG_FILE);
       }
       return instance;
    }

    public SentryConfiguartion() throws FileNotFoundException, IOException
    {
       this.fileName = CONFIG_FILE;
       properties = new Properties();
       FileInputStream input = new FileInputStream(fileName);
       properties.load(input);
    }

    public SentryConfiguartion(final String fileName)
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
          log.error("Failed to load the configuration file: " + fileName + ".", e);

       }
    }

    //*******************//
    // Getters
    //*******************//

    public List<String> getExclusionList()
    {
       return getStringList(EXCLUSION_LIST, Lists.newArrayList());
    }

    public String getDatabase()
    {
       return getString(DATABASE, "");
    }

    public String getServer()
    {
       return getString(SERVER, "");
    }

    public String getUser()
    {
       return getString(USER);
    }

    public String getPassword()
    {
       return getString(PASSWORD, "");
    }

    public String getGoogleMapApiKey()
    {
       return getString(GOOGLE_MAP_API_KEY, "");
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
       properties.store(out, "vim configuration file");
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
