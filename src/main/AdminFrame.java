package main;

import users.*;
import enums.*;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTabbedPane;
import products.*;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.SpinnerNumberModel;

public class AdminFrame extends JFrame {

	private JPanel contentPane;
	private JTextField userNameText;
	private JTextField nameText;
	private JTextField userIDText;
	private JTextField houseNoText;
	private JTextField cityText;
	private JTextField postCodeText;
	private JTable productTable;
	
	private DefaultTableModel dtmProduct;
	
	private HashMap<Integer, Product> productList; //Stores the list of products in the stock.txt file (Key = Barcode, Value = Product Object)
	private ArrayList<Product> sortedProductList; //Sorted Product List by Retail List for display purposes
	private JTextField barcodeTextField;
	private JTextField brandTextField;
	private JTextField colourTextField;
	private JTextField originalPriceTextBox;
	private JTextField retailPriceTextBox;

	public AdminFrame(Admin selectedAdmin) throws FileNotFoundException {
		
		//Creates an unsorted (but easier to search) product list
		productList = new HashMap<Integer,Product>();
		File stockFile = new File("Stock.txt");
		Scanner fileProcessor = new Scanner(stockFile);
		while(fileProcessor.hasNextLine()) {
			String line = fileProcessor.nextLine();
			String[] formatted = line.split(",");
			int barcode = Integer.parseInt(formatted[0]);
			String type = formatted[2];
			String brand = formatted[3];
			String colour = formatted[4];
			boolean isWireless = formatted[5].equals("wireless");
			int quantity = Integer.parseInt(formatted[6]);
			double originalPrice = Double.parseDouble(formatted[7]);
			double retailPrice = Double.parseDouble(formatted[8]);
			
			Product newProduct;
			if(formatted[1].equals("mouse")) {
				newProduct = new Mouse(barcode, brand, colour, isWireless, quantity, originalPrice, retailPrice, type, Integer.parseInt(formatted[9]));
			} else {
				newProduct = new Keyboard(barcode, brand, colour, isWireless, quantity, originalPrice, retailPrice, type, formatted[9]);
			}
			productList.put(barcode ,newProduct);
		}
		fileProcessor.close();
		//////////////////////////////////////////////////////////////
		
		//Creates a sorted (by retail price) product list to be displayed to the admin
		sortedProductList = new ArrayList<Product>();
		for (HashMap.Entry<Integer, Product> entry : productList.entrySet()) {
			
			Product product = entry.getValue();
			
			sortedProductList.add(product);			
		}
		PriceCompare productPriceCompare = new PriceCompare();
		Collections.sort(sortedProductList, productPriceCompare);
		///////////////////////////////////////////////////////////////////////////////////
		
		setTitle("Admin Screen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 684, 399);
		contentPane.add(tabbedPane);
		
		JPanel productCatalog = new JPanel();
		tabbedPane.addTab("View All Products", null, productCatalog, null);
		productCatalog.setLayout(null);
		
		JLabel productTableStatusMessage = new JLabel("");
		productTableStatusMessage.setHorizontalAlignment(SwingConstants.CENTER);
		productTableStatusMessage.setBounds(10, 337, 267, 23);
		productCatalog.add(productTableStatusMessage);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 659, 315);
		productCatalog.add(scrollPane);
		
		productTable = new JTable() {
			//Make the product table un-editable
			public boolean editCellAt(int row, int column, java.util.EventObject e) {
				return false;
			}
		};
		scrollPane.setViewportView(productTable);
		
		dtmProduct = new DefaultTableModel();
		dtmProduct.setColumnIdentifiers(new Object[] {"Barcode", "Brand", "Product Type", "Type", "Colour", "Is Wireless?", "Quantity", "Original Price", "Retail Price",  "Additional Information"});
		productTable.setModel(dtmProduct);
		
		//Fills the Product Table with data upon load
		for (Product product : sortedProductList) {
			Object[] rowdata;
			if(product instanceof Mouse) {
				Mouse temp = (Mouse) product;
				rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Mouse", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.originalCost()), String.format("£%.2f", temp.retailCost()), temp.getNoOfButtons() + " buttons"};
			} else {
				Keyboard temp = (Keyboard) product;
				rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Keyboard", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.originalCost()), String.format("£%.2f", temp.retailCost()), temp.getLayout() + " layout"};
			}
			dtmProduct.addRow(rowdata);
		}
		
		JSpinner spinner = new JSpinner();
		spinner.setBounds(511, 338, 59, 20);
		productCatalog.add(spinner);
		
		JButton changeQuantityButton = new JButton("Enter");
		changeQuantityButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Check if the user has selected a product in the table
				if(productTable.getSelectedRow() != -1) {
					//Grab product and change in quantity
					Product selectedProduct = sortedProductList.get(productTable.getSelectedRow());
					int quantitySelected = (int) spinner.getValue();
					
					//If the change in quantity will leave the product's quantity at at least 0...
					if(selectedProduct.getQuantity() + quantitySelected >= 0) {
						//...update the quantity of that product by that amount in the stock file
						updateStock(dtmProduct, sortedProductList, productList, quantitySelected, selectedProduct.getBarcode());
						productTableStatusMessage.setText("Quantity Updated");
					} else {
						productTableStatusMessage.setText("Cannot reduce quantity below 0");
					}
				} else {
					productTableStatusMessage.setText("No Product Selected");
				}
			}
		});
		changeQuantityButton.setBounds(580, 337, 89, 23);
		productCatalog.add(changeQuantityButton);
		
		JLabel quantityChangerLabel = new JLabel("Increase/Decrease Quantity by:");
		quantityChangerLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		quantityChangerLabel.setBounds(287, 337, 214, 23);
		productCatalog.add(quantityChangerLabel);
		
		JPanel addProductTab = new JPanel();
		tabbedPane.addTab("Add Product", null, addProductTab, null);
		addProductTab.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Product Type:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 86, 14);
		addProductTab.add(lblNewLabel);
		
		JComboBox productTypeComboBox = new JComboBox(ProductType.values());
		productTypeComboBox.setBounds(10, 36, 86, 22);
		addProductTab.add(productTypeComboBox);
		
		JLabel lblNewLabel_3 = new JLabel("Barcode:");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(10, 68, 86, 14);
		addProductTab.add(lblNewLabel_3);
		
		barcodeTextField = new JTextField();
		barcodeTextField.setHorizontalAlignment(SwingConstants.CENTER);
		barcodeTextField.setBounds(10, 93, 86, 20);
		addProductTab.add(barcodeTextField);
		barcodeTextField.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Type:");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_4.setBounds(10, 124, 86, 14);
		addProductTab.add(lblNewLabel_4);
		
		JComboBox typeComboBox = new JComboBox(ProductStyle.values());
		typeComboBox.setBounds(10, 149, 86, 22);
		addProductTab.add(typeComboBox);
		
		JLabel lblNewLabel_5 = new JLabel("Brand:");
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_5.setBounds(10, 182, 86, 14);
		addProductTab.add(lblNewLabel_5);
		
		brandTextField = new JTextField();
		brandTextField.setHorizontalAlignment(SwingConstants.CENTER);
		brandTextField.setBounds(10, 207, 86, 20);
		addProductTab.add(brandTextField);
		brandTextField.setColumns(10);
		
		JLabel lblNewLabel_6 = new JLabel("Colour:");
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_6.setBounds(10, 238, 86, 14);
		addProductTab.add(lblNewLabel_6);
		
		colourTextField = new JTextField();
		colourTextField.setHorizontalAlignment(SwingConstants.CENTER);
		colourTextField.setBounds(10, 263, 86, 20);
		addProductTab.add(colourTextField);
		colourTextField.setColumns(10);
		
		JLabel lblNewLabel_7 = new JLabel("Is Wireless:");
		lblNewLabel_7.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_7.setBounds(10, 294, 182, 14);
		addProductTab.add(lblNewLabel_7);
		
		JCheckBox isWirelessCheckBox = new JCheckBox("");
		isWirelessCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		isWirelessCheckBox.setBounds(10, 315, 182, 23);
		addProductTab.add(isWirelessCheckBox);
		
		JLabel lblNewLabel_8 = new JLabel("Quantity:");
		lblNewLabel_8.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_8.setBounds(106, 11, 86, 14);
		addProductTab.add(lblNewLabel_8);
		
		JSpinner quantitySpinner = new JSpinner();
		quantitySpinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		quantitySpinner.setBounds(106, 37, 86, 20);
		addProductTab.add(quantitySpinner);
		
		originalPriceTextBox = new JTextField();
		originalPriceTextBox.setHorizontalAlignment(SwingConstants.CENTER);
		originalPriceTextBox.setBounds(106, 93, 86, 20);
		addProductTab.add(originalPriceTextBox);
		originalPriceTextBox.setColumns(10);
		
		JLabel lblNewLabel_9 = new JLabel("Original Price:");
		lblNewLabel_9.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_9.setBounds(106, 68, 86, 14);
		addProductTab.add(lblNewLabel_9);
		
		JLabel lblNewLabel_10 = new JLabel("Retail Price:");
		lblNewLabel_10.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_10.setBounds(106, 124, 86, 14);
		addProductTab.add(lblNewLabel_10);
		
		retailPriceTextBox = new JTextField();
		retailPriceTextBox.setHorizontalAlignment(SwingConstants.CENTER);
		retailPriceTextBox.setBounds(106, 150, 86, 20);
		addProductTab.add(retailPriceTextBox);
		retailPriceTextBox.setColumns(10);
		
		JLabel lblNewLabel_11 = new JLabel("No. of Buttons:");
		lblNewLabel_11.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_11.setBounds(106, 182, 86, 14);
		addProductTab.add(lblNewLabel_11);
		
		JSpinner mouseButtonsSpinner = new JSpinner();
		mouseButtonsSpinner.setModel(new SpinnerNumberModel(new Integer(2), new Integer(2), null, new Integer(1)));
		mouseButtonsSpinner.setBounds(106, 207, 86, 20);
		addProductTab.add(mouseButtonsSpinner);
		
		JLabel lblNewLabel_12 = new JLabel("Keyboard Layout:");
		lblNewLabel_12.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_12.setBounds(106, 238, 86, 14);
		addProductTab.add(lblNewLabel_12);
		
		JComboBox keyboardLayoutComboBox = new JComboBox(KeyboardLayout.values());
		keyboardLayoutComboBox.setBounds(106, 262, 86, 22);
		addProductTab.add(keyboardLayoutComboBox);
		
		JLabel addProductStatusLabel = new JLabel("");
		addProductStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		addProductStatusLabel.setBounds(202, 11, 467, 315);
		addProductTab.add(addProductStatusLabel);
		
		JButton createNewProductButton = new JButton("Add Product");
		createNewProductButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Declaring the attributes of the product
				ProductType productType = (ProductType) productTypeComboBox.getSelectedItem();
				int barcode;
				String type;
				String brand = brandTextField.getText();
				String colour = colourTextField.getText();
				boolean isWireless = isWirelessCheckBox.isSelected();
				int quantity = (int) quantitySpinner.getValue();
				double originalPrice;
				double retailPrice;
				int noOfButtons = (int) mouseButtonsSpinner.getValue();
				String keyboardLayout;
				///////////////////////////////////////////
				
				//Getting Barcode
				if(barcodeTextField.getText().length() == 6) {
					try {
						barcode = Integer.parseInt(barcodeTextField.getText());
					} catch (NumberFormatException e1) {
						addProductStatusLabel.setText("Barcode should be numeric");
						return;
					}
				} else {
					addProductStatusLabel.setText("Barcode Length MUST be 6 digits");
					return;
				}
				//////////////////
				
				//Getting Type of Product
				if(!(productType == ProductType.MOUSE && (ProductStyle) typeComboBox.getSelectedItem() == ProductStyle.FLEXIBLE)) {
					switch((ProductStyle) typeComboBox.getSelectedItem()) {
						case STANDARD:
							type = "standard";
							break;
						case GAMING:
							type = "gaming";
							break;
						default:
							type = "flexible";
							break;
					}
				} else {
					addProductStatusLabel.setText("Product CANNOT be a Mouse and Flexible!");
					return;
				}
				////////////////////////////
				
				//Getting Original Price
				try {
					originalPrice = Double.parseDouble(originalPriceTextBox.getText());
					if(originalPrice < 0) {
						addProductStatusLabel.setText("Original Price needs to be above zero!");
						return;
					}
				} catch (NumberFormatException e1) {
					addProductStatusLabel.setText("Original Price should be numeric!");
					return;
				}
				/////////////////////////
				
				//Getting Retail Price
				try {
					retailPrice = Double.parseDouble(retailPriceTextBox.getText());
					if(retailPrice < 0) {
						addProductStatusLabel.setText("Retail Price needs to be above zero!");
						return;
					} else if(retailPrice <= originalPrice) {
						addProductStatusLabel.setText("Retail Price should be above Original Price");
						return;
					}
				} catch (NumberFormatException e1) {
					addProductStatusLabel.setText("Retail Price should be numeric!");
					return;
				}
				/////////////////////////
				
				//Getting Keyboard Layout
				switch((KeyboardLayout) keyboardLayoutComboBox.getSelectedItem()) {
					case UK:
						keyboardLayout = "UK";
						break;
					default:
						keyboardLayout = "US";
						break;
				}
				//////////////////////////
				
				//Adding the Product into the Stock
				if(productList.get(barcode) == null) {
					Product newProduct;
					
					switch(productType) {
						case MOUSE:
							newProduct = new Mouse(barcode, brand, colour, isWireless, quantity, originalPrice, retailPrice, type, noOfButtons);
							break;
							
						default:
							newProduct = new Keyboard(barcode, brand, colour, isWireless, quantity, originalPrice, retailPrice, type, keyboardLayout);
							break;
					}
					
					productList.put(barcode, newProduct);
					
					sortedProductList.add(newProduct);
					Collections.sort(sortedProductList, productPriceCompare);
					
					//Rebuilding the Product Table
					dtmProduct.setRowCount(0);
					for (Product product : sortedProductList) {
						Object[] rowdata;
						if(product instanceof Mouse) {
							Mouse temp = (Mouse) product;
							rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Mouse", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.originalCost()), String.format("£%.2f", temp.retailCost()), temp.getNoOfButtons() + " buttons"};
						} else {
							Keyboard temp = (Keyboard) product;
							rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Keyboard", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.originalCost()), String.format("£%.2f", temp.retailCost()), temp.getLayout() + " layout"};
						}
						dtmProduct.addRow(rowdata);
					}
					///////////////////////////////
					
					//Writing the new product to Stock.txt
					BufferedWriter bw = null;
					try {
						FileWriter outputFile = new FileWriter("Stock.txt", true);
						bw = new BufferedWriter(outputFile);
						if(newProduct instanceof Mouse) {
							Mouse temp = (Mouse) newProduct;
							bw.write(temp + "\n");
						} else {
							Keyboard temp = (Keyboard) newProduct;
							bw.write(temp + "\n");
						}
						addProductStatusLabel.setText("Product Created!");
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
					//////////////////////////////////////
					
				} else {
					addProductStatusLabel.setText("Barcode already exists!");
					return;
				}
				///////////////////////////////////
			}
		});
		createNewProductButton.setBounds(525, 337, 144, 23);
		addProductTab.add(createNewProductButton);
		
		JPanel accountTab = new JPanel();
		tabbedPane.addTab("View Account Details", null, accountTab, null);
		accountTab.setLayout(null);
		
		userNameText = new JTextField();
		userNameText.setText((String) null);
		userNameText.setHorizontalAlignment(SwingConstants.CENTER);
		userNameText.setEditable(false);
		userNameText.setColumns(10);
		userNameText.setBounds(10, 36, 86, 20);
		accountTab.add(userNameText);
		userNameText.setText(selectedAdmin.getUsername());
		
		JLabel userNameLabel = new JLabel("Username:");
		userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		userNameLabel.setBounds(10, 11, 86, 14);
		accountTab.add(userNameLabel);
		
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		nameLabel.setBounds(10, 67, 86, 14);
		accountTab.add(nameLabel);
		
		nameText = new JTextField();
		nameText.setText((String) null);
		nameText.setHorizontalAlignment(SwingConstants.CENTER);
		nameText.setEditable(false);
		nameText.setColumns(10);
		nameText.setBounds(10, 92, 86, 20);
		accountTab.add(nameText);
		nameText.setText(selectedAdmin.getName());
		
		JLabel userIDLabel = new JLabel("User ID:");
		userIDLabel.setHorizontalAlignment(SwingConstants.CENTER);
		userIDLabel.setBounds(10, 123, 86, 14);
		accountTab.add(userIDLabel);
		
		userIDText = new JTextField();
		userIDText.setText("0");
		userIDText.setHorizontalAlignment(SwingConstants.CENTER);
		userIDText.setEditable(false);
		userIDText.setColumns(10);
		userIDText.setBounds(10, 148, 86, 20);
		accountTab.add(userIDText);
		userIDText.setText(Integer.toString(selectedAdmin.getUserId()));
		
		JLabel houseNoLabel = new JLabel("House Number:");
		houseNoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		houseNoLabel.setBounds(10, 179, 86, 14);
		accountTab.add(houseNoLabel);
		
		houseNoText = new JTextField();
		houseNoText.setText("0");
		houseNoText.setHorizontalAlignment(SwingConstants.CENTER);
		houseNoText.setEditable(false);
		houseNoText.setColumns(10);
		houseNoText.setBounds(10, 204, 86, 20);
		accountTab.add(houseNoText);
		houseNoText.setText(Integer.toString(selectedAdmin.getAddr().getHouseNumber()));
		
		cityText = new JTextField();
		cityText.setText((String) null);
		cityText.setHorizontalAlignment(SwingConstants.CENTER);
		cityText.setEditable(false);
		cityText.setColumns(10);
		cityText.setBounds(10, 260, 86, 20);
		accountTab.add(cityText);
		cityText.setText(selectedAdmin.getAddr().getCity());
		
		JLabel lblNewLabel_1 = new JLabel("City:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(10, 235, 86, 14);
		accountTab.add(lblNewLabel_1);
		
		postCodeText = new JTextField();
		postCodeText.setText((String) null);
		postCodeText.setHorizontalAlignment(SwingConstants.CENTER);
		postCodeText.setEditable(false);
		postCodeText.setColumns(10);
		postCodeText.setBounds(10, 316, 86, 20);
		accountTab.add(postCodeText);
		postCodeText.setText(selectedAdmin.getAddr().getPostCode());
		
		JLabel lblNewLabel_2 = new JLabel("Postcode:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(10, 291, 86, 14);
		accountTab.add(lblNewLabel_2);
		
		JButton btnLogoutButton = new JButton("Logout");
		btnLogoutButton.addActionListener(new ActionListener() {
			//Allow the user to get back to the login frame
			public void actionPerformed(ActionEvent e) {
				LoginFrame lof = new LoginFrame();
				lof.setVisible(true);
				dispose();
			}
		});
		btnLogoutButton.setBounds(580, 337, 89, 23);
		accountTab.add(btnLogoutButton);
	}
	
	private void updateStock(DefaultTableModel dtmProduct, ArrayList<Product> sortedProductList, HashMap<Integer, Product> productList, int changeInQuantity, int barcode) {
		//Clears the table
		dtmProduct.setRowCount(0);
		
		//Updates the quantity
		productList.get(barcode).addQuantity(changeInQuantity);
		
		//Writes the new quantity into the Stock.txt file
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
		
		//Rebuilds the table
		for (Product product : sortedProductList) {
			Object[] rowdata;
			if(product instanceof Mouse) {
				Mouse temp = (Mouse) product;
				rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Mouse", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.originalCost()), String.format("£%.2f", temp.retailCost()), temp.getNoOfButtons() + " buttons"};
			} else {
				Keyboard temp = (Keyboard) product;
				rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Keyboard", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.originalCost()), String.format("£%.2f", temp.retailCost()), temp.getLayout() + " layout"};
			}
			dtmProduct.addRow(rowdata);
		}
	}
}
