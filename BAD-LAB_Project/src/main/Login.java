package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Connect;

public class Login extends Application implements EventHandler<ActionEvent>{

	public static void main(String[] args) {
		launch(args);
	}
	
	Scene scene;
	BorderPane bp;
	GridPane gp;
	VBox vb, vb2;
	
	Label titleLbl, usernameLbl, passwordLbl;
	TextField usernameField;
	PasswordField passField;
	
	Button loginBtn;
	Hyperlink regLink;
	
	private static String userId;
	private static String username;
	
	private Connect connect = Connect.getInstance();

	private void initialize() {
		bp = new BorderPane();
		gp = new GridPane();
		vb = new VBox();
		vb2 = new VBox();
		
		titleLbl = new Label("Login");
		usernameLbl = new Label("Username");
		passwordLbl = new Label("Password");
		usernameField = new TextField();
		passField = new PasswordField();
		
		loginBtn = new Button("Login");
		
		regLink = new Hyperlink("Don't have an account yet? Register here!");
		
		scene = new Scene(bp, 800, 600);
	}
	
	private void addComponent() {
		// Title
		GridPane.setHalignment(titleLbl, HPos.CENTER);
		gp.add(titleLbl, 0, 0);
		
		// Username
		vb.getChildren().addAll(usernameLbl, usernameField);
		GridPane.setColumnSpan(vb, 3);
		gp.add(vb, 0, 1);
		
		// Password
		vb2.getChildren().addAll(passwordLbl, passField);
		GridPane.setColumnSpan(vb2, 3);
		gp.add(vb2, 0, 3);
		
		// Login
	    loginBtn.setMinWidth(80);
	    loginBtn.setMinHeight(35);
	    loginBtn.setPrefWidth(80);
	    loginBtn.setPrefHeight(35);
		GridPane.setHalignment(loginBtn, HPos.CENTER);
		gp.add(loginBtn, 0, 5);

		// Hyperlink
		GridPane.setHalignment(regLink, HPos.CENTER);
		gp.add(regLink, 0, 6);
		
		bp.setCenter(gp);
	}

	private void arrangeComponent() {
		gp.setAlignment(Pos.CENTER);
		gp.setVgap(12);
	}

	private void styleComponent() {
		titleLbl.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
		usernameField.setPromptText("Input your username here");
		passField.setPromptText("Input your password here");
	}
	
	private void setEvent() {
		regLink.setOnAction(e -> {
			Register reg = new Register();
			
			Stage stage = (Stage) regLink.getScene().getWindow();
			
			try {
				reg.start(stage);
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		});
		
		loginBtn.setOnAction(this);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		initialize();
		addComponent();
		arrangeComponent();
		styleComponent();
		setEvent();
		
		stage.setTitle("cangkIR");
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void handle(ActionEvent e) {
		if (e.getSource() == loginBtn) {
			username = usernameField.getText();
			String password = passField.getText();
			
			Alert alert = new Alert(Alert.AlertType.ERROR);
			if (username.isEmpty()) {
				alert.setHeaderText("Login Error");
				alert.setContentText("Please fill out your username");
				alert.show();
			} else if (password.isEmpty()) {
				alert.setHeaderText("Login Error");
				alert.setContentText("Please fill out your password");
				alert.show();
			} else {
				if (validateCredentials(username, password)) {
					redirectUser(username);
				} else {
	            	alert.setHeaderText("Login Error");
					alert.setContentText("Invalid username or password.");
					alert.show();
	            }
			}
		}
	}	
	
	private boolean validateCredentials(String username, String password) {
		String query = "SELECT * FROM msuser WHERE Username = ? AND UserPassword = ?";
	    
	    try (PreparedStatement preparedStatement = connect.getConnection().prepareStatement(query)) {
	        preparedStatement.setString(1, username);
	        preparedStatement.setString(2, password);
	        
	        try (ResultSet resultSet = preparedStatement.executeQuery()) {
	        	if (resultSet.next()) {
	                 userId = resultSet.getString("UserID");
	                 return true;
	             }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	private void redirectUser(String username) {
	    String query = "SELECT UserRole FROM msuser WHERE Username = ?";
	    
	    try (PreparedStatement ps = connect.getConnection().prepareStatement(query)) {
	        ps.setString(1, username);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                String userRole = rs.getString("UserRole");

	                if ("Admin".equalsIgnoreCase(userRole)) {
	                    redirectAdminPage();
	                } else if ("User".equalsIgnoreCase(userRole)) {
	                    redirectHomePage();    
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public static String getCurrentUsername() {
		return username;
	}
	
	public static String getCurrentUserId() {
		return userId;
	}
	
	private void redirectAdminPage() {
		
		CupManagementPage cmp = new CupManagementPage();
		
		Stage stage = (Stage) loginBtn.getScene().getWindow();
		
		try {
			cmp.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void redirectHomePage() {
		HomePage.setUsername(username);
		
		HomePage hp = new HomePage();
		
		Stage stage = (Stage) loginBtn.getScene().getWindow();
		
		try {
			hp.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
