 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      DateComparator.java
  * Created:   8/20/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.controller;

 import java.util.Comparator;

 public class DateComparator implements Comparator<String>
 {

    @Override
    public int compare(String o1,
                       String o2)
    {
       if (o1 == null && o2 != null)
          return 1;
       else if (o1 != null && o2 == null)
          return -1;

       String[] p1 = o1.split("-");
       String[] p2 = o2.split("-");

       Integer p1year = Integer.parseInt(p1[2]);
       Integer p2year = Integer.parseInt(p2[2]);
       Integer p1mon = Integer.parseInt(p1[1]);
       Integer p2mon = Integer.parseInt(p2[1]);
       Integer p1day = Integer.parseInt(p1[0]);
       Integer p2day = Integer.parseInt(p2[0]);

       if (p1year > p2year)
          return -1;
       if (p1year < p2year)
          return 1;

       if (p1mon > p2mon)
          return 1;
       if (p1mon < p2mon)
          return -1;

       if (p1day > p2day)
          return 1;
       if (p1day < p2day)
          return -1;

       return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
       return false;
    }

 }
