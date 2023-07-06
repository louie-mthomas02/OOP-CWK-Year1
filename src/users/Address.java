package users;

public class Address {
	
	//attributes of Address
	private int houseNumber;
	private String postCode;
	private String city;
	
	//constructor of Address
	public Address(int houseNumber, String postCode, String city) {
		this.houseNumber = houseNumber;
		this.postCode = postCode;
		this.city = city;
	}
	
	//GETTER METHODS
	public int getHouseNumber() {
		return houseNumber;
	}

	public String getPostCode() {
		return postCode;
	}

	public String getCity() {
		return city;
	}
	//////////////////
	
	//For displaying when the customer has payed
	@Override
	public String toString() {
		return this.houseNumber + ", " + this.city + ", " + this.postCode;
	}
}
