 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      TechProfileConfiguration.java
  * Created:   7/24/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.config;

 import java.io.File;
 import java.io.InputStream;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;

 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.core.io.Resource;
 import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

 public class TechProfileConfiguration extends BaseConfiguration
 {

    static Logger log = LoggerFactory.getLogger(TechProfileConfiguration.class);

    //*******************//
    // File Location
    //*******************//
    public static final String CONFIG_DIR = "conf/profiles/";
    public static final String PROF_DIR = "profiles/";

    protected Map<String, TechProfile> denTechToProfile = Maps.newHashMap();
    protected Map<String, TechProfile> greTechToProfile = Maps.newHashMap();
    protected Map<String, TechProfile> fipTechToProfile = Maps.newHashMap();

    //*******************//
    // Properties
    //*******************//

    protected static final String PREFIX = "tech";

    protected static final String NAME = PREFIX + "Name";
    protected static final String DIVISION = PREFIX + "Division";
    protected static final String SKILLS = PREFIX + "Skills";
    protected static final String SCHEDULE_SKILLS = PREFIX + "ScheduleSkills";
    protected static final String HOME = PREFIX + "Home";
    protected static final String TERRITORY = PREFIX + "Territory";
    protected static final String EMAIL = PREFIX + "Email";
    protected static final String CUSTOMER_PREF = PREFIX + "CustomerPreference";


    //*******************//
    // Constructors
    //*******************//
    private static TechProfileConfiguration instance;

    public static TechProfileConfiguration getInstance()
    {
       if (instance == null)
       {
          instance = new TechProfileConfiguration();
       }
       return instance;
    }

    //*******************//
    // Getters
    //*******************//

    public List<String> getAllCustomerPreferences()
    {
       List<String> allCustomerPrefs = Lists.newArrayList();

       List<TechProfile> allProfiles = Lists.newArrayList();
       allProfiles.addAll(denTechToProfile.values());
       allProfiles.addAll(greTechToProfile.values());
       allProfiles.addAll(fipTechToProfile.values());
       for (TechProfile profile : allProfiles)
       {

          allCustomerPrefs.addAll(profile.getCustomerPref());
       }
       return allCustomerPrefs;
    }

    public Map<String, TechProfile> getDenTechToProfiles()
    {
       return denTechToProfile;
    }

    public Map<String, TechProfile> getGreTechToProfiles()
    {
       return greTechToProfile;
    }

    public Map<String, TechProfile> getFipTechToProfiles()
    {
       return fipTechToProfile;
    }

    public TechProfileConfiguration()
    {
       loadProfiles();
    }

    protected void loadProfiles()
    {
       try
       {
          File cwd = new File(".");
          log.info("Cwd: " + cwd.getAbsolutePath());
          PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

          // Ant-style path matching

          Resource[] resources;
          try
          {
             resources = resolver.getResources(CONFIG_DIR + "**");
          }
          catch (Exception e)
          {
             resources = resolver.getResources(PROF_DIR + "**");
          }

          if (resources == null || resources.length <= 0)
          {
             log.error("Tech config dir does not exist: " + CONFIG_DIR);
             return;
          }

          for (Resource resource : resources)
          {
             try
             {
                Properties properties = new Properties();
                InputStream input = resource.getInputStream();
                properties.load(input);

                TechProfile profile = new TechProfile();
                profile.setName(getString(NAME, properties));
                profile.setDivision(getString(DIVISION, properties));
                profile.setSkills(getSkillList(SKILLS, properties));
                profile.setScheduleSkills(getSkillList(SCHEDULE_SKILLS, properties));
                profile.setCustomerPref(getStringList(CUSTOMER_PREF, Lists.newArrayList(), properties));
                profile.setHome(getString(HOME, properties));
                profile.setTerritory(getString(TERRITORY, properties));
                profile.setEmail(getString(EMAIL, properties));

                if (profile.getDivision().equals("DENVER"))
                   denTechToProfile.put(profile.getName(), profile);
                else if (profile.getDivision().equals("GREELEY"))
                   greTechToProfile.put(profile.getName(), profile);
                else if (profile.getDivision().equals("FIP"))
                   fipTechToProfile.put(profile.getName(), profile);
                log.info("Loaded Profile [" + profile.getName() + "]");
             }
             catch (Exception e)
             {
                log.error("Failed to load properties for Tech Profile: " + resource);
             }
          }
       }
       catch (Exception e)
       {
          log.error("Failed to load the tech profiles.", e);
       }
    }


 }
