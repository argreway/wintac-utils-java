 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WIPUtils.java
  * Created:   6/29/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.utils;

 import java.util.List;
 import java.util.Map;
 import java.util.Vector;

 import javax.swing.table.DefaultTableModel;

 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import com.sentryfire.persistance.DAOFactory;
 import com.sentryfire.persistance.dao.influxdb.InfluxClient;
 import org.influxdb.dto.Point;
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeZone;
 import org.joda.time.MutableDateTime;
 import org.joda.time.format.DateTimeFormat;
 import org.joda.time.format.DateTimeFormatter;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class WIPUtils
 {

    private static Logger log = LoggerFactory.getLogger(WIPUtils.class);

    protected static MutableDateTime start;
    protected static MutableDateTime end;

    protected static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S");

    // Start of wintac data
    static
    {
       start = new MutableDateTime(DateTimeZone.UTC);
       start.setYear(2010);
       start.setMonthOfYear(1);
       start.setDayOfMonth(1);

       end = new MutableDateTime(DateTimeZone.UTC);
       end.addDays(60);
    }


    private static int BATCH_SIZE = 10000;

    public static DefaultTableModel insertAllStats()
    {
       String message = "Inserting all stats from " + start + " to " + end;
       log.info(message);

       DAOFactory.getInfluxClient().dropDataBase();
       DAOFactory.getInfluxClient().createDataBase();

       insertInvoiceStats();
       log.info("Finished invoices.");
       insertWorkOrderStats();
       log.info("Finished wo.");
       insertProposalStats();
       log.info("Finished props.");
       insertPurchaseOrders();
       log.info("Finished po.");
       insertPayrollStats();
       log.info("Finished pay.");
       insertARStats();
       log.info("Finished ar.");
       DefaultTableModel dt = insertItems();
       log.info("Finished items.");
       return dt;
    }

    public static DefaultTableModel getOutStandingWorkOrders()
    {
       return DAOFactory.sqlDB().getOutStandingWorkOrders(start.toDateTime(), end.toDateTime());
    }

    public static DefaultTableModel insertItems()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getInvoiceItemTable();
       updateWIPStats(dt, InfluxClient.COL.IDATE.toString(), InfluxClient.ITEM_FIELDS, InfluxClient.ITEM_TAGS, InfluxClient.MEASUREMENT.ITEM.toString());
       return dt;
    }

    public static DefaultTableModel insertPayrollStats()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getPayrollDataTable(start.toDateTime(), end.toDateTime());
       updateWIPStats(dt, InfluxClient.COL.CHKDATE.toString(), InfluxClient.PAY_FIELDS, InfluxClient.MEASUREMENT.PAY.toString());
       return dt;
    }

    public static DefaultTableModel insertProposalStats()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getProposals(start.toDateTime(), end.toDateTime());
       updateWIPStats(dt, InfluxClient.COL.JDATE.toString(), InfluxClient.WIP_FIELDS, InfluxClient.MEASUREMENT.PROP.toString());
       return dt;
    }

    public static DefaultTableModel insertPurchaseOrders()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getPurchaseOrders(start.toDateTime(), end.toDateTime());
       updateWIPStats(dt, InfluxClient.COL.JDATE.toString(), InfluxClient.WIP_FIELDS, InfluxClient.MEASUREMENT.PO.toString());
       return dt;
    }

    public static DefaultTableModel insertInvoiceStats()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getInvoices(start.toDateTime(), end.toDateTime());
       updateWIPStats(dt, InfluxClient.COL.JDATE.toString(), InfluxClient.WIP_FIELDS, InfluxClient.MEASUREMENT.INV.toString());
       return dt;
    }

    public static DefaultTableModel insertWorkOrderStats()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getOutStandingWorkOrders(start.toDateTime(), end.toDateTime());
       updateWIPStats(dt, InfluxClient.COL.JDATE.toString(), InfluxClient.WIP_FIELDS, InfluxClient.MEASUREMENT.WO.toString());
       return dt;
    }

    public static DefaultTableModel insertARStats()
    {
       DateTime now = new DateTime(DateTimeZone.UTC);
       DefaultTableModel dt = DAOFactory.sqlDB().getARDataTable(now);
       updateWIPStats(dt, InfluxClient.COL.INVDATE.toString(), InfluxClient.AR_FIELDS, InfluxClient.MEASUREMENT.AR.toString());
       return dt;
    }

    protected static void updateWIPStats(DefaultTableModel dataTable,
                                         String dateColumn,
                                         List<String> fieldColumns,
                                         String measurement)
    {
       updateWIPStats(dataTable, dateColumn, fieldColumns, null, measurement);
    }

    protected static void updateWIPStats(DefaultTableModel dataTable,
                                         String dateColumn,
                                         List<String> fieldColumns,
                                         List<String> tagColumns,
                                         String measurement)
    {
       List<Point> measurements = Lists.newArrayList();
       DateTime timestamp = new DateTime(DateTimeZone.UTC);

       // Create Measurement per row
       for (Object rowObj : dataTable.getDataVector())
       {
          Vector<Object> row = (Vector<Object>) rowObj;

          Map<String, Object> fields = Maps.newHashMap();
          Map<String, String> tags = Maps.newHashMap();

          int nCols = dataTable.getColumnCount();
          int dateColumnIdx = -1;
          for (int i = 0; i < nCols; i++)
          {
             String columnName = dataTable.getColumnName(i);


             Object value = row.get(i);
             if (value == null)
                continue;

             if (columnName.equals(dateColumn))
                dateColumnIdx = i;

             if (isField(fieldColumns, columnName))
             {
                fields.put(columnName, Float.parseFloat(row.get(i).toString()));
             }
             else if (isField(tagColumns, columnName))
             {
                String tValue = row.get(i).toString().trim().replace(" ", "_");
                tags.put(columnName, tValue);
             }
             else if (tagColumns != null)
             {
                // Everything else should be treated as a string value
                String tValue = row.get(i).toString().trim().replace(" ", "_");
                fields.put(columnName, tValue);
             }
             else
             {
                String tValue = row.get(i).toString().trim().replace(" ", "_");
                tags.put(columnName, tValue);
             }
          }

          if (dateColumnIdx > -1)
          {
             String convDate = row.get(dateColumnIdx).toString();
             if (convDate != null && !convDate.trim().isEmpty())
                timestamp = formatter.parseDateTime(convDate);
          }
          else
          {
             log.error("No date column to generate timestamp! [" + dateColumn + "]");
          }

          measurements.add(InfluxClient.buildPoint(measurement, fields, tags, timestamp));

          if (measurements.size() == BATCH_SIZE)
          {
             try
             {
                log.info("Bulk write " + measurements.size());
                DAOFactory.getInfluxClient().write(measurements);
                measurements.clear();
                Thread.sleep(100);

             }
             catch (Exception e)
             {
                log.error("Failed to send bulk points due to : ", e);
             }
          }
       }

       // Bulk write out the stats
       DAOFactory.getInfluxClient().write(measurements);
       InfluxClient.clearStampList();
       log.info("Final bulk write " + measurements.size());
    }

    protected static boolean isField(List<String> fieldList,
                                     String columnName)
    {
       if (fieldList == null)
          return false;
       for (String fieldName : fieldList)
       {
          if (columnName.equals(fieldName))
             return true;
       }
       return false;
    }

 }
