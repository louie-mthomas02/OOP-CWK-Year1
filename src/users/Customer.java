package users;

public class Customer extends User {
	
	//constructor of Customer class
	public Customer(int userId, String username, String name, int houseNumber, String postCode, String city) {
		super(userId, username, name, houseNumber, postCode, city);
	}
	
	//For displaying in GUI comboBox
	@Override
	public String toString() {
		return "Customer: " + this.getUsername();
	}
}
