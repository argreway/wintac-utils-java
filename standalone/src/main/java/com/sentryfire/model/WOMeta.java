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

 public class WOMeta implements Serializable
 {

    protected List<ItemStatHolder> itemStatHolderList = Lists.newArrayList();

    public List<ItemStatHolder> getItemStatHolderList()
    {
       return itemStatHolderList;
    }

    public void setItemStatHolderList(List<ItemStatHolder> itemStatHolderList)
    {
       this.itemStatHolderList = itemStatHolderList;
    }

    public Set<SKILL> getSkillsRequired()
    {
       return itemStatHolderList.stream().map(ItemStatHolder::getSkill).collect(Collectors.toSet());
    }

    public Integer getWorkLoadMinutes()
    {
       return itemStatHolderList.stream().mapToInt(ItemStatHolder::getMin).sum();
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
