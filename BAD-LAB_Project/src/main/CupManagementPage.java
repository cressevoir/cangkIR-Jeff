package main;

import java.sql.SQLException;
import java.util.ArrayList;
import util.Connect;
import models.CupList;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CupManagementPage extends Application implements EventHandler<ActionEvent> {


	public static void main(String[] args) {
		launch(args);
	}
	
	Scene scene;
	BorderPane root;
	GridPane formPane, rightForm;
	VBox rightSide;
	ScrollPane sp;
	
	MenuBar menuBar;
	Menu homeMenu;
	MenuItem cupManagement, logOut;
	
	Label title, nameLbl, priceLbl;
	TextField nameField, priceField;
	Button addBtn, updateBtn, removeBtn;
	
	TableView<CupList> cupTable;
	ArrayList<CupList> cupList = new ArrayList<CupList>();
	ArrayList<String> cupID = new ArrayList<>();
	
	Connect connect = Connect.getInstance();
	
	private Integer tempId = null;

	private void initiate() {
		
		root = new BorderPane();
		formPane = new GridPane();
		rightForm = new GridPane();
		rightSide = new VBox();
		sp = new ScrollPane();
		
		cupList = new ArrayList<CupList>();
		
		menuBar = new MenuBar();
        homeMenu = new Menu("Menu");

        cupManagement = new MenuItem("Cup Management");
        logOut = new MenuItem("Log Out");
        
        title = new Label("Cup Management");
        nameLbl = new Label("Cup Name");
        priceLbl = new Label("Cup Price");
        
        nameField = new TextField();
        priceField = new TextField();
        
        addBtn = new Button("Add Cup");
        updateBtn = new Button("Update Price");
        removeBtn = new Button("Remove Cup");
        
        cupTable = new TableView<CupList>();
        
        scene = new Scene(root, 800, 600);
	}
	
	private void addComponent() {
		
		formPane.add(title, 0, 0);
		sp.setContent(cupTable);
		sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		formPane.add(sp, 0, 1);
		
		homeMenu.getItems().add(cupManagement);
	    homeMenu.getItems().add(logOut);  
	    menuBar.getMenus().addAll(homeMenu);
	    
	    rightForm.add(nameLbl, 0, 0);
	    rightForm.add(nameField, 0, 1);
	    rightForm.add(priceLbl, 0, 3);
	    rightForm.add(priceField, 0, 4);
	    
	    rightForm.add(addBtn, 0, 6);
	    addBtn.setMinWidth(175);
	    addBtn.setMinHeight(40);
	    addBtn.setPrefWidth(175);
	    addBtn.setPrefHeight(40);
	    
	    rightForm.add(updateBtn, 0, 7);
	    updateBtn.setMinWidth(175);
	    updateBtn.setMinHeight(40);
	    updateBtn.setPrefWidth(175);
	    updateBtn.setPrefHeight(40);
	    
	    rightForm.add(removeBtn, 0, 8);
	    removeBtn.setMinWidth(175);
	    removeBtn.setMinHeight(40);
	    removeBtn.setPrefWidth(175);
	    removeBtn.setPrefHeight(40);
	    
	    rightSide.getChildren().add(rightForm);
	    
	    formPane.add(rightSide, 1, 1);
	    
	    root.setTop(menuBar);
	    root.setCenter(formPane);
	}

	public void arrangeComponent() {
		
		formPane.setAlignment(Pos.BOTTOM_LEFT);
		BorderPane.setMargin(formPane, new Insets(15));
		
		formPane.setVgap(15);
		formPane.setHgap(15);
		
		rightForm.setVgap(15);
	}
	
	public void styleComponent() {
		title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
		nameField.setPromptText("Input cup name here");
		nameField.setPrefWidth(280);
		priceField.setPromptText("Input cup price here");
		priceField.setPrefWidth(280);
	}
	
	public void setTable() {
		TableColumn<CupList, String> name = new TableColumn<CupList, String>("Cup Name");
		name.setCellValueFactory(new PropertyValueFactory<CupList, String>("name"));
		name.setMinWidth(205);
		
		TableColumn<CupList, Integer> price = new TableColumn<CupList, Integer>("Cup Price");
		price.setCellValueFactory(new PropertyValueFactory<CupList, Integer>("price"));
		price.setMinWidth(205);
		
		cupTable.getColumns().clear();
		cupTable.getColumns().addAll(name, price);
	}
	
	public void getData() {
		cupList.clear();
		cupID.clear();
		
		String query = "SELECT * FROM mscup";
		connect.rs = connect.execQuery(query);
		
		try {
			while(connect.rs.next()) {
				String id = connect.rs.getString("CupID");
				String name = connect.rs.getString("CupName");
				Integer price = connect.rs.getInt("CupPrice");
					
				cupList.add(new CupList(name, price, id));
				cupID.add(id);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void refreshTable() {
		getData();
		ObservableList<CupList> cupObj = FXCollections.observableArrayList(cupList);
		cupTable.setItems(cupObj);
		
		refreshField();
	}
	
	public void refreshField() {
		nameField.setText("");
		priceField.setText("");
		
		tempId = null;
	}
	
	public void setEvent() {
		cupManagement.setOnAction(e -> redirectCupManagementPage());
		logOut.setOnAction(e -> redirectLoginPage());
		
		addBtn.setOnAction(this);
		
		cupTable.setOnMouseClicked(e -> {
			TableSelectionModel<CupList> selectedCup = cupTable.getSelectionModel();
			selectedCup.setSelectionMode(SelectionMode.SINGLE);
			
		});
		
		cupTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue)->{
			if (newValue.intValue() >= 0) {
		        nameField.setText(cupTable.getSelectionModel().getSelectedItem().getName());
		        priceField.setText("" + cupTable.getSelectionModel().getSelectedItem().getPrice());
		    } else {
		        nameField.clear();
		        priceField.clear();
		    }
		});
		
		updateBtn.setOnAction(this);
		removeBtn.setOnAction(this);
	}
	
	private void addData(String name, Integer price) {
		int Id = cupList.size()+1;
		String Id1 = String.format("CU%03d", Id);
		while(cupID.contains(Id1)) {
			Id++;
			Id1 = String.format("CU%03d", Id);
		}
		
		String query = 
				String.format("INSERT INTO mscup VALUES('%s', '%s', %d)", 
								Id1, name, price);
		connect.execUpdate(query);

		refreshTable();
		refreshField();
	}
	
	private void updateData(String name, Integer price, String name2) {
		String query = String.format("UPDATE mscup"
				+ " SET CupName = '%s', CupPrice = %d" + 
				" WHERE CupName = '%s'", name, price, name2);
		
		connect.execUpdate(query);
			
		refreshTable();
	}
	
	private void removeData(String name) {
		String query = String.format("DELETE FROM mscup WHERE CupName = '%s'", name);
		
		connect.execUpdate(query);
		
		refreshTable();
	}

	@Override
	public void start(Stage stage) throws Exception {
		initiate();
		addComponent();
		arrangeComponent();
		styleComponent();
		setTable();
		setEvent();
		getData();
		refreshTable();
		
		stage.setTitle("cangkIR");
		stage.setScene(scene);
		stage.show();
		
	}

	@Override
	public void handle(ActionEvent e) {
		if(e.getSource() == addBtn) {
			String name = nameField.getText();
			Alert alert = new Alert(Alert.AlertType.ERROR);
			
			String priceText = priceField.getText();
		    int price = Integer.parseInt(priceText);
			
			if(name.length() == 0) {
				alert.setHeaderText("Cup Management");
				alert.setContentText("Please fill out the cup name");
				alert.show();
				return;
			} else if(!isNameUnique(name)) {
				alert.setHeaderText("Cup Management");
				alert.setContentText("Cup Already Exists");
				alert.show();
				return;
			} else if (price < 5000 || price > 1000000) {
				alert.setHeaderText("Cup Management");
				alert.setContentText("Cup price must be between 5000 and 1000000");
				alert.show();
				return;
			}
			 else {
				addData(name, price);
				
				Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
			    alert2.setHeaderText("Cup Management");
			    alert2.setContentText("Cup Succesfully Added");
			    alert2.show();
			    
			    refreshTable();
			}
			
		} if (e.getSource() == updateBtn) {
		    String name = nameField.getText();

		    if (name.isEmpty()) {
		        Alert alert = new Alert(Alert.AlertType.ERROR);
		        alert.setHeaderText("Cup Management");
		        alert.setContentText("Please select a cup from the table to be updated");
		        alert.show();
		        return;
		    }

		    String priceText = priceField.getText();
		    int price = Integer.parseInt(priceText);

		    if (price < 5000 || price > 1000000) {
		        Alert alert = new Alert(Alert.AlertType.ERROR);
		        alert.setHeaderText("Cup Management");
		        alert.setContentText("Cup price must be between 5000 and 1000000");
		        alert.show();
		        return;
		    }

		    String name2 = cupTable.getSelectionModel().getSelectedItem().getName();
		    updateData(name, price, name2);

		    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
		    alert2.setHeaderText("Cup Management");
		    alert2.setContentText("Cup Succesfully Updated");
		    alert2.show();
		    
		    refreshTable();
	
		} if(e.getSource() == removeBtn) {
			CupList selectedCup = cupTable.getSelectionModel().getSelectedItem();
	        if (selectedCup == null) {
	            Alert alerts = new Alert(Alert.AlertType.ERROR);
	            alerts.setHeaderText("Cup Management");
	            alerts.setContentText("Please select a cup from the table to be removed");
	            alerts.show();
	        } else {
	        	String name = selectedCup.getName();
				removeData(name);
				
				Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
			    alert2.setHeaderText("Cup Management");
			    alert2.setContentText("Cup Succesfully Deleted");
			    alert2.show();
			    
				refreshTable();
	        }
		}
	}

	private boolean isNameUnique(String name) {
		Connect connect = Connect.getInstance();
		String query = String.format("SELECT * FROM mscup WHERE CupName = '%s'", name);
	    connect.rs = connect.execQuery(query);

	    try {
	        return !connect.rs.next(); 
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	private void redirectCupManagementPage() {
		CupManagementPage cmp = new CupManagementPage();
		
		Stage stage = (Stage) menuBar.getScene().getWindow();
		try {
			cmp.start(stage);
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
