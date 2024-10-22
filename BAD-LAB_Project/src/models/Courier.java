package models;

public class Courier {
	private String courierId;
	private String courierName;
	private int courierPrice;
	
	public Courier(String courierId, String courierName, int courierPrice) {
		this.courierId = courierId;
		this.courierName = courierName;
		this.courierPrice = courierPrice;
	}

	public String getCourierId() {
		return courierId;
	}

	public void setCourierId(String courierId) {
		this.courierId = courierId;
	}

	public String getCourierName() {
		return courierName;
	}

	public void setCourierName(String courierName) {
		this.courierName = courierName;
	}

	public int getCourierPrice() {
		return courierPrice;
	}

	public void setCourierPrice(int courierPrice) {
		this.courierPrice = courierPrice;
	}
}
