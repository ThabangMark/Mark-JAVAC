package banking.view;

import banking.controller.AuthController;
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

public class LoginView {
    private Stage stage;
    private AuthController authController;

    public LoginView(Stage stage) {
        this.stage = stage;
        this.authController = new AuthController();
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        Label titleLabel = new Label("Banking System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Welcome Back");
        subtitleLabel.setFont(Font.font("Arial", 18));
        subtitleLabel.setTextFill(Color.WHITE);

        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(30));
        formBox.setMaxWidth(400);
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label formTitle = new Label("Sign In");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label usernameLabel = new Label("Username");
        usernameLabel.setFont(Font.font("Arial", 14));
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefHeight(40);
        usernameField.setStyle("-fx-font-size: 14px;");

        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Arial", 14));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-font-size: 14px;");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(200);
        loginButton.setPrefHeight(45);
        loginButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #764ba2; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;"));

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter both username and password");
                errorLabel.setVisible(true);
                return;
            }

            try {
                User user = authController.login(username, password);
                if (user != null) {
                    openDashboard(user);
                } else {
                    errorLabel.setText("Invalid username or password");
                    errorLabel.setVisible(true);
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        Label registerLabel = new Label("Don't have an account? Contact admin");
        registerLabel.setFont(Font.font("Arial", 12));
        registerLabel.setTextFill(Color.GRAY);

        Label infoLabel = new Label("Default login: admin / admin123");
        infoLabel.setFont(Font.font("Arial", 11));
        infoLabel.setTextFill(Color.LIGHTGRAY);
        infoLabel.setStyle("-fx-font-style: italic;");

        formBox.getChildren().addAll(
                formTitle,
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                errorLabel,
                loginButton,
                registerLabel,
                infoLabel
        );

        root.getChildren().addAll(titleLabel, subtitleLabel, formBox);

        Scene scene = new Scene(root, 900, 700);
        stage.setScene(scene);
        stage.setTitle("Banking System - Login");
        stage.show();
    }

    private void openDashboard(User user) {
        DashboardView dashboardView = new DashboardView(stage, authController);
        dashboardView.show();
    }
}