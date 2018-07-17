 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      PojoConverterUtil.java
  * Created:   7/17/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import java.util.List;
 import java.util.Vector;

 import javax.swing.table.DefaultTableModel;

 import com.google.common.collect.Lists;
 import org.joda.time.DateTime;
 import org.joda.time.format.DateTimeFormat;
 import org.joda.time.format.DateTimeFormatter;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class PojoConverterUtil
 {
    protected static Logger log = LoggerFactory.getLogger(PojoConverterUtil.class);

    protected static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S");
    protected static DateTimeFormatter msFormatter = DateTimeFormat.forPattern("M/d/yyyy HH:mm:ss a");

    protected static String AL_DATE = "Date";

    public static List<ActivityLog> convertTableToLog(DefaultTableModel dataTable)
    {

       List<ActivityLog> result = Lists.newArrayList();

       // Create Measurement per row
       for (Object rowObj : dataTable.getDataVector())
       {
          Vector<Object> row = (Vector<Object>) rowObj;
          ActivityLog obj = new ActivityLog();

          int nCols = dataTable.getColumnCount();
          for (int i = 0; i < nCols; i++)
          {
             String columnName = dataTable.getColumnName(i);


             Object value = row.get(i);
             if (value == null)
                continue;

             String rowVal = row.get(i).toString();

             if (columnName.equals(AL_DATE))
             {
                if (rowVal != null && !rowVal.trim().isEmpty())
                {
                   DateTime convDate = formatter.parseDateTime(rowVal);
                   rowVal = msFormatter.print(convDate);
                }
             }

             if (columnName.equals("Name"))
                obj.setName(rowVal);
             else if (columnName.equals("Action"))
                obj.setAction(rowVal);
             else if (columnName.equals("Date"))
                obj.setDate(rowVal);
             else if (columnName.equals("Time"))
                obj.setTime(rowVal);
             else if (columnName.equals("LOGKey"))
             {
                // do nothing
             }
             else
                log.error("Unknown column name " + columnName);
          }
          result.add(obj);
       }
       return result;
    }

    public static List<String> getValueFromTable(DefaultTableModel dataTable,
                                                 String valueName)
    {
       List<String> result = Lists.newArrayList();

       if (dataTable != null && dataTable.getRowCount() > 0)
       {
          for (Object rowObj : dataTable.getDataVector())
          {
             Vector<Object> row = (Vector<Object>) rowObj;
             int nCols = dataTable.getColumnCount();
             for (int i = 0; i < nCols; i++)
             {
                String columnName = dataTable.getColumnName(i);
                if (valueName.equals(columnName))
                {
                   if (row.get(i) != null)
                   {
                      String rowVal = row.get(i).toString();
                      if (rowVal != null)
                         result.add(rowVal);
                   }
                   break;
                }
             }
          }
       }
       return result;
    }

 }
