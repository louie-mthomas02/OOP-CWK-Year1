package products;

public abstract class Product {
	//attributes
	protected int barcode; //must be 6 digits
	protected String brand; //i.e. Logitech, ASUS etc.
	protected String colour; //colour of product
	protected boolean isWireless; //true = wireless, false = wired product
	protected int quantity; //must be above zero
	protected double originalCost; //must be above zero
	protected double retailCost; //must be above zero and above originalCost
	
	//constructor
	public Product(int barcode, String brand, String colour, boolean isWireless, int quantity, double originalCost, double retailCost) {
		this.barcode = barcode;
		this.brand = brand;
		this.colour = colour;
		this.isWireless = isWireless;
		this.quantity = quantity;
		this.originalCost = originalCost;
		this.retailCost = retailCost;
	}
	
	////////////////GETTER METHODS///////////////
	public int getBarcode() {
		return barcode;
	}
	
	public String getBrand() {
		return brand;
	}
	
	public String getColour() {
		return colour;
	}
	
	public boolean isWireless() {
		return isWireless;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public double originalCost() {
		return originalCost;
	}
	
	public double retailCost() {
		return retailCost;
	}
	//////////////////////////////////////////////
	
	//Function to subtract quantity in basket from quantity in stock upon customer buying product
	public void subtractQuantity(int amount) {
		this.quantity -= amount;
	}
	
	//Function to edit the quantity of product stock from the Admin
	public void addQuantity(int amount) {
		this.quantity += amount;
	}
}
