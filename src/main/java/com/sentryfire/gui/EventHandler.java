package com.sentryfire.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;

public interface EventHandler {

	public void handleInit(ComponentEvent ce);
	public void setMainApp(MainApp window);

	// DB Menu
	public void handleConnectDB(ActionEvent e);
	public void handleDisconnectDB(ActionEvent e);

	// Customers Menu
	public void handleLoadCustomers(ActionEvent e);
	public void handleSearchCustomers(ActionEvent e);

	// WO Menu
	public void handleLoadWO(ActionEvent e);
	// Payroll Menu

	public void handleLoadPayroll(ActionEvent e);

	//Stats Menu
	public void handleUpdateWO(ActionEvent e);
	public void handleUpdateINV(ActionEvent e);
	public void handleUpdateAR(ActionEvent e);
	public void handleUpdatePO(ActionEvent e);
	public void handleUpdatePay(ActionEvent e);
	public void handleUpdateProp(ActionEvent e);
	public void handleUpdateItems(ActionEvent e);
	public void handleUpdateAll(ActionEvent e);

	// InfluxDB Menu
	public void handleCreateInflux(ActionEvent e);
	public void handleDropInflux(ActionEvent e);
	public void handleCreateInfluxHistory(ActionEvent e);
	public void handleDropInfluxHistory(ActionEvent e);

	// Schedule Menu
	public void handleUpdateScheduleItems(ActionEvent e);

}

