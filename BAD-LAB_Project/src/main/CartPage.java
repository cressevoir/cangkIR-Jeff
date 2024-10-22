package main;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.Window;
import models.Cart;
import models.Courier;
import util.Connect;

public class CartPage extends Application implements EventHandler<ActionEvent>{

	public static void main(String[] args) {
		launch(args);
	}
	
	Scene scene;
	BorderPane root;
	GridPane formPane, rightForm;
	VBox rightSide;
	
	MenuBar menuBar;
	Menu homeMenu;
	MenuItem home, cart, logOut;
	
	Label cartOwner, deleteItem, courier, courierPrice, totalPrice;
	TableView<Cart> cartTable;
	Button delBtn, checkoutBtn;
	ComboBox<String> courierChoices;
	CheckBox insuranceOpt;
	
	Vector<Cart> cartDatas;
	
	String cupIdTemp = null;
	
	// Checkout Confirmation
	Window popupWindow;
	Stage popupStage;
	Scene popupScene;
	Label mainLabel;
	Button yesButton, noButton;
	HBox selectionButton;
	VBox contentVbox;
	
	private static String username;
	private static String userId;

	public static void setUserId(String userId) {
		CartPage.userId = userId;
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		CartPage.username = username;
	}
	
	private Connect connect = Connect.getInstance();

	private void initialize() {
		root = new BorderPane();
		formPane = new GridPane();
		rightForm = new GridPane();
		rightSide = new VBox();
		
		cartDatas = new Vector<Cart>();
		
		menuBar = new MenuBar();
        homeMenu = new Menu("Menu");

        home = new MenuItem("Home");
        cart = new MenuItem("Cart");
        logOut = new MenuItem("Log Out");
        
		cartOwner = new Label(username + "'s Cart");
		deleteItem = new Label("Delete Item");
		courier = new Label("Courier");
		courierPrice = new Label("Courier Price :");
		totalPrice = new Label("Total Price :");
		
		cartTable = new TableView<Cart>();
		
		delBtn = new Button("Delete Item");
		checkoutBtn = new Button("Checkout");
		
		courierChoices = new ComboBox<>();
		
		insuranceOpt = new CheckBox("Use Delivery Insurance");
				
		scene = new Scene(root, 800, 600);
	}

	private void addComponent() {
		formPane.add(cartOwner, 0, 0);
		formPane.add(cartTable, 0, 1);
		
        homeMenu.getItems().add(home);
        homeMenu.getItems().add(cart);
        homeMenu.getItems().add(logOut);
        
        menuBar.getMenus().addAll(homeMenu);
		
		rightForm.add(deleteItem, 0, 0);
		
		delBtn.setMinWidth(110);
		delBtn.setMinHeight(40);
		delBtn.setPrefWidth(110);
		delBtn.setPrefHeight(40);
		rightForm.add(delBtn, 0, 1);
		
		addCourierChoices();
		
		rightForm.add(courier, 0, 2);
		rightForm.add(courierChoices, 0, 3);
		rightForm.add(courierPrice, 0, 4);
		rightForm.add(insuranceOpt, 0, 5);
		rightForm.add(totalPrice, 0, 6);
		
		checkoutBtn.setMinWidth(110);
		checkoutBtn.setMinHeight(40);
		checkoutBtn.setPrefWidth(110);
		checkoutBtn.setPrefHeight(40);
		rightForm.add(checkoutBtn, 0, 7);
		
		rightSide.getChildren().add(rightForm);
		
		formPane.add(rightSide, 1, 1);
		
		root.setTop(menuBar);
		root.setCenter(formPane);
	}
	
	private void setTable() {
		TableColumn<Cart, String> nameCol = new TableColumn<>("Cup Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.setMinWidth(150);
		
		TableColumn<Cart, Integer> priceCol = new TableColumn<>("Cup Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setMinWidth(80);
		
		TableColumn<Cart, Integer> qtyCol = new TableColumn<>("Quantity");
		qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		qtyCol.setMinWidth(80);
		
		TableColumn<Cart, Integer> totalCol = new TableColumn<>("Total");
		totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
		totalCol.setMinWidth(100);
		
		cartTable.getColumns().addAll(nameCol, priceCol, qtyCol, totalCol);
		
		refreshTable();
	}

	private void arrangeComponent() {
		formPane.setAlignment(Pos.BOTTOM_LEFT);
		BorderPane.setMargin(formPane, new Insets(10));
		formPane.setVgap(10);
		formPane.setHgap(10);
		
		rightForm.setVgap(15);
	}

	private void styleComponent() {
		cartOwner.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		deleteItem.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		courier.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		courierPrice.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		totalPrice.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
	}

	private void setEvent() {
		home.setOnAction(e -> redirectHomePage());
	    cart.setOnAction(e -> redirectCartPage());
	    logOut.setOnAction(e -> redirectLoginPage());
		
		delBtn.setOnAction(this); 
		checkoutBtn.setOnAction(this);
		
		insuranceOpt.setOnAction(e -> {
			calculateTotalPrice();
		});
		
		cartTable.setOnMouseClicked(tableMouseEvent());
	}
	
	private EventHandler<MouseEvent> tableMouseEvent(){
		return new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				TableSelectionModel<Cart> tableSelectionModel = cartTable.getSelectionModel();
				tableSelectionModel.setSelectionMode(SelectionMode.SINGLE);
				Cart cart = tableSelectionModel.getSelectedItem();
				
				cupIdTemp = cart.getCupId();
			}
			
		};
	}
	
	private void openCheckoutConfirmation() {	
		
		//Initialize
		popupStage = new Stage();
		popupWindow = new Window("Checkout Confirmation");
		mainLabel = new Label("Are you sure you want to purchase?");
		yesButton = new Button("Yes");
		noButton = new Button("No");
		selectionButton = new HBox(20);
		contentVbox = new VBox(20);
		
		//addComponent
		contentVbox.getChildren().add(mainLabel);
		contentVbox.getChildren().add(selectionButton);
		selectionButton.getChildren().addAll(yesButton, noButton);
		popupWindow.getContentPane().getChildren().add(contentVbox);
		
		//arrange
		popupWindow.setPrefSize(600, 500);
		selectionButton.setAlignment(Pos.CENTER);
		contentVbox.setAlignment(Pos.CENTER);
		popupWindow.setResizableWindow(false);
		
		//Style Component
		mainLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		yesButton.setPrefSize(70, 30);
		yesButton.setPrefSize(70, 30);
		
		noButton.setPrefSize(70, 30);
		noButton.setPrefSize(70, 30);
		
		//button EventHandler
		yesButton.setOnAction(e -> handleYesButton());
		noButton.setOnAction(e -> handleNoButton());
		
		popupScene = new Scene(popupWindow);
		
		
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setScene(popupScene);
		
		popupStage.show();
	}
	
	
	private void handleYesButton() {
		String transactionID = generateTransactionId();
		createTransactionHeader(transactionID);
		
		for(Cart cartItem : cartDatas) {
			String cupID = cartItem.getCupId();
			int quantity = cartItem.getQuantity();
			createTransactionDetail(transactionID, cupID, quantity);
		}
		
		// empty user's cart
		String query = String.format("DELETE FROM cart\n" + 
				"WHERE UserID = (SELECT UserID FROM msuser WHERE Username = '%s')", username);
		connect.execUpdate(query);
		
		refreshTable();
		
		popupStage.close();
		showAlert("Checkout successful!");
	}

	private void handleNoButton() {
		popupStage.close();
	}
	
	private String generateTransactionId() {
		int transactionCount = getTotalTransaction();
		int transactionIndex = transactionCount + 1;
		String transactionID = String.format("TR%03d", transactionIndex);
		
		return transactionID;
	}
	
	private int getTotalTransaction() {
		
		String query = "SELECT COUNT(*) FROM transactionheader";
		connect.rs = connect.execQuery(query);
		
		int transactionCount = 0;
		
		try {
			if(connect.rs.next()) {
				transactionCount = connect.rs.getInt(1);
			}
		} catch (Exception e) {
	
		}
		return transactionCount;
	}
	
	private void createTransactionHeader(String transactionID) {
		try {
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String currentDate = dateFormat.format(new Date());
			int insuranceValue = insuranceOpt.isSelected() ? 1 : 0;
			
			String courierID = getCourierId();
			
			String query = String.format("INSERT INTO transactionheader (TransactionID, UserID, CourierID, TransactionDate, UseDeliveryInsurance) VALUES ('%s', '%s', '%s', '%s', %d)",
					transactionID, Login.getCurrentUserId() , courierID , currentDate, insuranceValue);
			connect.execUpdate(query);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createTransactionDetail(String transactionID, String cupID, int quantity) {
	    try {
	        String query = String.format("INSERT INTO transactiondetail (TransactionID, CupID, Quantity) VALUES ('%s', '%s', %d)",
	                transactionID, cupID, quantity);
	        connect.execUpdate(query);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Message");
		alert.setHeaderText("Checkout Information");
		alert.setContentText(message);
		alert.showAndWait();
	}

	
	private void calculateTotalPrice() {
		String selectedCourier = courierChoices.getSelectionModel().getSelectedItem();

	    int courierCost = getCourierPrice(selectedCourier);
	    int insuranceCost = insuranceOpt.isSelected() ? 2000 : 0;

	    int totalPriceValue = calculateTotalPriceFromCart() + courierCost + insuranceCost;

	    courierPrice.setText("Courier Price : " + courierCost);
	    totalPrice.setText("Total Price : " + totalPriceValue);
	}

	private int calculateTotalPriceFromCart() {
	    int total = 0;
	    for (Cart cartItem : cartDatas) {
	        total += cartItem.getTotal();
	    }
	    return total;
	}
	
	private void getData() {
	    cartDatas.removeAllElements();

	    String query = String.format("SELECT c.CupID, CupName, CupPrice, Quantity, mc.CupPrice * c.quantity AS Total, c.UserID\n" +
                "FROM cart c\n" +
                "JOIN mscup mc ON c.CupID = mc.CupID\n" +
                "JOIN msuser mu ON c.UserID = mu.UserID\n" +
                "WHERE Username = '%s'", username);
        connect.rs = connect.execQuery(query);

        try {
            while (connect.rs.next()) {
                String cupId = connect.rs.getString("CupID");
                String name = connect.rs.getString("CupName");
                Integer price = connect.rs.getInt("CupPrice");
                Integer quantity = connect.rs.getInt("Quantity");
                Integer total = connect.rs.getInt("Total");
                String userId = connect.rs.getString("UserID");

                cartDatas.add(new Cart(userId, cupId, name, price, quantity, total));
            }
        } catch (Exception e) {
	    	
	    }
	}

	
	private void refreshTable() {
		getData();
		ObservableList<Cart> cartObs = FXCollections.observableArrayList(cartDatas);
		cartTable.setItems(cartObs);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		initialize();
		addComponent();
		arrangeComponent();
		styleComponent();
		setEvent();
		setTable();
		
		stage.setTitle("cangkIR");
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void handle(ActionEvent e) {	
		if (e.getSource() == delBtn) {
			if (cupIdTemp == null) {
		        Alert alert = new Alert(Alert.AlertType.ERROR);
		        alert.setHeaderText("Deletion Error");
		        alert.setContentText("Please select the item you want to delete");
		        alert.show();
		    } else {
				String query = String.format(
						"DELETE FROM Cart\n" + 
						"WHERE CupID = '%s'AND UserID = (SELECT UserID FROM msuser WHERE Username = '%s')", cupIdTemp, username);
				connect.execUpdate(query);
				
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
		        alert.setHeaderText("Deletion Information");
		        alert.setContentText("Cart deleted successfully!");
		        alert.show();
				
				refreshTable();
		    }
		} else if (e.getSource() == checkoutBtn) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			if (cartDatas.isEmpty()) {
				alert.setHeaderText("Checkout Error");
				alert.setContentText("There are no items to be checked out");
				alert.show();
			} else {
				openCheckoutConfirmation();
			}
		}
	}
	
	private void addCourierChoices() {
        ArrayList<Courier> couriers = getCouriersData();

        for (Courier courier : couriers) {
            courierChoices.getItems().add(courier.getCourierName());
        }

        courierChoices.setOnAction(e -> {
            String selectedCourier = courierChoices.getValue();
            int courierPrice = getCourierPrice(selectedCourier);
            updateCourierPriceLabel(courierPrice);
            calculateTotalPrice();
        });
    }
	
	private ArrayList<Courier> getCouriersData() {
	    ArrayList<Courier> couriers = new ArrayList<>();

	    String query = "SELECT * FROM mscourier";
	    connect.rs = connect.execQuery(query);

	    try {
	        while (connect.rs.next()) {
	        	String courierId = connect.rs.getString("CourierID");
	            String courierName = connect.rs.getString("CourierName");
	            int courierPrice = connect.rs.getInt("CourierPrice");
	            couriers.add(new Courier(courierId, courierName, courierPrice));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return couriers;
	}

    private int getCourierPrice(String courierName) {
        for (Courier courier : getCouriersData()) {
            if (courier.getCourierName().equals(courierName)) {
                return courier.getCourierPrice();
            }
        }

        return 0;
    }
    
    public String getCourierId() {
        String courierName = courierChoices.getValue();
        for (Courier courier : getCouriersData()) {
            if (courier.getCourierName().equals(courierName)) {
                return courier.getCourierId();
            }
        }
        return null;
    }

    
    private void updateCourierPriceLabel(int price) {
        courierPrice.setText("Courier Price: " + price);
    }
    
	private void redirectHomePage() {
		HomePage.setUsername(username);
		
	    HomePage hp = new HomePage();
	    
	    Stage stage = (Stage) menuBar.getScene().getWindow();
	    try {
			hp.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void redirectCartPage() {
		CartPage.setUsername(username);
		
	    CartPage cp = new CartPage();
	    
	    Stage stage = (Stage) menuBar.getScene().getWindow();
	    try {
			cp.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void redirectLoginPage() {
	    Login loginPage = new Login();
	    
	    Stage stage = (Stage) menuBar.getScene().getWindow();
	    try {
			loginPage.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
