package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import products.*;
import users.*;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JFormattedTextField;

public class CheckoutFrame extends JFrame {

	private JPanel contentPane;
	private JTextField paypalTextField;
	private JTextField creditCardNumberTextField;
	private JTextField securityNumberTextField;

	/**
	 * Create the frame.
	 */
	public CheckoutFrame(Customer selectedCustomer, HashMap<Integer, Product> productList, HashMap<Integer, Integer> basket, double totalPrice, CustomerFrame cf) {
		setTitle("Checkout Screen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Total: £" + String.format("%.2f", totalPrice));
		lblNewLabel.setBounds(10, 236, 92, 14);
		contentPane.add(lblNewLabel);
		
		JLabel paymentStatusLabel = new JLabel("");
		paymentStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		paymentStatusLabel.setBounds(112, 236, 213, 14);
		contentPane.add(paymentStatusLabel);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 223, 414, 2);
		contentPane.add(separator);
		
		//Send the user back to their customer frame
		JButton btnNewButton = new JButton("Back");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cf.setVisible(true);
				dispose();
			}
		});
		btnNewButton.setBounds(335, 232, 89, 23);
		contentPane.add(btnNewButton);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 414, 200);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("PayPal", null, panel, null);
		panel.setLayout(null);
		
		paypalTextField = new JTextField();
		paypalTextField.setHorizontalAlignment(SwingConstants.CENTER);
		paypalTextField.setBounds(125, 42, 156, 20);
		panel.add(paypalTextField);
		paypalTextField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Enter your PayPal Email Address:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(10, 11, 389, 20);
		panel.add(lblNewLabel_1);
		
		JButton paypalButton = new JButton("Pay via PayPal");
		paypalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//VERY simple email pattern to validate email
				String emailRegex = "^(.+)@(.+).(.+)$";
				Pattern emailPattern = Pattern.compile(emailRegex);
				
				String enteredEmail = paypalTextField.getText();
				Matcher enteredEmailMatcher = emailPattern.matcher(enteredEmail);
				
				//If what they entered was an email, open the ConfirmPayment frame with the PayPal message
				if(enteredEmailMatcher.find()) {
					
					//Take away the quantity of selected products in the basket from that of stock
					processStock(basket, productList);
					
					ConfirmPaymentFrame cpf = new ConfirmPaymentFrame(true, selectedCustomer, totalPrice);
					cpf.setVisible(true);
					cf.dispose();
					dispose();
				
				//If not, alert user
				} else {
					paymentStatusLabel.setText("Invalid Email");
				}
			}
		});
		paypalButton.setBounds(285, 138, 114, 23);
		panel.add(paypalButton);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Credit Card", null, panel_1, null);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("Enter your 6 digit Credit Card Number:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(10, 11, 389, 14);
		panel_1.add(lblNewLabel_2);
		
		creditCardNumberTextField = new JTextField();
		creditCardNumberTextField.setHorizontalAlignment(SwingConstants.CENTER);
		creditCardNumberTextField.setBounds(110, 36, 186, 20);
		panel_1.add(creditCardNumberTextField);
		creditCardNumberTextField.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Enter your 3 digit Security Number:");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(10, 67, 389, 14);
		panel_1.add(lblNewLabel_3);
		
		securityNumberTextField = new JTextField();
		securityNumberTextField.setHorizontalAlignment(SwingConstants.CENTER);
		securityNumberTextField.setBounds(110, 92, 186, 20);
		panel_1.add(securityNumberTextField);
		securityNumberTextField.setColumns(10);
		
		JButton creditCardButton = new JButton("Pay via Credit Card");
		creditCardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//Grab card info
				String cardNumberInput = creditCardNumberTextField.getText();
				String securityNumberInput = securityNumberTextField.getText();
				
				int cardNumber;
				int securityNumber;
				
				//Validating Card Number Input
				if(cardNumberInput.length() == 6){
					//Attempts to parse the card number to Integer
					try {
						cardNumber = Integer.parseInt(cardNumberInput);
					} catch (NumberFormatException e1){
						paymentStatusLabel.setText("Invalid Card Number");
						return;
					}
				} else {
					paymentStatusLabel.setText("Invalid Card Number Length");
					return;
				}
				
				//Validating Security Number Input
				if(securityNumberInput.length() == 3) {
					try {
						securityNumber = Integer.parseInt(securityNumberInput);
					} catch (NumberFormatException e1){
						paymentStatusLabel.setText("Invalid Security Number");
						return;
					}
				} else {
					paymentStatusLabel.setText("Invalid Security Number Length");
					return;
				}
				
				//Take away the quantity of selected products in the basket from that of stock
				processStock(basket, productList);
				
				//Open Confirm Payment Frame with Credit Card message
				ConfirmPaymentFrame cpf = new ConfirmPaymentFrame(false, selectedCustomer, totalPrice);
				cpf.setVisible(true);
				cf.dispose();
				dispose();
			}
		});
		creditCardButton.setBounds(274, 138, 125, 23);
		panel_1.add(creditCardButton);
	}
	
	//Function to subtract the basket quantities from the stock file 
	private void processStock(HashMap<Integer, Integer> basket, HashMap <Integer, Product> productList) {
		
		//Grab each of the items in the basket and subtract their quantities from the product list quantities
		for(HashMap.Entry<Integer, Integer> entry : basket.entrySet()) {
			Integer barcode = entry.getKey();
			Integer quantityInBasket = entry.getValue();
			
			productList.get(barcode).subtractQuantity(quantityInBasket);
		}
		
		//Writing the new product list to the Stock.txt file
		BufferedWriter bw = null;
		try {
			FileWriter outputFile = new FileWriter("Stock.txt", false);
			bw = new BufferedWriter(outputFile);
			for(HashMap.Entry<Integer, Product> entry : productList.entrySet()) {
				Product product = entry.getValue();
				
				if(product instanceof Mouse) {
					Mouse temp = (Mouse) product;
					bw.write(temp + "\n");
				} else {
					Keyboard temp = (Keyboard) product;
					bw.write(temp + "\n");
				}
			}
		} catch (IOException e1) {
			System.err.println(e1.getMessage());
			e1.printStackTrace();
		}
		finally {
			try {
				if(bw != null) {
					bw.close();
				}
			} catch (IOException e1) {
				System.err.println(e1.getMessage());
				e1.printStackTrace();
			}
		}
	}
}
