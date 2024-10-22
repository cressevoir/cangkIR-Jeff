package main;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import util.Connect;


public class Register extends Application implements EventHandler<ActionEvent>{

	Stage primaryStage;
	Scene scene;
	GridPane gridPane;
	BorderPane borderPane; //sebagai container
	Label title, usernameLabel, emailLabel, passwordLabel, genderLabel;
	TextField usernameField, emailField;
	PasswordField passwordField;
	RadioButton maleRadio, femaleRadio;
	Button registerButton;
	Hyperlink loginLink;
	HBox genderBox;
	private Connect connect = Connect.getInstance();
	
	
	private void initialize() {
		gridPane = new GridPane();
		borderPane = new BorderPane();
	
		scene = new Scene(borderPane, 800, 600);
		
		title = new Label("Register");
		
		loginLink = new Hyperlink("Already have an account? Click here to login!");
        
		
		usernameLabel = new Label("Username");
		emailLabel = new Label("Email");
		passwordLabel = new Label("Password");
		
		genderLabel = new Label("Gender");
		
		usernameField = new TextField();
		emailField = new TextField();
		
		passwordField = new PasswordField();
		
		maleRadio = new RadioButton("Male");
		femaleRadio = new RadioButton("Female");
		ToggleGroup genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        
        genderBox = new HBox(9);
        
        registerButton =  new Button("Register");
        
        gridPane = new GridPane();
       
	}
	
	private void addComponent() {
		
		//Title
		borderPane.setTop(title);
		title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		
		//Username
		gridPane.add(usernameLabel, 0, 0);
		gridPane.add(usernameField, 0, 1);
		usernameField.setPrefColumnCount(40);
		usernameField.setPromptText("Input your username here");
		
		//Email
		gridPane.add(emailLabel, 0, 2);
		gridPane.add(emailField, 0, 3);
		emailField.setPrefColumnCount(40);
		emailField.setPromptText("Input your email here");
		
		//Password
		gridPane.add(passwordLabel, 0, 4);
		gridPane.add(passwordField, 0, 5);
		passwordField.setPrefColumnCount(40);
		passwordField.setPromptText("Input your password here");
		
		//Gender
		gridPane.add(genderLabel, 0, 6);
		genderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		gridPane.add(genderBox, 0, 7);
		genderBox.getChildren().addAll(maleRadio, femaleRadio);
		
		//Register Button
		gridPane.add(registerButton, 0, 8);
		registerButton.setPrefSize(100, 40);
		registerButton.setOnAction(e -> handle(e));
		GridPane.setHalignment(registerButton, HPos.CENTER);
		
		//HyperLink
		loginLink.setOnAction(e -> redirectToLoginPage());
		GridPane.setHalignment(loginLink, HPos.CENTER);	
		
		gridPane.setVgap(6);
		gridPane.setHgap(10);
		gridPane.setAlignment(Pos.CENTER);
		
		
		borderPane.setCenter(gridPane);
		BorderPane.setAlignment(gridPane, Pos.CENTER);
		
		BorderPane.setAlignment(title, Pos.CENTER);
		borderPane.setBottom(loginLink);
		BorderPane.setAlignment(loginLink, Pos.CENTER);
		borderPane.setPadding(new Insets(50));
	}
	
	@Override
	public void handle(ActionEvent e) {
		String username = usernameField.getText();
		String email = emailField.getText();
		String password = passwordField.getText();
		boolean isMaleSelected = maleRadio.isSelected();
		boolean isFemaleSelected = femaleRadio.isSelected();
		
		if (username.isEmpty()) {
			showAlert("Please fill out your username");
		}
		else if(email.isEmpty()) {
			showAlert("Please fill out your email");
		}
		else if(password.isEmpty()) {
			showAlert("Please fill out your password");
		}
		else if(!isMaleSelected && !isFemaleSelected) {
			showAlert("Please choose your gender");
		}
		else if(!isUsernameUnique(username)) {
			showAlert("Please choose a different username");
		}
		else if(!email.endsWith("@gmail.com")) {
			showAlert("Make sure your email ends with @gmail.com");
		}
		else if(!isEmailUnique(email)) {
			showAlert("Please choose a different email");
		}
		else if(password.length() < 8 || password.length() > 15) {
			showAlert("Make sure your password has a length of 8 -15 characters");
		}
		else if(!password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")){
			showAlert("Password must be alphanumeric");
		}else {
			
			String newUsername = usernameField.getText();
			String newEmail = emailField.getText();
			String role = assignRole(newUsername);
			
			int userIndex = getTotalUsers() + 1;
			String userID = String.format("US%03d", userIndex);
			
			String query = String.format("INSERT INTO msuser (UserID, Username, UserEmail, UserPassword, UserGender, UserRole) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
					userID, newUsername, newEmail, password, (isMaleSelected ? "Male" : "Female"), role);
			connect.execUpdate(query);
			
			redirectToLoginPage();
		}
	}


	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Register Error");
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	private void redirectToLoginPage() {
	    Login mainPage = new Login();
	    Stage primaryStage = new Stage();
	    try {
	        mainPage.start(primaryStage);
	    } catch (Exception e) {
	        e.printStackTrace();	
	    }
	    // Tutup stage saat ini (halaman registrasi)
	    Stage currentStage = (Stage) borderPane.getScene().getWindow();
	    currentStage.close();
	}
	
	public boolean isUsernameUnique(String username) {
		Connect connect = Connect.getInstance();
		String query = String.format("SELECT * FROM msuser WHERE Username = '%s'", username);
	    connect.rs = connect.execQuery(query);

	    try {
	        return !connect.rs.next(); 
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public boolean isEmailUnique(String email) {
		 String query = String.format("SELECT * FROM msuser WHERE UserEmail = '%s'", email);
		 connect.rs = connect.execQuery(query);
		 
		 try {
			 return !connect.rs.next(); 
		 } catch (SQLException e) {
			 e.printStackTrace();
			 return false;
		 }
	}
	
	public int getTotalUsers() {
        int totalUsers = 0;
        String query = "SELECT COUNT(*) FROM msuser";
        connect.rs = connect.execQuery(query);

        try {
            if (connect.rs.next()) {
                totalUsers = connect.rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalUsers;
    }
	
	public String assignRole(String newUsername) {
		if (newUsername.toLowerCase().contains("admin")) {
			return "Admin";
		} else {
			return "User";
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		initialize();
		addComponent();
		
		primaryStage.setTitle("cangkIR");
		primaryStage.setScene(scene);
		primaryStage.show();
	}


}
