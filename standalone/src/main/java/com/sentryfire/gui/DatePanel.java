package com.sentryfire.gui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jdatepicker.JDatePicker;

public class DatePanel {

	private EventHandler eh;

	public Consumer<ActionEvent> callBack;

	public JFrame frame;
	public JDatePicker datePicker;
	public JDatePicker  datePicker_1;

    public void setEventHandler(EventHandler eh)
    {
    	this.eh = eh;
    }

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DatePanel window = new DatePanel();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DatePanel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 214, 234);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);

		JLabel lblEndDate = new JLabel("Start Date");
		GridBagConstraints gbc_lblEndDate = new GridBagConstraints();
		gbc_lblEndDate.gridwidth = 2;
		gbc_lblEndDate.insets = new Insets(0, 0, 5, 0);
		gbc_lblEndDate.gridx = 0;
		gbc_lblEndDate.gridy = 0;
		frame.getContentPane().add(lblEndDate, gbc_lblEndDate);

		datePicker = new JDatePicker();
		GridBagConstraints gbc_datePicker = new GridBagConstraints();
		gbc_datePicker.gridwidth = 2;
		gbc_datePicker.insets = new Insets(0, 0, 5, 0);
		gbc_datePicker.gridx = 0;
		gbc_datePicker.gridy = 1;
		frame.getContentPane().add(datePicker, gbc_datePicker);

		JLabel lblNewLabel = new JLabel("End Date");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		frame.getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		datePicker_1 = new JDatePicker();
		GridBagConstraints gbc_datePicker_1 = new GridBagConstraints();
		gbc_datePicker_1.insets = new Insets(0, 0, 5, 0);
		gbc_datePicker_1.gridwidth = 2;
		gbc_datePicker_1.gridx = 0;
		gbc_datePicker_1.gridy = 3;
		frame.getContentPane().add(datePicker_1, gbc_datePicker_1);

		JButton btnContinue = new JButton("Continue");
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eh.handleCalendarContinue(e);
			}
		});
		GridBagConstraints gbc_btnContinue = new GridBagConstraints();
		gbc_btnContinue.gridwidth = 2;
		gbc_btnContinue.insets = new Insets(0, 0, 5, 0);
		gbc_btnContinue.gridx = 0;
		gbc_btnContinue.gridy = 5;
		frame.getContentPane().add(btnContinue, gbc_btnContinue);
	}

}

