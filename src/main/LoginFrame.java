package main;

import java.util.*;
import users.*;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Dialog.ModalExclusionType;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {	
	
	private JPanel contentPane;
	
	static List<User> userList;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws FileNotFoundException{
		
		//Creates a list of logins for the user to select
		userList = new ArrayList<User>();
		
		//Reading logins from UserAccounts.txt
		File userAccounts = new File("UserAccounts.txt");
		Scanner fileScanner = new Scanner(userAccounts);
		
		while(fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();
			String[] formatted = line.split(",");
			int userID = Integer.parseInt(formatted[0]);
			String username = formatted[1];
			String name = formatted[2];
			int houseNumber = Integer.parseInt(formatted[3]);
			String postcode = formatted[4];
			String city = formatted[5];
			
			User newUser;
			if (formatted[6].equals("admin")){
				newUser = new Admin(userID, username, name, houseNumber, postcode, city);
			} else {
				newUser = new Customer(userID, username, name, houseNumber, postcode, city);
			}
			
			userList.add(newUser);
		}
		
		fileScanner.close();
		//////////////////////////////////////
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoginFrame() {		
		setTitle("Login Screen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JComboBox<User> selectUserCombobox = new JComboBox(userList.toArray());
		selectUserCombobox.setBounds(10, 227, 155, 23);
		contentPane.add(selectUserCombobox);
		
		JLabel selectUserLabel = new JLabel("Select a user:");
		selectUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
		selectUserLabel.setBounds(10, 202, 155, 14);
		contentPane.add(selectUserLabel);
		
		JButton btnLoginButton = new JButton("Login");
		btnLoginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Grabs the selected login from the combo box
				User selectedUser = (User) selectUserCombobox.getSelectedItem();
				
				//If login is a Customer, instantiate Customer Frame
				if (selectedUser instanceof Customer) {
					CustomerFrame cf;
					try {
						cf = new CustomerFrame((Customer) selectedUser);
						cf.setVisible(true);
						dispose();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
				//Else, instantiate Admin Frame
				else {
					AdminFrame adf;
					try {
						adf = new AdminFrame((Admin) selectedUser);
						adf.setVisible(true);
						dispose();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		btnLoginButton.setBounds(335, 227, 89, 23);
		contentPane.add(btnLoginButton);
		
	}
}
