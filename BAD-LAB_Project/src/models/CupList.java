package models;

public class CupList {
	private String name;
	private int price;
	private String cupID;
	
	public CupList(String name, int price, String cupID) {
		this.name = name;
		this.price = price;
		this.cupID = cupID;
	}
	
	public String getCupID() {
		return cupID;
	}
	public void setCupID(String cupID) {
		this.cupID = cupID;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}