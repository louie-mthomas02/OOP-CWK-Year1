package users;

public abstract class User {
	
	//attributes of ANY user
	protected int userId;
	protected String username;
	protected String name;
	protected Address addr; //contains an integer house number, string post code and a string city
	
	//constructor for User
	public User(int userId, String username, String name, int houseNumber, String postCode, String city) {
		this.userId = userId;
		this.username = username;
		this.name = name;
		this.addr = new Address(houseNumber, postCode, city);
	}
	
	
	//GETTER METHODS
	public int getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public Address getAddr() {
		return addr;
	}
	////////////////
}
