package banking.view;

import banking.controller.AuthController;
import banking.controller.CustomerController;
import banking.model.Customer;
import banking.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class CustomerRegistrationView {
    private Stage stage;
    private AuthController authController;
    private CustomerController customerController;

    public CustomerRegistrationView(Stage stage) {
        this.stage = stage;
        this.authController = new AuthController();
        this.customerController = new CustomerController();
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        Label titleLabel = new Label("Customer Registration");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Create Your Account");
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setTextFill(Color.WHITE);

        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(30));
        formBox.setMaxWidth(450);
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label formTitle = new Label("Personal Information");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Customer Information Fields
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        firstNameField.setPrefHeight(40);

        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");
        surnameField.setPrefHeight(40);

        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        addressField.setPrefHeight(40);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setPrefHeight(40);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefHeight(40);

        Separator separator = new Separator();

        Label loginInfoLabel = new Label("Login Credentials");
        loginInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefHeight(40);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (min 6 characters)");
        passwordField.setPrefHeight(40);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setPrefHeight(40);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);

        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(200);
        registerButton.setPrefHeight(45);
        registerButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Button backButton = new Button("Back to Login");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #667eea; -fx-cursor: hand;");
        backButton.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            loginView.show();
        });

        registerButton.setOnAction(e -> {
            errorLabel.setVisible(false);

            try {
                // Validate fields
                String firstName = firstNameField.getText().trim();
                String surname = surnameField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String username = usernameField.getText().trim();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                if (firstName.isEmpty() || surname.isEmpty() || address.isEmpty() ||
                        phone.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    errorLabel.setText("All fields are required");
                    errorLabel.setVisible(true);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    errorLabel.setText("Passwords do not match");
                    errorLabel.setVisible(true);
                    return;
                }

                // Create customer
                Customer customer = new Customer();
                customer.setFirstName(firstName);
                customer.setSurname(surname);
                customer.setAddress(address);
                customer.setPhoneNumber(phone);
                customer.setEmail(email);

                if (customerController.createCustomer(customer)) {
                    // Create user account linked to customer
                    if (authController.register(username, password, User.UserRole.CUSTOMER, customer.getCustomerId())) {
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Success");
                        success.setHeaderText("Registration Successful!");
                        success.setContentText("Your account has been created. You can now login.");
                        success.showAndWait();

                        // Go back to login
                        LoginView loginView = new LoginView(stage);
                        loginView.show();
                    } else {
                        errorLabel.setText("Error creating user account");
                        errorLabel.setVisible(true);
                    }
                } else {
                    errorLabel.setText("Error creating customer profile. Email may already exist.");
                    errorLabel.setVisible(true);
                }

            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        formBox.getChildren().addAll(
                formTitle,
                firstNameField, surnameField, addressField, phoneField, emailField,
                separator,
                loginInfoLabel,
                usernameField, passwordField, confirmPasswordField,
                errorLabel,
                registerButton,
                backButton
        );

        root.getChildren().addAll(titleLabel, subtitleLabel, formBox);

        Scene scene = new Scene(root, 900, 700);
        stage.setScene(scene);
        stage.setTitle("Banking System - Customer Registration");
        stage.show();
    }
}