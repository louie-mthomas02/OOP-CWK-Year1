package main;

import users.*;
import java.util.*;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import products.*;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Window.Type;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.SpinnerNumberModel;

public class CustomerFrame extends JFrame {

	private JPanel contentPane;
	private JTextField userNameText;
	private JTextField nameText;
	private JTextField userIDText;
	private JTextField houseNoText;
	private JTextField cityText;
	private JTextField postCodeText;
	private JTable productTable;
	
	private HashMap<Integer, Product> productList; //Stores the list of products in the stock.txt file (Key = Barcode, Value = Product Object)
	private HashMap<Integer, Integer> shoppingBasket; //Stores the current shopping basket of the customer (Key = Barcode, Value = Number in Basket)
	private ArrayList<Product> sortedProductList;
	private ArrayList<String> brandList; //For Brand Filter
	
	private DefaultTableModel dtmProduct;
	private JTable basketTable;
	private DefaultTableModel dtmBasket;
	
	private JLabel totalPriceLabel;
	private double totalPrice;
	
	private JLabel statusLabel;

	public CustomerFrame(Customer selectedCustomer) throws FileNotFoundException {
		setAlwaysOnTop(true);
		
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
			productList.put(barcode, newProduct);
		}
		fileProcessor.close();
		//////////////////////////////////////////////////////////////
		
		//Creates a sorted (by retail price) product list to be displayed to the customer
		sortedProductList = new ArrayList<Product>();
		brandList = new ArrayList<String>();
		for (HashMap.Entry<Integer, Product> entry : productList.entrySet()) {
			
			Product product = entry.getValue();
			
			sortedProductList.add(product);		
			
			//Adds the brand of the product to the brand filter list if not already present
			if(!brandList.contains(product.getBrand())) {
				brandList.add(product.getBrand());
			}
			///////////////////////////////////////////////////////////////////////////////
		}
		PriceCompare productPriceCompare = new PriceCompare();
		Collections.sort(sortedProductList, productPriceCompare);
		///////////////////////////////////////////////////////////////////////////////////
		
		//Instantiates a Shopping Basket for the Customer to use
		shoppingBasket = new HashMap<Integer, Integer>();
		////////////////////////////////////////////////////////
		
		setTitle("Customer Screen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 684, 399);
		contentPane.add(tabbedPane);
		
		
		//Creates the Product Catalogue for the Customer to view
		JPanel productCatalog = new JPanel();
		tabbedPane.addTab("Product Catalogue", null, productCatalog, null);
		productCatalog.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 36, 659, 290);
		productCatalog.add(scrollPane);
		
		productTable = new JTable() {
			//Prevent the user from editing table details
			public boolean editCellAt(int row, int column, java.util.EventObject e) {
				return false;
			}
		};
		scrollPane.setViewportView(productTable);
		
		dtmProduct = new DefaultTableModel();
		dtmProduct.setColumnIdentifiers(new Object[] {"Barcode", "Brand", "Product Type", "Type", "Colour", "Is Wireless?", "Quantity", "Retail Price",  "Additional Information"});
		productTable.setModel(dtmProduct);
		
		//Fills the Product Catalogue with data
		for (Product product : sortedProductList) {
			
			Object[] rowdata;
			if(product instanceof Mouse) {
				Mouse temp = (Mouse) product;
				rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Mouse", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.retailCost()), temp.getNoOfButtons() + " buttons"};
			} else {
				Keyboard temp = (Keyboard) product;
				rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Keyboard", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.retailCost()), temp.getLayout() + " layout"};
			}
			dtmProduct.addRow(rowdata);
		}
		
		JSpinner quantitySpinner = new JSpinner();
		quantitySpinner.setBounds(481, 338, 55, 20);
		productCatalog.add(quantitySpinner);
		
		JLabel quantitySelectorLabel = new JLabel("Quantity:");
		quantitySelectorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		quantitySelectorLabel.setBounds(384, 337, 87, 23);
		productCatalog.add(quantitySelectorLabel);
		
		JButton btnAddToBasketButton = new JButton("Add to Basket");
		btnAddToBasketButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//Check if the user has selected a row from the product catalog
				if(productTable.getSelectedRow() != -1) {
					
					//Grab the Product object
					Product selectedProduct = sortedProductList.get(productTable.getSelectedRow());
					
					//Get amount of quantity wanted from the user
					int quantitySelected = (int) quantitySpinner.getValue();
					
					//Check if quantity is more than 0
					if (quantitySelected > 0) {
						
						//Attempt to add product to basket
						boolean addedToBasket = false;
						if((shoppingBasket.get(selectedProduct.getBarcode()) != null) && (shoppingBasket.get(selectedProduct.getBarcode()) + quantitySelected <= selectedProduct.getQuantity())) {
							shoppingBasket.put(selectedProduct.getBarcode(), shoppingBasket.get(selectedProduct.getBarcode()) + quantitySelected);
							addedToBasket = true;
						}
						else if((shoppingBasket.get(selectedProduct.getBarcode()) == null) && quantitySelected <= selectedProduct.getQuantity()){
							shoppingBasket.put(selectedProduct.getBarcode(), quantitySelected);	
							addedToBasket = true;
						}
						
						//If added to the basket successfully, update basket table and total price
						if(addedToBasket) {
							dtmBasket.setRowCount(0);
							totalPrice = 0;
							for(HashMap.Entry<Integer, Integer> basketObject : shoppingBasket.entrySet()) {
								Product relevantProduct = productList.get(basketObject.getKey());
								
								Object[] rowdata;
								if (relevantProduct instanceof Mouse) {
									Mouse temp = (Mouse) relevantProduct;
									rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Mouse", temp.getType(), temp.getColour(), basketObject.getValue(), String.format("£%.2f", temp.retailCost())};
								} else {
									Keyboard temp = (Keyboard) relevantProduct;
									rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Keyboard", temp.getType(), temp.getColour(), basketObject.getValue(), String.format("£%.2f", temp.retailCost())};
								}
								dtmBasket.addRow(rowdata);
								
								totalPrice += (relevantProduct.retailCost() * basketObject.getValue());
							}
							
							totalPriceLabel.setText("Total: " + String.format("£%.2f", totalPrice));
							
							//Alert the user that the product has been added
							statusLabel.setText("Added to Basket!");
						
						//If the user is requesting for more stock in their basket than is available, alert them
						} else {
							statusLabel.setText("Not enough in stock.");
						}
					//Alert the user, if their selected quantity is less than or equal to zero
					} else {
						statusLabel.setText("Invalid Quantity Number");
					}
				//Alert the user if they haven't selected a product from the table
				} else {
					statusLabel.setText("No Row Selected!");
				}
			}
		});
		btnAddToBasketButton.setBounds(546, 337, 123, 23);
		productCatalog.add(btnAddToBasketButton);
		
		statusLabel = new JLabel("");
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setBounds(130, 338, 244, 21);
		productCatalog.add(statusLabel);
		
		JComboBox brandFilterComboBox = new JComboBox(brandList.toArray());
		brandFilterComboBox.setBounds(66, 7, 123, 22);
		productCatalog.add(brandFilterComboBox);
		
		JLabel lblNewLabel_3 = new JLabel("Brand:");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_3.setBounds(10, 11, 46, 14);
		productCatalog.add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("Number of Buttons:");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_4.setBounds(199, 11, 110, 14);
		productCatalog.add(lblNewLabel_4);
		
		JSpinner noOfButtonsSpinner = new JSpinner();
		noOfButtonsSpinner.setModel(new SpinnerNumberModel(new Integer(2), new Integer(2), null, new Integer(1)));
		noOfButtonsSpinner.setBounds(319, 8, 55, 20);
		productCatalog.add(noOfButtonsSpinner);
		
		JCheckBox applyNoOfButtons = new JCheckBox("Apply # Of Buttons?");
		applyNoOfButtons.setHorizontalAlignment(SwingConstants.CENTER);
		applyNoOfButtons.setBounds(384, 6, 136, 23);
		productCatalog.add(applyNoOfButtons);
		
		JButton applyFiltersButton = new JButton("Apply Filters");
		applyFiltersButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedBrand = (String) brandFilterComboBox.getSelectedItem();
				int noOfButtonsSpecified = (int)noOfButtonsSpinner.getValue();
				
				//Reset Table
				dtmProduct.setRowCount(0);
				
				//Reset Sorted Product List (so that the correct product is picked after applying filters)
				sortedProductList.clear();
				
				//Rebuild List with Filters
				if(applyNoOfButtons.isSelected()) {
					for(HashMap.Entry <Integer, Product> entry : productList.entrySet()) {
						if(entry.getValue() instanceof Mouse) {
							Mouse product = (Mouse) entry.getValue();
							
							if(product.getBrand().equals(selectedBrand) & product.getNoOfButtons() == noOfButtonsSpecified) {
								sortedProductList.add(product);
							}
						}
					}
				} else {
					for(HashMap.Entry<Integer,Product> entry : productList.entrySet()) {
						Product product = entry.getValue();
						
						if(product.getBrand().equals(selectedBrand)) {
							sortedProductList.add(product);
						}
					}
				}
				
				Collections.sort(sortedProductList, productPriceCompare);
				//////////////////////////////
				
				//Rebuild Table with Filters
				for (Product product : sortedProductList) {
					
					Object[] rowdata;
					if(product instanceof Mouse) {
						Mouse temp = (Mouse) product;
						rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Mouse", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.retailCost()), temp.getNoOfButtons() + " buttons"};
					} else {
						Keyboard temp = (Keyboard) product;
						rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Keyboard", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.retailCost()), temp.getLayout() + " layout"};
					}
					dtmProduct.addRow(rowdata);
				}
				/////////////////////////////
			}
		});
		applyFiltersButton.setBounds(526, 7, 143, 23);
		productCatalog.add(applyFiltersButton);
		
		JButton btnNewButton = new JButton("Reset Filters");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Reset Table
				dtmProduct.setRowCount(0);
				
				//Reset Sorted Product List
				sortedProductList.clear();
				
				//Rebuild List
				for(HashMap.Entry <Integer,Product> entry : productList.entrySet()) {
					Product product = entry.getValue();
					
					sortedProductList.add(product);
				}
				
				Collections.sort(sortedProductList, productPriceCompare);
				
				//Rebuild Table
				for (Product product : sortedProductList) {
					
					Object[] rowdata;
					if(product instanceof Mouse) {
						Mouse temp = (Mouse) product;
						rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Mouse", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.retailCost()), temp.getNoOfButtons() + " buttons"};
					} else {
						Keyboard temp = (Keyboard) product;
						rowdata = new Object[] {temp.getBarcode(), temp.getBrand(), "Keyboard", temp.getType(), temp.getColour(), temp.isWireless(), temp.getQuantity(), String.format("£%.2f", temp.retailCost()), temp.getLayout() + " layout"};
					}
					dtmProduct.addRow(rowdata);
				}
			}
		});
		btnNewButton.setBounds(10, 337, 110, 23);
		productCatalog.add(btnNewButton);
		
		JPanel basketTab = new JPanel();
		tabbedPane.addTab("View Basket", null, basketTab, null);
		basketTab.setLayout(null);
		
		JLabel basketStatusLabel = new JLabel("");
		basketStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		basketStatusLabel.setBounds(208, 341, 307, 14);
		basketTab.add(basketStatusLabel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 11, 659, 315);
		basketTab.add(scrollPane_1);
		
		basketTable = new JTable(){
			//Prevent the user from editing table details
			public boolean editCellAt(int row, int column, java.util.EventObject e) {
				return false;
			};
		};
		scrollPane_1.setViewportView(basketTable);
		dtmBasket = new DefaultTableModel();
		dtmBasket.setColumnIdentifiers(new Object[] {"Barcode", "Brand", "Product Type", "Type", "Colour", "Quantity in Basket", "Retail Price"});
		basketTable.setModel(dtmBasket);
		
		CustomerFrame copy = this; //Allows the Checkout Button to pass this frame if the user wants to go back
		JButton btnCheckoutButton = new JButton("Checkout");
		btnCheckoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				////////////
				//CHECKOUT//
				////////////
				if(totalPrice != 0) {
					//Pass the customer object, the product catalog, the customer's shopping basket, the total price of their basket, and this frame
					basketStatusLabel.setText("");
					CheckoutFrame chf = new CheckoutFrame(selectedCustomer, productList, shoppingBasket, totalPrice, copy);
					chf.setVisible(true);
					setVisible(false);
				} else {
					basketStatusLabel.setText("There's nothing in the basket!");
				}				
			}
		});
		btnCheckoutButton.setBounds(10, 337, 89, 23);
		basketTab.add(btnCheckoutButton);
		
		JButton btnClearBasketButton = new JButton("Clear Basket");
		btnClearBasketButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//Clear the basket and set the total price to zero
				dtmBasket.setRowCount(0);
				shoppingBasket.clear();
				totalPrice = 0;
				totalPriceLabel.setText("Total: £0.00");
			}
		});
		btnClearBasketButton.setBounds(525, 337, 144, 23);
		basketTab.add(btnClearBasketButton);
		
		totalPriceLabel = new JLabel("Total: £0.00");
		totalPriceLabel.setBounds(109, 341, 89, 14);
		basketTab.add(totalPriceLabel);
		
		JPanel accountTab = new JPanel();
		tabbedPane.addTab("View Account Details", null, accountTab, null);
		accountTab.setLayout(null);
		
		userNameText = new JTextField();
		userNameText.setHorizontalAlignment(SwingConstants.CENTER);
		userNameText.setEditable(false);
		userNameText.setBounds(10, 36, 86, 20);
		accountTab.add(userNameText);
		userNameText.setColumns(10);
		userNameText.setText(selectedCustomer.getUsername());
		
		JLabel userNameLabel = new JLabel("Username:");
		userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		userNameLabel.setBounds(10, 11, 86, 14);
		accountTab.add(userNameLabel);
		
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		nameLabel.setBounds(10, 67, 86, 14);
		accountTab.add(nameLabel);
		
		nameText = new JTextField();
		nameText.setEditable(false);
		nameText.setHorizontalAlignment(SwingConstants.CENTER);
		nameText.setBounds(10, 92, 86, 20);
		accountTab.add(nameText);
		nameText.setColumns(10);
		nameText.setText(selectedCustomer.getName());
		
		JLabel userIDLabel = new JLabel("User ID:");
		userIDLabel.setHorizontalAlignment(SwingConstants.CENTER);
		userIDLabel.setBounds(10, 123, 86, 14);
		accountTab.add(userIDLabel);
		
		userIDText = new JTextField();
		userIDText.setHorizontalAlignment(SwingConstants.CENTER);
		userIDText.setEditable(false);
		userIDText.setBounds(10, 148, 86, 20);
		accountTab.add(userIDText);
		userIDText.setColumns(10);
		userIDText.setText(Integer.toString(selectedCustomer.getUserId()));
		
		JLabel houseNoLabel = new JLabel("House Number:");
		houseNoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		houseNoLabel.setBounds(10, 179, 86, 14);
		accountTab.add(houseNoLabel);
		
		houseNoText = new JTextField();
		houseNoText.setHorizontalAlignment(SwingConstants.CENTER);
		houseNoText.setEditable(false);
		houseNoText.setBounds(10, 204, 86, 20);
		accountTab.add(houseNoText);
		houseNoText.setColumns(10);
		houseNoText.setText(Integer.toString(selectedCustomer.getAddr().getHouseNumber()));
		
		cityText = new JTextField();
		cityText.setHorizontalAlignment(SwingConstants.CENTER);
		cityText.setEditable(false);
		cityText.setBounds(10, 260, 86, 20);
		accountTab.add(cityText);
		cityText.setColumns(10);
		cityText.setText(selectedCustomer.getAddr().getCity());
		
		JLabel lblNewLabel_1 = new JLabel("City:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(10, 235, 86, 14);
		accountTab.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Postcode:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(10, 291, 86, 14);
		accountTab.add(lblNewLabel_2);
		
		JButton btnLogoutButton = new JButton("Logout");
		btnLogoutButton.addActionListener(new ActionListener() {
			
			//Send back to login frame
			public void actionPerformed(ActionEvent e) {
				LoginFrame lof = new LoginFrame();
				lof.setVisible(true);
				dispose();
			}
		});
		btnLogoutButton.setBounds(580, 337, 89, 23);
		accountTab.add(btnLogoutButton);
		
		postCodeText = new JTextField();
		postCodeText.setBounds(10, 316, 86, 20);
		accountTab.add(postCodeText);
		postCodeText.setHorizontalAlignment(SwingConstants.CENTER);
		postCodeText.setEditable(false);
		postCodeText.setColumns(10);
		postCodeText.setText(selectedCustomer.getAddr().getPostCode());
	}
}
