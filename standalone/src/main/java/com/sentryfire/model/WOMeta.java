 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WOMeta.java
  * Created:   7/12/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import java.io.Serializable;
 import java.util.List;
 import java.util.Set;
 import java.util.stream.Collectors;

 import com.google.common.collect.Lists;
 import com.sentryfire.config.AppConfiguartion;

 public class WOMeta implements Serializable
 {

    protected List<ItemStatHolder> itemStatHolderList = Lists.newArrayList();

    ///////////// Getter/Setter ////////////
    public List<ItemStatHolder> getItemStatHolderList()
    {
       return itemStatHolderList;
    }

    public List<ItemStatHolder> getItemStatHolderList(String tech)
    {
       return itemStatHolderList.stream().filter(i -> tech.equals(i.getTech())).collect(Collectors.toList());
    }

    public void setItemStatHolderList(List<ItemStatHolder> itemStatHolderList)
    {
       this.itemStatHolderList = itemStatHolderList;
    }

    public Set<SKILL> getSkillsRequired()
    {
       return itemStatHolderList.stream().map(ItemStatHolder::getSkill).collect(Collectors.toSet());
    }

    public boolean hasEarlyItem(String tech)
    {
       List<String> items = getItemStatHolderList(tech).stream().map(ItemStatHolder::getItemCode).collect(Collectors.toList());
       for (String ic : items)
       {
          if (AppConfiguartion.getInstance().getEarlyMorningItems().contains(ic))
             return true;
       }
       return false;
    }

    public Integer getWorkLoadMinutes()
    {
       return itemStatHolderList.stream().mapToInt(ItemStatHolder::getMin).sum();
    }

    public Integer getWorkLoadMinutes(String tech)
    {
       return itemStatHolderList.stream().filter(i -> tech.equals(i.getTech())).mapToInt(ItemStatHolder::getMin).sum();
    }

    public Set<String> getTechsOnSite()
    {
       return itemStatHolderList.stream().map(ItemStatHolder::getTech).collect(Collectors.toSet());
    }

    @Override
    public String toString()
    {
       return "WOMeta{" +
              "itemStatHolderList=" + itemStatHolderList +
              '}';
    }
 }
