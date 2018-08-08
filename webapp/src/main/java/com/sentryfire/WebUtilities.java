 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WebUtilities.java
  * Created:   8/7/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire;

 import javax.swing.table.DefaultTableModel;

 import com.sentryfire.persistance.DAOFactory;
 import org.joda.time.MutableDateTime;

 public class WebUtilities
 {

    public String outputSimple()
    {
       return "Hello Tony";
    }


    public String getActivityLog()
    {
       MutableDateTime start = new MutableDateTime();
       start.setYear(2018);
       start.setDayOfMonth(1);
       start.setMonthOfYear(4);
       start.setHourOfDay(0);

       MutableDateTime end = new MutableDateTime(start);
       end.setDayOfMonth(2);
       end.setMonthOfYear(5);
       start.setHourOfDay(23);

       DefaultTableModel model = DAOFactory.sqlDB().getUserActivityLog(start.toDateTime(), end.toDateTime());
       return "RowCount = " + model.getRowCount();
    }
 }
