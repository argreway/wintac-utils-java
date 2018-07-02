 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      AppEventHandler.java
  * Created:   6/28/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.gui;

 import java.awt.event.ActionEvent;
 import java.awt.event.ComponentEvent;

 import javax.swing.JTable;
 import javax.swing.table.DefaultTableModel;

 import com.sentryfire.SentryConfiguartion;
 import com.sentryfire.business.utils.WIPUtils;
 import com.sentryfire.persistance.DAOFactory;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class AppEventHandler implements EventHandler
 {
    private Logger log = LoggerFactory.getLogger(getClass());

    private SentryConfiguartion config = SentryConfiguartion.getInstance();
    private MainApp window;

    public AppEventHandler()
    {
    }

    public void setMainApp(MainApp window)
    {
       this.window = window;
    }

    @Override
    public void handleInit(ComponentEvent ce)
    {
       window.textFieldServer.setText(config.getServer());
       window.textFieldDB.setText(config.getDatabase());
       window.textFieldUser.setText(config.getUser());
       window.passwordField.setText(config.getPassword());

       handleConnectDB(null);
    }

    @Override
    public void handleConnectDB(ActionEvent e)
    {
       DAOFactory.sqlDB().connectToDB(
          window.textFieldServer.getText(),
          window.textFieldDB.getText(),
          window.textFieldUser.getText(),
          new String(window.passwordField.getPassword()));
    }

    @Override
    public void handleDisconnectDB(ActionEvent e)
    {
       DAOFactory.sqlDB().closeDBConnection();
    }

    @Override
    public void handleUpdateWO(ActionEvent e)
    {
       DefaultTableModel model = WIPUtils.insertWorkOrderStats();
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    @Override
    public void handleUpdateINV(ActionEvent e)
    {
       DefaultTableModel model = WIPUtils.insertInvoiceStats();
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    @Override
    public void handleUpdateAR(ActionEvent e)
    {
       DefaultTableModel model = WIPUtils.insertARStats();
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    @Override
    public void handleUpdatePO(ActionEvent e)
    {
       DefaultTableModel model = WIPUtils.insertPurchaseOrders();
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    }

    @Override
    public void handleUpdatePay(ActionEvent e)
    {
       DefaultTableModel model = WIPUtils.insertPayrollStats();
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    @Override
    public void handleUpdateProp(ActionEvent e)
    {
       DefaultTableModel model = WIPUtils.insertProposalStats();
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    }

    @Override
    public void handleUpdateItems(ActionEvent e)
    {
       DefaultTableModel model = WIPUtils.insertItems();
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    }

    @Override
    public void handleUpdateAll(ActionEvent e)
    {
       DefaultTableModel model = WIPUtils.insertAllStats();
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    @Override
    public void handleCreateInflux(ActionEvent e)
    {
       DAOFactory.getInfluxClient().createDataBase();
    }

    @Override
    public void handleDropInflux(ActionEvent e)
    {
       DAOFactory.getInfluxClient().dropDataBase();
    }

    @Override
    public void handleLoadCustomers(ActionEvent e)
    {
       DefaultTableModel model = DAOFactory.sqlDB().getCustomerList();
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    @Override
    public void handleSearchCustomers(ActionEvent e)
    {
       DefaultTableModel model = DAOFactory.sqlDB().getCustomerList();
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    @Override
    public void handleLoadWO(ActionEvent e)
    {
       MutableDateTime start = new MutableDateTime();
       start.setYear(2018);
       start.setDayOfMonth(1);
       start.setMonthOfYear(7);

       MutableDateTime end = new MutableDateTime(start);
       end.setDayOfMonth(end.dayOfMonth().getMaximumValue());

       DefaultTableModel model = DAOFactory.sqlDB().getOutStandingWorkOrders(
          start.toDateTime(), end.toDateTime());
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    @Override
    public void handleLoadPayroll(ActionEvent e)
    {
       MutableDateTime start = new MutableDateTime();
       start.setYear(2018);
       start.setDayOfMonth(1);
       start.setMonthOfYear(6);

       MutableDateTime end = new MutableDateTime(start);
       end.setDayOfMonth(end.dayOfMonth().getMaximumValue());

       DefaultTableModel model = DAOFactory.sqlDB().getPayrollDataTable(
          start.toDateTime(), end.toDateTime());
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }
 }
