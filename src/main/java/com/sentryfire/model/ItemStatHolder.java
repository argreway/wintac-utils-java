 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      ItemStatHolder.java
  * Created:   7/12/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 public class ItemStatHolder
 {
    protected Integer min;
    protected Integer count;
    protected String itemCode;

    public ItemStatHolder(Integer min,
                          Integer count,
                          String itemCode)
    {
       this.min = min;
       this.count = count;
       this.itemCode = itemCode;
    }

    @Override
    public String toString()
    {
       return "(ic=" + itemCode + ", min=" + min +
              ", count=" + count + ")";
    }

    public Integer getMin()
    {
       return min;
    }

    public void setMin(Integer min)
    {
       this.min = min;
    }

    public Integer getCount()
    {
       return count;
    }

    public void setCount(Integer count)
    {
       this.count = count;
    }

    public String getItemCode()
    {
       return itemCode;
    }

    public void setItemCode(String itemCode)
    {
       this.itemCode = itemCode;
    }
 }
