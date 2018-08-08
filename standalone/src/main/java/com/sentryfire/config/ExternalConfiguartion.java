 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      ExternalConfiguartion.java
  * Created:   6/1/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.config;

 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.List;
 import java.util.Properties;

 import com.google.common.collect.Lists;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class ExternalConfiguartion extends BaseConfiguration
 {
    Logger log = LoggerFactory.getLogger(ExternalConfiguartion.class);

    //*******************//
    // File Location
    //*******************//
    public static final String CONFIG_FILE = "sentry-config.properties";

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
    private static ExternalConfiguartion instance;

    public static ExternalConfiguartion getInstance()
    {
       if (instance == null)
       {
          instance = new ExternalConfiguartion(CONFIG_FILE);
       }
       return instance;
    }

    public ExternalConfiguartion() throws FileNotFoundException, IOException
    {
       this.fileName = CONFIG_FILE;
       properties = new Properties();
       FileInputStream input = new FileInputStream(fileName);
       properties.load(input);
    }

    public ExternalConfiguartion(final String fileName)
    {
       this.fileName = fileName;

       properties = new Properties();

       try
       {
          InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
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

 }
