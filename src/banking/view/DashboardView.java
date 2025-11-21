package banking.view;

import banking.controller.AuthController;
import banking.controller.CustomerController;
import banking.controller.AccountController;
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

public class DashboardView {
    private Stage stage;
    private AuthController authController;
    private CustomerController customerController;
    private AccountController accountController;
    private BorderPane mainLayout;
    private VBox contentArea;

    public DashboardView(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;
        this.customerController = new CustomerController();
        this.accountController = new AccountController();
    }

    public void show() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        HBox topBar = createTopBar();
        mainLayout.setTop(topBar);

        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        mainLayout.setCenter(scrollPane);

        showHome();

        Scene scene = new Scene(mainLayout, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Banking System - Dashboard");
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #667eea;");
        topBar.setSpacing(20);

        Label titleLabel = new Label("Banking System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        User currentUser = authController.getCurrentUser();
        Label userLabel = new Label("Welcome, " + currentUser.getUsername());
        userLabel.setTextFill(Color.WHITE);
        userLabel.setFont(Font.font("Arial", 14));

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setOnAction(e -> {
            authController.logout();
            LoginView loginView = new LoginView(stage);
            loginView.show();
        });

        topBar.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);
        return topBar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Button homeButton = createSidebarButton("🏠 Home");
        Button customersButton = createSidebarButton("👥 Customers");
        Button accountsButton = createSidebarButton("💰 Accounts");
        Button transactionsButton = createSidebarButton("💳 Transactions");
        Button interestButton = createSidebarButton("📊 Pay Interest");

        homeButton.setOnAction(e -> showHome());
        customersButton.setOnAction(e -> showCustomers());
        accountsButton.setOnAction(e -> showAccounts());
        transactionsButton.setOnAction(e -> showTransactions());
        interestButton.setOnAction(e -> payInterestToAll());

        sidebar.getChildren().addAll(homeButton, customersButton, accountsButton, transactionsButton, interestButton);
        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(210);
        button.setPrefHeight(40);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;"));
        return button;
    }

    private void showHome() {
        contentArea.getChildren().clear();

        Label welcomeLabel = new Label("Welcome to Banking System");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));

        Label infoLabel = new Label("Manage customers, accounts, and transactions efficiently");
        infoLabel.setFont(Font.font("Arial", 16));
        infoLabel.setTextFill(Color.GRAY);

        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);

        VBox customerStat = createStatBox("Total Customers", String.valueOf(customerController.getAllCustomers().size()), "#3498db");
        VBox accountStat = createStatBox("Total Accounts", String.valueOf(accountController.getAllAccounts().size()), "#2ecc71");

        statsBox.getChildren().addAll(customerStat, accountStat);

        contentArea.getChildren().addAll(welcomeLabel, infoLabel, new Separator(), statsBox);
    }

    private VBox createStatBox(String title, String value, String color) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setPrefWidth(250);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 16));
        titleLabel.setTextFill(Color.GRAY);

        box.getChildren().addAll(valueLabel, titleLabel);
        return box;
    }

    private void showCustomers() {
        CustomerManagementView customerView = new CustomerManagementView(contentArea, customerController, accountController);
        customerView.show();
    }

    private void showAccounts() {
        AccountManagementView accountView = new AccountManagementView(contentArea, accountController, customerController);
        accountView.show();
    }

    private void showTransactions() {
        TransactionView transactionView = new TransactionView(contentArea, accountController);
        transactionView.show();
    }

    private void payInterestToAll() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Pay Interest");
        alert.setHeaderText("Pay interest to all eligible accounts?");
        alert.setContentText("This will calculate and pay monthly interest to all Savings and Investment accounts.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    accountController.payInterestToAllAccounts();
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("Interest paid to all eligible accounts successfully!");
                    success.showAndWait();
                } catch (Exception e) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText(null);
                    error.setContentText("Error paying interest: " + e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }
}
