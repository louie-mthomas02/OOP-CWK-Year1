package main;

import java.awt.BorderLayout;
import users.*;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.awt.event.ActionEvent;

public class ConfirmPaymentFrame extends JFrame {

	private JPanel contentPane;
	
	/**
	 * Create the frame.
	 */
	
	public ConfirmPaymentFrame(boolean withPaypal, Customer customer, double totalPrice) {
		setTitle("Payment Confirmed");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 609, 401);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(10, 11, 573, 340);
		contentPane.add(panel);
		panel.setLayout(null);
		
		//Send the user to a fresh customer frame with same account
		JButton shopMoreButton = new JButton("Shop More");
		shopMoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				try {
					CustomerFrame cf = new CustomerFrame(customer);
					cf.setVisible(true);
					dispose();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		shopMoreButton.setBounds(474, 238, 89, 23);
		panel.add(shopMoreButton);
		
		//Exit application
		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		exitButton.setBounds(474, 306, 89, 23);
		panel.add(exitButton);
		
		//Send the user to the login frame where they can select a new account
		JButton logoutButton = new JButton("Logout");
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoginFrame lf = new LoginFrame();
				lf.setVisible(true);
				dispose();
			}
		});
		logoutButton.setBounds(474, 272, 89, 23);
		panel.add(logoutButton);
		
		JLabel titleLabel = new JLabel("Payment Confirmed!");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(10, 11, 553, 23);
		panel.add(titleLabel);
		
		JLabel confirmMessage = new JLabel("");
		confirmMessage.setHorizontalAlignment(SwingConstants.CENTER);
		confirmMessage.setBounds(10, 45, 553, 182);
		panel.add(confirmMessage);
		
		JLabel thankYouMessage = new JLabel("Thank You for Shopping with Us!");
		thankYouMessage.setHorizontalAlignment(SwingConstants.CENTER);
		thankYouMessage.setBounds(10, 238, 454, 91);
		panel.add(thankYouMessage);
		
		//If they have paid with PayPal, display this message
		if(withPaypal) {
			confirmMessage.setText("£" + String.format("%.2f", totalPrice) + " paid using PayPal, and the delivery address is " + customer.getAddr());
		
		//If with Credit Card instead, display this message
		} else {
			confirmMessage.setText("£" + String.format("%.2f", totalPrice) + " paid using Credit Card, and the delivery address is " + customer.getAddr());
		}
	}
}
