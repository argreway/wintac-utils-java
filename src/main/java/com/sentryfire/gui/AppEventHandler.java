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
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;

 import javax.swing.JTable;
 import javax.swing.table.DefaultTableModel;

 import com.sentryfire.SentryConfiguartion;
 import com.sentryfire.business.history.HistoryDataUtils;
 import com.sentryfire.business.utils.RealTimeDataUtils;
 import com.sentryfire.persistance.DAOFactory;
 import org.joda.time.MutableDateTime;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class AppEventHandler implements EventHandler
 {
    private Logger log = LoggerFactory.getLogger(getClass());

    private static ExecutorService executor = Executors.newFixedThreadPool(5);

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
       Runnable task = () -> {
          DAOFactory.sqlDB().connectToDB(
             window.textFieldServer.getText(),
             window.textFieldDB.getText(),
             window.textFieldUser.getText(),
             new String(window.passwordField.getPassword()));
       };
       executor.submit(task);
    }

    @Override
    public void handleDisconnectDB(ActionEvent e)
    {
       Runnable task = () -> {
          DAOFactory.sqlDB().closeDBConnection();
       };
       executor.submit(task);
    }

    @Override
    public void handleUpdateWO(ActionEvent e)
    {
       Runnable task = () -> {
          DefaultTableModel model = RealTimeDataUtils.insertWorkOrderStats();
          window.table.setModel(model);
          window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       };
       executor.submit(task);
    }

    @Override
    public void handleUpdateINV(ActionEvent e)
    {
       DefaultTableModel model = RealTimeDataUtils.insertInvoiceStats();
       window.table.setModel(model);
       window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    @Override
    public void handleUpdateAR(ActionEvent e)
    {
       Runnable task = () -> {
          DefaultTableModel model = RealTimeDataUtils.insertARStats();
          window.table.setModel(model);
          window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       };
       executor.submit(task);
    }

    @Override
    public void handleUpdatePO(ActionEvent e)
    {
       Runnable task = () -> {
          DefaultTableModel model = RealTimeDataUtils.insertPurchaseOrders();
          window.table.setModel(model);
          window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       };
       executor.submit(task);

    }

    @Override
    public void handleUpdatePay(ActionEvent e)
    {
       Runnable task = () -> {
          DefaultTableModel model = RealTimeDataUtils.insertPayrollStats();
          window.table.setModel(model);
          window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       };
       executor.submit(task);
    }

    @Override
    public void handleUpdateProp(ActionEvent e)
    {
       Runnable task = () -> {
          DefaultTableModel model = RealTimeDataUtils.insertProposalStats();
          window.table.setModel(model);
          window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       };
       executor.submit(task);
    }

    @Override
    public void handleUpdateItems(ActionEvent e)
    {
       Runnable task = () -> {

          DefaultTableModel model = RealTimeDataUtils.insertItems();
          window.table.setModel(model);
          window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       };
       executor.submit(task);

    }

    @Override
    public void handleUpdateAll(ActionEvent e)
    {
       Runnable task = () -> {
          DefaultTableModel model = RealTimeDataUtils.insertAllStats();
          window.table.setModel(model);
          window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       };
       executor.submit(task);
    }

    @Override
    public void handleCreateInflux(ActionEvent e)
    {
       Runnable task = () -> {
          DAOFactory.getInfluxClient().createDataBase();
       };
       executor.submit(task);
    }

    @Override
    public void handleDropInflux(ActionEvent e)
    {
       Runnable task = () -> {
          DAOFactory.getHistoryInfluxClient().dropDataBase();
       };
       executor.submit(task);
    }

    @Override
    public void handleCreateInfluxHistory(ActionEvent e)
    {
       Runnable task = () -> {
          DAOFactory.getHistoryInfluxClient().createDataBase();
       };
       executor.submit(task);
    }

    @Override
    public void handleDropInfluxHistory(ActionEvent e)
    {

    }

    @Override
    public void handleLoadCustomers(ActionEvent e)
    {
       Runnable task = () -> {
          DefaultTableModel model = DAOFactory.sqlDB().getCustomerList();
          window.table.setModel(model);
          window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       };
       executor.submit(task);
    }

    @Override
    public void handleSearchCustomers(ActionEvent e)
    {
       Runnable task = () -> {
          DefaultTableModel model = DAOFactory.sqlDB().getCustomerList();
          window.table.setModel(model);
          window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       };
       executor.submit(task);
    }

    @Override
    public void handleLoadWO(ActionEvent e)
    {
       Runnable task = () -> {
          MutableDateTime start = new MutableDateTime();
          start.setYear(2018);
          start.setDayOfMonth(1);
          start.setMonthOfYear(7);

          MutableDateTime end = new MutableDateTime(start);
          end.setDayOfMonth(end.dayOfMonth().getMaximumValue());

          DefaultTableModel model = RealTimeDataUtils.getOutStandingWorkOrders(start.toDateTime(), end.toDateTime());
          window.table.setModel(model);
          window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       };
       executor.submit(task);
    }

    @Override
    public void handleLoadPayroll(ActionEvent e)
    {
       Runnable task = () -> {
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
       };
       executor.submit(task);
    }

    @Override
    public void handleUpdateHistoryItems(ActionEvent e)
    {
       Runnable task = () -> {
          HistoryDataUtils.updateHistoryWOAndItems();
       };
       executor.submit(task);
    }

    @Override
    public void handleUpdateWoHistoryCount(ActionEvent e)
    {
       Runnable task = () -> {
          HistoryDataUtils.updateWOCounts();
       };
       executor.submit(task);
    }

    @Override
    public void handleUpdateLaborEff(ActionEvent e)
    {
       Runnable task = () -> {
          HistoryDataUtils.updateLaborEfficency();
       };
       executor.submit(task);
    }

    @Override
    public void handleUpdateAllHistoryStats(ActionEvent e)
    {
       Runnable task = () -> {
          HistoryDataUtils.insertAllHistoryStats();
       };
       executor.submit(task);
    }

    @Override
    public void handleLoadActivityLog(ActionEvent e)
    {
       Runnable task = () -> {
//          String value = GUIManager.getDatePanel().datePicker.getFormattedTextField().getText();
//          String value2 = GUIManager.getDatePanel().datePicker_1.getFormattedTextField().getText();
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
          window.table.setModel(model);
          window.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       };
       executor.submit(task);
    }

    @Override
    public void showCalendar(ActionEvent e)
    {
       GUIManager.showDateWindow(true);
    }

    @Override
    public void handleCalendarContinue(ActionEvent e)
    {
       GUIManager.showDateWindow(true);
       String value = GUIManager.getDatePanel().datePicker.getFormattedTextField().getText();
       String value2 = GUIManager.getDatePanel().datePicker_1.getFormattedTextField().getText();
       log.info("Found " + value + ": " + value2);
       GUIManager.showDateWindow(false);
       handleLoadActivityLog(e);
    }
 }
