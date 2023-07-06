package products;

public class Mouse extends Product {
	private String type; //either Standard or Gaming
	private int numberOfButtons; //must be at least 2
	
	public Mouse(int barcode, String brand, String colour, boolean isWireless, int quantity, double originalCost, double retailCost, String type, int numberOfButtons) {
		super(barcode, brand, colour, isWireless, quantity, originalCost, retailCost);
		this.type = type;
		this.numberOfButtons = numberOfButtons;
	}
	
	////////////////GETTER METHODS////////////////
	public String getType() {
		return type;
	}
	
	public int getNoOfButtons() {
		return numberOfButtons;
	} 
	//////////////////////////////////////////////
	
	//For writing the product info to Stock.txt
	@Override
	public String toString() {
		String wirelessStatus;
		if (this.isWireless()) {
			wirelessStatus = "wireless";
		} else {
			wirelessStatus = "wired";
		}
		return this.getBarcode() + ",mouse," + this.getType() + "," + this.getBrand() + "," + this.getColour() + "," + wirelessStatus + "," + this.getQuantity() + "," + this.originalCost() + "," + this.retailCost() + "," + this.getNoOfButtons();
	}
}
