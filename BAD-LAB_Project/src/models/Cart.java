package models;

public class Cart {
	private String userId;
	private String cupId;
	private String name;
	private int price;
	private int quantity;
	private int total;
	
	public Cart(String userId, String cupId, String name, int price, int quantity, int total) {
		this.userId = userId;
		this.cupId = cupId;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.total = total;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCupId() {
		return cupId;
	}

	public void setCupId(String cupId) {
		this.cupId = cupId;
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	
}
