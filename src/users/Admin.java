package users;

public class Admin extends User {
	
	//constructor of Admin class
	public Admin (int userId, String username, String name, int houseNumber, String postCode, String city) {
		super(userId, username, name, houseNumber, postCode, city);
	}
	
	//For displaying in GUI comboBox
	@Override
	public String toString() {
		return "Admin: " + this.getUsername();
	}	
}
