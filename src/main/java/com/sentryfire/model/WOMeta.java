 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WOMeta.java
  * Created:   7/12/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import java.util.List;

 import com.google.common.collect.Lists;

 public class WOMeta
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

    @Override
    public String toString()
    {
       return "WOMeta{" +
              "itemStatHolderList=" + itemStatHolderList +
              '}';
    }
 }
