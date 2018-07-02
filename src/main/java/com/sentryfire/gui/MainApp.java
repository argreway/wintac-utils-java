package com.sentryfire.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainApp {

	private EventHandler eh;

	public JFrame frame;
	public JTextField textFieldServer;
	public JTextField textFieldDB;
	public JTextField textFieldUser;
	public JTextField textField;
	public JButton btnButton;
	public JPasswordField passwordField;
	public final JScrollPane scrollTablePane = new JScrollPane();
	public JTable table;
	private JTextArea textAreaLogger;

	/**
	 * Launch the application.
	 */
    public static GuiThread startGui(EventHandler eh)
    {
    	GuiThread thread = new GuiThread(eh);
        EventQueue.invokeLater(thread);
        return thread;
    }

    public void setEventHandler(EventHandler eh)
    {
    	this.eh = eh;
    }

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp window = new MainApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void logMessage(String message)
	{
	   textAreaLogger.append(message);
	   textAreaLogger.setCaretPosition(textAreaLogger.getDocument().getLength());
	}

	/**
	 * Create the application.
	 */
	public MainApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frame = new JFrame();
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				eh.handleInit(e);
			}
		});
		frame.getContentPane().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				eh.handleInit(e);
			}
		});
		frame.getContentPane().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				eh.handleInit(e);
			}
		});
		frame.setBounds(100, 100, 1212, 756);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar mainMenuBar = new JMenuBar();
		mainMenuBar.setBounds(0, 0, 1210, 22);
		mainMenuBar.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
			}
		});

		JMenu mnFile = new JMenu("DB");
		mnFile.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mainMenuBar.add(mnFile);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JMenuItem mntmConnect = new JMenuItem("Connect");
		mntmConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleConnectDB(e);
			}
		});
		mntmConnect.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnFile.add(mntmConnect);

		JMenuItem mntmDisconnect = new JMenuItem("Disconnect");
		mntmDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleDisconnectDB(e);
			}
		});
		mntmDisconnect.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnFile.add(mntmDisconnect);
		mnFile.add(mntmExit);

		JMenu mnCustomers = new JMenu("Customers");
		mnCustomers.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mainMenuBar.add(mnCustomers);

		JMenuItem mntmLoadCustomers = new JMenuItem("Load Customers");
		mntmLoadCustomers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleLoadCustomers(e);
			}
		});
		mntmLoadCustomers.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnCustomers.add(mntmLoadCustomers);

		JMenuItem mntmSearchCustomers = new JMenuItem("Search Customers");
		mntmSearchCustomers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleSearchCustomers(e);
			}
		});
		mntmSearchCustomers.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnCustomers.add(mntmSearchCustomers);

		JMenu mnWorkOrders = new JMenu("Work Orders");
		mnWorkOrders.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mainMenuBar.add(mnWorkOrders);

		JMenuItem mntmOutstandingWo = new JMenuItem("Outstanding WO");
		mntmOutstandingWo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleLoadWO(e);
			}
		});
		mntmOutstandingWo.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnWorkOrders.add(mntmOutstandingWo);

		JMenu mnPayroll = new JMenu("Payroll");
		mnPayroll.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mainMenuBar.add(mnPayroll);

		JMenuItem mntmGetPayrollTable = new JMenuItem("Get Payroll Table");
		mntmGetPayrollTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleLoadPayroll(e);
			}
		});
		mntmGetPayrollTable.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnPayroll.add(mntmGetPayrollTable);

		JMenu mnNewMenu = new JMenu("Stats");
		mnNewMenu.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mainMenuBar.add(mnNewMenu);

		JMenuItem mntmUpdateWo = new JMenuItem("Update Work Orders");
		mntmUpdateWo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleUpdateWO(e);
			}
		});
		mntmUpdateWo.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnNewMenu.add(mntmUpdateWo);

		JMenuItem mntmUpdateInvoices = new JMenuItem("Update Invoices");
		mntmUpdateInvoices.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleUpdateINV(e);
			}
		});
		mntmUpdateInvoices.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnNewMenu.add(mntmUpdateInvoices);

		JMenuItem mntmUpdatePurchaseOrders = new JMenuItem("Update Purchase Orders");
		mntmUpdatePurchaseOrders.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleUpdatePO(e);
			}
		});
		mntmUpdatePurchaseOrders.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnNewMenu.add(mntmUpdatePurchaseOrders);

		JMenuItem mntmUpdateProposals = new JMenuItem("Update Proposals");
		mntmUpdateProposals.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleUpdateProp(e);
			}
		});
		mntmUpdateProposals.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnNewMenu.add(mntmUpdateProposals);

		JMenuItem mntmUpdateAr = new JMenuItem("Update AR");
		mntmUpdateAr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleUpdateAR(e);
			}
		});

		JMenuItem mntmUpdatePayroll = new JMenuItem("Update Payroll");
		mntmUpdatePayroll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleUpdatePay(e);
			}
		});
		mntmUpdatePayroll.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnNewMenu.add(mntmUpdatePayroll);
		mntmUpdateAr.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnNewMenu.add(mntmUpdateAr);

		JMenuItem mntmUpdateItems = new JMenuItem("Update Invoice Items");
		mntmUpdateItems.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleUpdateItems(e);
			}
		});
		mntmUpdateItems.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnNewMenu.add(mntmUpdateItems);

		JMenuItem mntmUpdateAllStats = new JMenuItem("Update ALL Stats");
		mntmUpdateAllStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleUpdateAll(e);
			}
		});
		mntmUpdateAllStats.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnNewMenu.add(mntmUpdateAllStats);

		JMenu mnNewMenu_1 = new JMenu("InfluxDB");
		mnNewMenu_1.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mainMenuBar.add(mnNewMenu_1);

		JMenuItem mntmCreateDb = new JMenuItem("Create DB");
		mntmCreateDb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleCreateInflux(e);
			}
		});
		mntmCreateDb.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnNewMenu_1.add(mntmCreateDb);

		JMenuItem mntmDropDb = new JMenuItem("Drop DB");
		mntmDropDb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleDropInflux(e);
			}
		});
		frame.getContentPane().setLayout(null);
		mntmDropDb.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnNewMenu_1.add(mntmDropDb);
		frame.getContentPane().add(mainMenuBar);

		JPanel pannelDB = new JPanel();
		pannelDB.setBounds(0, 22, 1210, 32);
		pannelDB.setBackground(Color.GRAY);
		frame.getContentPane().add(pannelDB);
		pannelDB.setLayout(null);

		textFieldServer = new JTextField();
		textFieldServer.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		textFieldServer.setBounds(60, 0, 145, 26);
		pannelDB.add(textFieldServer);
		textFieldServer.setColumns(10);

		textFieldDB = new JTextField();
		textFieldDB.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		textFieldDB.setColumns(10);
		textFieldDB.setBounds(243, 0, 110, 26);
		pannelDB.add(textFieldDB);

		textFieldUser = new JTextField();
		textFieldUser.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		textFieldUser.setColumns(10);
		textFieldUser.setBounds(386, 0, 110, 26);
		pannelDB.add(textFieldUser);

		JLabel lblServer = new JLabel("Server");
		lblServer.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblServer.setBounds(24, 3, 36, 23);
		pannelDB.add(lblServer);

		JLabel lblDb = new JLabel("DB");
		lblDb.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblDb.setBounds(217, 3, 36, 23);
		pannelDB.add(lblDb);

		JLabel lblUser = new JLabel("User");
		lblUser.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblUser.setBounds(361, 3, 36, 23);
		pannelDB.add(lblUser);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblPassword.setBounds(502, 2, 57, 21);
		pannelDB.add(lblPassword);

		passwordField = new JPasswordField();
		passwordField.setBounds(555, 0, 95, 26);
		pannelDB.add(passwordField);

		JPanel panelFind = new JPanel();
		panelFind.setBounds(0, 53, 1210, 34);
		panelFind.setBackground(Color.CYAN);
		frame.getContentPane().add(panelFind);
		panelFind.setLayout(null);

		textField = new JTextField();
		textField.setBounds(61, 6, 112, 22);
		textField.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		textField.setColumns(10);
		panelFind.add(textField);

		JLabel lblFind = new JLabel("Find:");
		lblFind.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblFind.setBounds(26, 8, 61, 16);
		panelFind.add(lblFind);

		JLabel labelStatus = new JLabel("");
		labelStatus.setBounds(219, 8, 436, 20);
		panelFind.add(labelStatus);

		btnButton = new JButton("Button");
		btnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnButton.setBounds(185, 3, 117, 29);
		panelFind.add(btnButton);
		scrollTablePane.setBounds(0, 87, 1210, 526);
		frame.getContentPane().add(scrollTablePane);

		table = new JTable();
		scrollTablePane.setViewportView(table);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 612, 1210, 12);
		frame.getContentPane().add(separator);

		JScrollPane scrollPaneLogs = new JScrollPane();
		scrollPaneLogs.setBounds(0, 620, 1210, 108);
		frame.getContentPane().add(scrollPaneLogs);

		textAreaLogger = new JTextArea();
		textAreaLogger.setEditable(false);
		scrollPaneLogs.setViewportView(textAreaLogger);
	}
}
