package main;

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
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.ArrayList;
import models.CupList;
import util.Connect;

public class HomePage extends Application implements EventHandler<MouseEvent>{
	
	public static void main(String[] args) {
		launch(args);
	}
	
	Scene scene;
	BorderPane borderPane;
	GridPane formPane;
	
	private static String username;
	
	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		HomePage.username = username;
	}

	MenuBar menuBar;
	Menu homeMenu;
	MenuItem home, cart, logOut;
	
	Label cupNameLbl, priceLbl, tableTitleLbl, hargaLbl;
	Spinner<Integer> nameSpinner;
	TableView<CupList> cupTable;
	Button addCartBtn;
	
	ArrayList<CupList> cupList = new ArrayList<CupList>();
	Connect connect = Connect.getInstance();
	
	
	private void initialize() {
		borderPane = new BorderPane();
		formPane = new GridPane();
		
		menuBar = new MenuBar();
        homeMenu = new Menu("Menu");

        home = new MenuItem("Home");
        cart = new MenuItem("Cart");
        logOut = new MenuItem("Log Out");
		
		cupNameLbl = new Label("Cup Name");
		priceLbl = new Label("Price:");
		tableTitleLbl = new Label("   Cup List");
		hargaLbl = new Label("");
		
		nameSpinner = new Spinner<>(1, 20, 1);
		
		cupTable = new TableView<CupList>();
	
		addCartBtn = new Button("Add to Cart");
		
		scene = new Scene(borderPane, 800, 600);
	}
	
	private void addComponent() {	    
	    homeMenu.getItems().add(home);
		homeMenu.getItems().add(cart);
		homeMenu.getItems().add(logOut);
        
        menuBar.getMenus().addAll(homeMenu);
		
        formPane.add(tableTitleLbl, 0, 0);
        formPane.add(cupTable, 0, 1, 1, 15);
        formPane.add(cupNameLbl, 1, 6, 4, 1);
        formPane.add(nameSpinner, 1, 7, 4, 1);
        formPane.add(priceLbl, 1, 8);
        formPane.add(addCartBtn, 1, 9);
        formPane.add(hargaLbl, 2, 8);
        
        borderPane.setTop(menuBar);
        borderPane.setCenter(formPane);
	}

	private void arrangeComponent() {
		formPane.setVgap(20);
		formPane.setHgap(20);
		
		formPane.setAlignment(Pos.BOTTOM_LEFT);
		
		Insets margin = new Insets(10);
		
		formPane.setMargin(cupTable, margin);
	}

	private void styleComponent() {
		cupNameLbl.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		priceLbl.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		tableTitleLbl.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		hargaLbl.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
	}
	
	private void setTable() {
	    TableColumn<CupList, String> nameColumn = new TableColumn<CupList, String>("Cup Name");
	    nameColumn.setCellValueFactory(new PropertyValueFactory<CupList, String>("name"));
	    nameColumn.setMinWidth(250);

	    TableColumn<CupList, Integer> priceColumn = new TableColumn<CupList, Integer>("Cup Price");
	    priceColumn.setCellValueFactory(new PropertyValueFactory<CupList, Integer>("price"));
	    priceColumn.setMinWidth(150); 

	    cupTable.getColumns().clear();
	    cupTable.getColumns().addAll(nameColumn, priceColumn);
	}


	private void setEvent() {
		home.setOnAction(e -> redirectHomePage());
	    cart.setOnAction(e -> redirectCartPage());
	    logOut.setOnAction(e -> redirectLoginPage());
	    
		cupTable.setOnMouseClicked(this);
		
		nameSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
	            updateTotalPrice();
	        });
		
		addCartBtn.setOnAction(addcartEvent());
	}
	
	private void getData() {
		cupList.clear();
		
		String query = "SELECT * FROM mscup";
		connect.rs = connect.execQuery(query);
		
		try {
			while(connect.rs.next()) {
				String name = connect.rs.getString("CupName");
				int price = connect.rs.getInt("CupPrice");
				String cupID = connect.rs.getString("CupID");
				
				
				CupList cup = new CupList(name, price, cupID);
				cupList.add(cup);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void addDataCart(String userID, String cupID, int quantity) {
	    String userIDQuery = String.format("SELECT UserID FROM msuser WHERE Username = '%s'", username);
	    connect.execQuery(userIDQuery);
	    
	    try {
            if (connect.rs.next()) {
                userID = connect.rs.getString("UserID");
                
                String query = String.format("INSERT INTO cart VALUES ('%s', '%s', %d)", userID, cupID, quantity);
                connect.execUpdate(query);
            } else {
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
}
	
	private void refreshTable() {
		getData();
		ObservableList<CupList> cupObj = FXCollections.observableArrayList(cupList);
		cupTable.setItems(cupObj);
	}

	@Override
	public void start(Stage stage) throws Exception {
	    initialize();
	    addComponent();
	    setTable(); 
	    arrangeComponent();
	    styleComponent();
	    setEvent();
	    getData();
	    refreshTable();

	    stage.setTitle("cangkIR");
	    stage.setScene(scene);
	    stage.show();
	}
		
	private void updateTotalPrice() {
        CupList selectedCup = cupTable.getSelectionModel().getSelectedItem();

        if (selectedCup != null) {
            cupNameLbl.setText("Cup Name: " + selectedCup.getName());

            int selectedPrice = selectedCup.getPrice();
            int spinnerValue = nameSpinner.getValue();
            int totalPrice = selectedPrice * spinnerValue;

            hargaLbl.setText("" + totalPrice);
        }
    }
	
	private EventHandler<ActionEvent> addcartEvent() {
	    return new EventHandler<ActionEvent>() {
	    	
	        @Override
	        public void handle(ActionEvent event) {
	            if (event.getSource() == addCartBtn) {
	                CupList selectedCup = cupTable.getSelectionModel().getSelectedItem();

	                Alert alert = new Alert(Alert.AlertType.ERROR);
	                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);

	                if (selectedCup == null) {
	                    alert.setHeaderText("Cart Error");
	                    alert.setContentText("Please select cup before adding to cart");
	                    alert.show();
	                } else {
	                    String name = selectedCup.getName();
	                    int price = selectedCup.getPrice();
	                    int spinnerValue = nameSpinner.getValue();
	                    int totalPrice = price * spinnerValue;

	                    String cupID = null;
	                    String userID = "";
	                    
	                    String query = "SELECT CupID FROM mscup WHERE CupName = '" + name + "'";

	                    connect.rs = connect.execQuery(query);

	                    try {
	                        if (connect.rs.next()) {
	                            cupID = connect.rs.getString("CupID");
	                        } else {
	                            return;
	                        }
	                    } catch (SQLException e) {
	                        e.printStackTrace();
	                    }

	                    String cartQuery = String.format("SELECT CupID \n" + 
	                    		"FROM cart \n" + 
	                    		"WHERE CupID = '%s' AND UserID = (SELECT UserID FROM msuser WHERE Username = '%s')", cupID, username);
	                    connect.rs = connect.execQuery(cartQuery);

	                    try {
	                        if (connect.rs.next()) {
	                            String updateQuery = String.format("UPDATE cart \n" + 
	                            		"SET Quantity = Quantity + %d \n" + 
	                            		"WHERE CupID = '%s' AND UserID = (SELECT UserID FROM msuser WHERE Username = '%s')", spinnerValue, cupID, username);
	                            connect.execUpdate(updateQuery);
	                        } else {
	                            addDataCart(userID, cupID, spinnerValue);
	                        }
	                    } catch (SQLException e) {
	                        e.printStackTrace();
	                    }

	                    refreshTable();

	                    alert2.setHeaderText("Cart Info");
	                    alert2.setContentText("Item Successfully added to cart!");
	                    alert2.show();
	                }
	            }
	        }
	    };
	}


	@Override
	public void handle(MouseEvent e) {
		if (e.getSource() == cupTable) {
			 CupList selectedCup = cupTable.getSelectionModel().getSelectedItem();
			
			 if (selectedCup != null) {
		           
		            cupNameLbl.setText("Cup Name: " + selectedCup.getName());
		            
		            int selectedPrice = selectedCup.getPrice();
		            int spinnerValue = nameSpinner.getValue();
		            int totalPrice = selectedPrice * spinnerValue;
		            
		            hargaLbl.setText("" + totalPrice);
            }
		}
		
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
