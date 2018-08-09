 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      BaseConfiguration.java
  * Created:   7/24/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.config;

 import java.util.List;
 import java.util.Properties;
 import java.util.stream.Collectors;

 import com.google.common.collect.Lists;
 import com.sentryfire.model.SKILL;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class BaseConfiguration
 {
    Logger log = LoggerFactory.getLogger(getClass());

    protected Properties properties;


    //*******************//
    // Protected Utils
    //*******************//

    public List<SKILL> getSkillList(String propName,
                                    Properties properties)
    {
       List<SKILL> result = Lists.newArrayList();
       try
       {
          List<String> itemList = getStringList(propName, Lists.newArrayList(), properties);
          for (String skillStr : itemList)
          {
             if (skillStr.isEmpty())
                continue;

             try
             {
                SKILL skill = SKILL.valueOf(skillStr.trim());
                result.add(skill);
             }
             catch (Exception e)
             {
                log.error("Failed to convert skill. " + skillStr);
             }
          }
       }
       catch (Exception e)
       {
          log.error("Failed to parse config file correctly for items to skill.", e);
       }
       return result;
    }

    public String getString(final String property)
    {
       return getString(property, this.properties);
    }

    public String getString(final String property,
                            final Properties properties)
    {
       return properties.getProperty(property);
    }

    public String getString(final String property,
                            final String defaultStr)
    {
       return getString(property, defaultStr, this.properties);
    }

    public String getString(final String property,
                            final String defaultStr,
                            final Properties properties)
    {
       return properties.getProperty(property, defaultStr);
    }

    public List<String> getStringList(final String property,
                                      final List<String> defaultStrList)
    {
       return getStringList(property, defaultStrList, this.properties);
    }

    public List<String> getStringList(final String property,
                                      final List<String> defaultStrList,
                                      Properties properties)
    {
       String prop = properties.getProperty(property, null);
       if (prop == null)
       {
          return defaultStrList;
       }
       List<String> result = Lists.newArrayList(prop.split(","));
       return result.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

 }
