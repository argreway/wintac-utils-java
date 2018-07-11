 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      RealTimeDataUtils.java
  * Created:   6/29/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.utils;

 import javax.swing.table.DefaultTableModel;

import com.sentryfire.persistance.DAOFactory;
import com.sentryfire.persistance.dao.influxdb.InfluxClient;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class RealTimeDataUtils
 {

    private static Logger log = LoggerFactory.getLogger(RealTimeDataUtils.class);

    protected static MutableDateTime start;
    protected static MutableDateTime end;

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

    public static DefaultTableModel getOutStandingWorkOrders(DateTime begin,
                                                             DateTime stop)
    {
       return DAOFactory.sqlDB().getWorkOrdersByTime(begin.toDateTime(), stop.toDateTime());
    }

    public static DefaultTableModel insertItems()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getInvoiceItemTable();
       DAOFactory.getInfluxClient().insertInfluxStatsForDataTable(dt, InfluxClient.COL.IDATE.toString(), InfluxClient.ITEM_FIELDS, InfluxClient.ITEM_TAGS, InfluxClient.MEASUREMENT.ITEM.toString());
       return dt;
    }

    public static DefaultTableModel insertPayrollStats()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getPayrollDataTable(start.toDateTime(), end.toDateTime());
       DAOFactory.getInfluxClient().insertInfluxStatsForDataTable(dt, InfluxClient.COL.CHKDATE.toString(), InfluxClient.PAY_FIELDS, InfluxClient.MEASUREMENT.PAY.toString());
       return dt;
    }

    public static DefaultTableModel insertProposalStats()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getProposals(start.toDateTime(), end.toDateTime());
       DAOFactory.getInfluxClient().insertInfluxStatsForDataTable(dt, InfluxClient.COL.JDATE.toString(), InfluxClient.WIP_FIELDS, InfluxClient.MEASUREMENT.PROP.toString());
       return dt;
    }

    public static DefaultTableModel insertPurchaseOrders()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getPurchaseOrders(start.toDateTime(), end.toDateTime());
       DAOFactory.getInfluxClient().insertInfluxStatsForDataTable(dt, InfluxClient.COL.JDATE.toString(), InfluxClient.WIP_FIELDS, InfluxClient.MEASUREMENT.PO.toString());
       return dt;
    }

    public static DefaultTableModel insertInvoiceStats()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getInvoices(start.toDateTime(), end.toDateTime());
       DAOFactory.getInfluxClient().insertInfluxStatsForDataTable(dt, InfluxClient.COL.JDATE.toString(), InfluxClient.WIP_FIELDS, InfluxClient.MEASUREMENT.INV.toString());
       return dt;
    }

    public static DefaultTableModel insertWorkOrderStats()
    {
       DefaultTableModel dt = DAOFactory.sqlDB().getWorkOrdersByTime(start.toDateTime(), end.toDateTime());
       DAOFactory.getInfluxClient().insertInfluxStatsForDataTable(dt, InfluxClient.COL.JDATE.toString(), InfluxClient.WIP_FIELDS, InfluxClient.MEASUREMENT.WO.toString());
       return dt;
    }

    public static DefaultTableModel insertARStats()
    {
       DateTime now = new DateTime(DateTimeZone.UTC);
       DefaultTableModel dt = DAOFactory.sqlDB().getARDataTable(now);
       DAOFactory.getInfluxClient().insertInfluxStatsForDataTable(dt, InfluxClient.COL.INVDATE.toString(), InfluxClient.AR_FIELDS, InfluxClient.MEASUREMENT.AR.toString());
       return dt;
    }

 }
