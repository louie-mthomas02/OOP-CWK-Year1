package products;

public class Keyboard extends Product {
	private String type; //either Standard, Gaming, or Flexible
	private String layout; //either US or UK
	
	//constructor
	public Keyboard(int barcode, String brand, String colour, boolean isWireless, int quantity, double originalCost, double retailCost, String type, String layout) {
		super(barcode, brand, colour, isWireless, quantity, originalCost, retailCost);
		this.type = type;
		this.layout = layout;
	}
	
	////////////////GETTER METHODS////////////////
	public String getType() {
		return type;
	}
	
	public String getLayout() {
		return layout;
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
		return this.getBarcode() + ",keyboard," + this.getType() + "," + this.getBrand() + "," + this.getColour() + "," + wirelessStatus + "," + this.getQuantity() + "," + this.originalCost() + "," + this.retailCost() + "," + this.getLayout();
	}
}
