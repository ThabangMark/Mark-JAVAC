import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class BankingSystemGUI extends Application {
    
    private String userRole;
    private String currentUsername;
    private String currentUserId;
    private Stage primaryStage;
    
    private ObservableList<User> users;
    private ObservableList<AccountView> accountData;
    private ObservableList<Transaction> transactions;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize with only manager account
        users = FXCollections.observableArrayList();
        users.add(new User("manager1", "admin123", "Manager", "MGR001", "Mr Odirile"));
        
        accountData = FXCollections.observableArrayList();
        transactions = FXCollections.observableArrayList();
        
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Botswana National Bank - Banking System");
        showLoginView();
        primaryStage.show();
    }
    
    private void showLoginView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #EFF6FF, #E0E7FF);");
        
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));
        centerBox.setMaxWidth(500);
        centerBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        
        Label titleLabel = new Label("Botswana National Bank");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#1E40AF"));
        
        Label subtitleLabel = new Label("Banking System");
        subtitleLabel.setFont(Font.font("System", 16));
        subtitleLabel.setTextFill(Color.web("#6B7280"));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefHeight(40);
        usernameField.setStyle("-fx-font-size: 14px;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-font-size: 14px;");
        
        Button loginBtn = new Button("Login");
        loginBtn.setPrefHeight(45);
        loginBtn.setPrefWidth(200);
        loginBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        loginBtn.setOnAction(e -> {
            Optional<User> user = authenticateUser(usernameField.getText(), passwordField.getText());
            if (user.isPresent()) {
                User loggedInUser = user.get();
                userRole = loggedInUser.getRole();
                currentUsername = loggedInUser.getFullName();
                currentUserId = loggedInUser.getUserId();
                
                if (userRole.equals("Manager")) {
                    showManagerDashboard();
                } else {
                    showCustomerDashboard();
                }
            }
        });
        
        Button registerBtn = new Button("Register as Customer");
        registerBtn.setPrefHeight(45);
        registerBtn.setPrefWidth(200);
        registerBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        registerBtn.setOnAction(e -> showRegistrationView());
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginBtn, registerBtn);
        
        Region spacer1 = new Region();
        spacer1.setPrefHeight(10);
        
        centerBox.getChildren().addAll(titleLabel, subtitleLabel, spacer1, usernameField, passwordField, buttonBox);
        
        StackPane centerPane = new StackPane(centerBox);
        centerPane.setPadding(new Insets(50));
        root.setCenter(centerPane);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }
    
    private Optional<User> authenticateUser(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Please enter username and password", Alert.AlertType.ERROR);
            return Optional.empty();
        }
        
        Optional<User> user = users.stream()
            .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
            .findFirst();
        
        if (!user.isPresent()) {
            showAlert("Login Error", "Invalid username or password", Alert.AlertType.ERROR);
        }
        return user;
    }
    
    private void showRegistrationView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #EFF6FF, #E0E7FF);");
        
        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));
        centerBox.setMaxWidth(500);
        centerBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        
        Label titleLabel = new Label("Customer Registration");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#1E40AF"));
        
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        fullNameField.setPrefHeight(40);
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefHeight(40);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(40);
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setPrefHeight(40);
        
        Button registerBtn = new Button("Register");
        registerBtn.setPrefHeight(45);
        registerBtn.setPrefWidth(200);
        registerBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold;");
        registerBtn.setOnAction(e -> {
            if (validateRegistration(fullNameField.getText(), usernameField.getText(), 
                                    passwordField.getText(), confirmPasswordField.getText())) {
                String newUserId = "CUST" + String.format("%03d", users.stream()
                    .filter(u -> u.getRole().equals("Customer")).count() + 1);
                users.add(new User(usernameField.getText(), passwordField.getText(), 
                                  "Customer", newUserId, fullNameField.getText()));
                
                showAlert("Success", "Registration successful!\n\nYour Customer ID: " + newUserId + 
                         "\n\nPlease login and contact the manager to open an account.", Alert.AlertType.INFORMATION);
                showLoginView();
            }
        });
        
        Button backBtn = new Button("Back to Login");
        backBtn.setPrefHeight(45);
        backBtn.setPrefWidth(200);
        backBtn.setStyle("-fx-background-color: #6B7280; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> showLoginView());
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(registerBtn, backBtn);
        
        centerBox.getChildren().addAll(titleLabel, fullNameField, usernameField, passwordField, confirmPasswordField, buttonBox);
        
        StackPane centerPane = new StackPane(centerBox);
        centerPane.setPadding(new Insets(50));
        root.setCenter(centerPane);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }
    
    private boolean validateRegistration(String fullName, String username, String password, String confirmPassword) {
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match!", Alert.AlertType.ERROR);
            return false;
        }
        if (password.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters!", Alert.AlertType.ERROR);
            return false;
        }
        if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
            showAlert("Error", "Username already exists!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private void showCustomerDashboard() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F9FAFB;");
        
        HBox header = createHeader("Customer Portal", currentUsername + " (" + currentUserId + ")");
        root.setTop(header);
        
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setFillWidth(true);
        
        ObservableList<AccountView> myAccounts = accountData.filtered(
            account -> account.getOwnerId().equals(currentUserId)
        );
        
        if (myAccounts.isEmpty()) {
            VBox emptyState = new VBox(20);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(50));
            emptyState.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
            
            Label emptyIcon = new Label("🏦");
            emptyIcon.setFont(Font.font(60));
            Label emptyTitle = new Label("No Accounts Found");
            emptyTitle.setFont(Font.font("System", FontWeight.BOLD, 24));
            Label emptyMessage = new Label("Contact the bank manager to open an account.");
            emptyMessage.setFont(Font.font("System", 14));
            emptyMessage.setTextFill(Color.web("#6B7280"));
            
            emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyMessage);
            mainContent.getChildren().add(emptyState);
        } else {
            Label accountsLabel = new Label("My Accounts");
            accountsLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
            
            FlowPane accountsFlow = new FlowPane();
            accountsFlow.setHgap(20);
            accountsFlow.setVgap(20);
            
            for (AccountView account : myAccounts) {
                accountsFlow.getChildren().add(createAccountCard(account));
            }
            
            Label actionsLabel = new Label("Quick Actions");
            actionsLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
            
            FlowPane actionsBox = new FlowPane();
            actionsBox.setHgap(15);
            actionsBox.setVgap(15);
            actionsBox.getChildren().addAll(
                createActionButton("💰 Deposit", "#059669", e -> showDepositDialog()),
                createActionButton("💸 Withdraw", "#DC2626", e -> showWithdrawDialog()),
                createActionButton("🔄 Transfer", "#2563EB", e -> showTransferDialog())
            );
            
            mainContent.getChildren().addAll(accountsLabel, accountsFlow, actionsLabel, actionsBox);
        }
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #F9FAFB;");
        root.setCenter(scrollPane);
        
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
    }
    
    private void showDepositDialog() {
        ObservableList<AccountView> myAccounts = accountData.filtered(a -> a.getOwnerId().equals(currentUserId));
        if (myAccounts.isEmpty()) return;
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Deposit Money");
        dialog.setHeaderText("Make a Deposit");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        ComboBox<AccountView> accountCombo = new ComboBox<>(myAccounts);
        accountCombo.setValue(myAccounts.get(0));
        accountCombo.setPrefWidth(250);
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setPrefWidth(250);
        
        grid.add(new Label("Select Account:"), 0, 0);
        grid.add(accountCombo, 1, 0);
        grid.add(new Label("Amount (BWP):"), 0, 1);
        grid.add(amountField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    double amt = Double.parseDouble(amountField.getText());
                    if (amt <= 0) {
                        showAlert("Error", "Amount must be positive!", Alert.AlertType.ERROR);
                        return;
                    }
                    AccountView selectedAccount = accountCombo.getValue();
                    selectedAccount.setBalance(selectedAccount.getBalance() + amt);
                    
                    showAlert("Success", String.format("Deposited BWP %.2f successfully!", amt), Alert.AlertType.INFORMATION);
                    showCustomerDashboard();
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Invalid amount! Please enter a valid number.", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void showWithdrawDialog() {
        ObservableList<AccountView> myAccounts = accountData.filtered(a -> a.getOwnerId().equals(currentUserId));
        if (myAccounts.isEmpty()) return;
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Withdraw Money");
        dialog.setHeaderText("Make a Withdrawal");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        ComboBox<AccountView> accountCombo = new ComboBox<>(myAccounts);
        accountCombo.setValue(myAccounts.get(0));
        accountCombo.setPrefWidth(250);
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setPrefWidth(250);
        
        grid.add(new Label("Select Account:"), 0, 0);
        grid.add(accountCombo, 1, 0);
        grid.add(new Label("Amount (BWP):"), 0, 1);
        grid.add(amountField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    double amt = Double.parseDouble(amountField.getText());
                    AccountView acc = accountCombo.getValue();
                    
                    if (amt <= 0) {
                        showAlert("Error", "Amount must be positive!", Alert.AlertType.ERROR);
                        return;
                    }
                    
                    if (acc.getAccountType().equals("Savings")) {
                        showAlert("Error", "Cannot withdraw from Savings account!\n\nSavings accounts do not allow withdrawals.", Alert.AlertType.ERROR);
                    } else if (amt > acc.getBalance()) {
                        showAlert("Error", String.format("Insufficient balance!\n\nAvailable: BWP %.2f\nRequested: BWP %.2f", 
                                 acc.getBalance(), amt), Alert.AlertType.ERROR);
                    } else {
                        acc.setBalance(acc.getBalance() - amt);
                        
                        showAlert("Success", String.format("Withdrawn BWP %.2f successfully!", amt), Alert.AlertType.INFORMATION);
                        showCustomerDashboard();
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Invalid amount! Please enter a valid number.", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void showTransferDialog() {
        ObservableList<AccountView> myAccounts = accountData.filtered(a -> a.getOwnerId().equals(currentUserId));
        if (myAccounts.size() < 2) {
            showAlert("Error", "You need at least 2 accounts to transfer!\n\nContact the manager to open another account.", Alert.AlertType.ERROR);
            return;
        }
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Transfer Money");
        dialog.setHeaderText("Transfer Between Accounts");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        ComboBox<AccountView> fromCombo = new ComboBox<>(myAccounts);
        fromCombo.setValue(myAccounts.get(0));
        fromCombo.setPrefWidth(250);
        
        ComboBox<AccountView> toCombo = new ComboBox<>(myAccounts);
        toCombo.setValue(myAccounts.get(1));
        toCombo.setPrefWidth(250);
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setPrefWidth(250);
        
        grid.add(new Label("From Account:"), 0, 0);
        grid.add(fromCombo, 1, 0);
        grid.add(new Label("To Account:"), 0, 1);
        grid.add(toCombo, 1, 1);
        grid.add(new Label("Amount (BWP):"), 0, 2);
        grid.add(amountField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    double amt = Double.parseDouble(amountField.getText());
                    AccountView from = fromCombo.getValue();
                    AccountView to = toCombo.getValue();
                    
                    if (amt <= 0) {
                        showAlert("Error", "Amount must be positive!", Alert.AlertType.ERROR);
                        return;
                    }
                    
                    if (from.getAccountNumber().equals(to.getAccountNumber())) {
                        showAlert("Error", "Cannot transfer to the same account!", Alert.AlertType.ERROR);
                    } else if (amt > from.getBalance()) {
                        showAlert("Error", String.format("Insufficient balance!\n\nAvailable: BWP %.2f\nRequested: BWP %.2f", 
                                 from.getBalance(), amt), Alert.AlertType.ERROR);
                    } else {
                        from.setBalance(from.getBalance() - amt);
                        to.setBalance(to.getBalance() + amt);
                        
                        showAlert("Success", String.format("Transferred BWP %.2f successfully!", amt), Alert.AlertType.INFORMATION);
                        showCustomerDashboard();
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Invalid amount! Please enter a valid number.", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private VBox createAccountCard(AccountView account) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        card.setPrefWidth(280);
        card.setMinHeight(140);
        
        Label typeLabel = new Label(account.getAccountType() + " Account");
        typeLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        typeLabel.setTextFill(Color.web("#1E40AF"));
        
        Label numberLabel = new Label(account.getAccountNumber());
        numberLabel.setFont(Font.font("System", 12));
        numberLabel.setTextFill(Color.web("#6B7280"));
        
        Label branchLabel = new Label("Branch: " + account.getBranch());
        branchLabel.setFont(Font.font("System", 11));
        branchLabel.setTextFill(Color.web("#9CA3AF"));
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Label balanceLabel = new Label("BWP " + String.format("%.2f", account.getBalance()));
        balanceLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        balanceLabel.setTextFill(Color.web("#059669"));
        
        card.getChildren().addAll(typeLabel, numberLabel, branchLabel, spacer, balanceLabel);
        return card;
    }
    
    private Button createActionButton(String text, String color, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setPrefHeight(70);
        btn.setPrefWidth(170);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8;");
        btn.setOnAction(handler);
        return btn;
    }
    
    private void showManagerDashboard() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F9FAFB;");
        
        HBox header = createHeader("Manager Portal", currentUsername);
        root.setTop(header);
        
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setFillWidth(true);
        
        // Statistics Overview
        Label statsLabel = new Label("Statistics Overview");
        statsLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        FlowPane statsBox = new FlowPane();
        statsBox.setHgap(20);
        statsBox.setVgap(20);
        statsBox.getChildren().addAll(
            createStatCard("Total Accounts", String.valueOf(accountData.size()), "#2563EB"),
            createStatCard("Total Balance", "BWP " + calculateTotalBalance(), "#059669"),
            createStatCard("Registered Customers", String.valueOf(getUniqueCustomers()), "#7C3AED")
        );
        
        // Registered Customers Table
        Label customersLabel = new Label("Registered Customers");
        customersLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        TableView<User> customerTable = new TableView<>();
        customerTable.setPrefHeight(200);
        customerTable.setStyle("-fx-background-color: white;");
        
        // Get fresh customer list from users
        ObservableList<User> customers = FXCollections.observableArrayList();
        for (User u : users) {
            if (u.getRole().equals("Customer")) {
                customers.add(u);
            }
        }
        customerTable.setItems(customers);
        
        TableColumn<User, String> userIdCol = new TableColumn<>("Customer ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setPrefWidth(120);
        
        TableColumn<User, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        nameCol.setPrefWidth(200);
        
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(150);
        
        customerTable.getColumns().addAll(userIdCol, nameCol, usernameCol);
        
        // Create New Account Form
        Label formLabel = new Label("Create New Account");
        formLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(12);
        formGrid.setPadding(new Insets(20));
        formGrid.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        formGrid.setMaxWidth(700);
        
        TextField accountNumberField = new TextField();
        accountNumberField.setPromptText("e.g., ACC001");
        accountNumberField.setPrefWidth(250);
        
        TextField balanceField = new TextField();
        balanceField.setPromptText("e.g., 1000.00");
        balanceField.setPrefWidth(250);
        
        ComboBox<String> accountTypeCombo = new ComboBox<>();
        accountTypeCombo.getItems().addAll("Savings", "Investment", "Cheque");
        accountTypeCombo.setValue("Savings");
        accountTypeCombo.setPrefWidth(250);
        
        TextField branchField = new TextField();
        branchField.setPromptText("e.g., Gaborone Main");
        branchField.setPrefWidth(250);
        
        ObservableList<String> customerOptions = FXCollections.observableArrayList();
        ComboBox<String> ownerIdCombo = new ComboBox<>(customerOptions);
        ownerIdCombo.setPromptText("Select Customer");
        ownerIdCombo.setPrefWidth(250);
        
        // Populate customer options - refresh from users list
        customerOptions.clear();
        for (User user : users) {
            if (user.getRole().equals("Customer")) {
                customerOptions.add(user.getUserId() + " - " + user.getFullName());
            }
        }
        
        formGrid.add(new Label("Account Number:"), 0, 0);
        formGrid.add(accountNumberField, 1, 0);
        formGrid.add(new Label("Initial Balance:"), 0, 1);
        formGrid.add(balanceField, 1, 1);
        formGrid.add(new Label("Account Type:"), 0, 2);
        formGrid.add(accountTypeCombo, 1, 2);
        formGrid.add(new Label("Branch:"), 0, 3);
        formGrid.add(branchField, 1, 3);
        formGrid.add(new Label("Customer:"), 0, 4);
        formGrid.add(ownerIdCombo, 1, 4);
        
        FlowPane formButtonBox = new FlowPane();
        formButtonBox.setHgap(10);
        formButtonBox.setVgap(10);
        formButtonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button createBtn = createFormButton("Create Account", "#059669");
        Button updateBtn = createFormButton("Update Account", "#F59E0B");
        Button deleteBtn = createFormButton("Delete Account", "#DC2626");
        Button clearBtn = createFormButton("Clear Form", "#6B7280");
        Button refreshBtn = createFormButton("Refresh Customers", "#3B82F6");
        
        formButtonBox.getChildren().addAll(createBtn, updateBtn, deleteBtn, clearBtn, refreshBtn);
        formGrid.add(formButtonBox, 0, 5, 2, 1);
        
        // All Accounts Table
        Label tableLabel = new Label("All Accounts");
        tableLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        TableView<AccountView> table = createAccountTable();
        table.setItems(accountData);
        table.setPrefHeight(280);
        
        // CREATE ACCOUNT BUTTON
        createBtn.setOnAction(e -> {
            String ownerSelection = ownerIdCombo.getValue();
            if (ownerSelection == null || ownerSelection.isEmpty()) {
                showAlert("Error", "Please select a customer!", Alert.AlertType.ERROR);
                return;
            }
            
            String ownerId = ownerSelection.split(" - ")[0];
            
            if (validateAccountForm(accountNumberField.getText(), balanceField.getText(), branchField.getText(), ownerId)) {
                boolean exists = accountData.stream().anyMatch(acc -> acc.getAccountNumber().equals(accountNumberField.getText()));
                if (exists) {
                    showAlert("Error", "Account number already exists!", Alert.AlertType.ERROR);
                    return;
                }
                
                double initialBalance = Double.parseDouble(balanceField.getText());
                String accType = accountTypeCombo.getValue();
                
                if (accType.equals("Investment") && initialBalance < 500.00) {
                    showAlert("Error", "Investment account requires minimum BWP 500.00!", Alert.AlertType.ERROR);
                    return;
                }
                
                accountData.add(new AccountView(
                    accountNumberField.getText(),
                    initialBalance,
                    accType,
                    branchField.getText(),
                    ownerId
                ));
                
                showAlert("Success", "Account created successfully!", Alert.AlertType.INFORMATION);
                clearManagerForm(accountNumberField, balanceField, accountTypeCombo, branchField, ownerIdCombo);
                table.refresh();
                
                // Refresh statistics
                statsBox.getChildren().clear();
                statsBox.getChildren().addAll(
                    createStatCard("Total Accounts", String.valueOf(accountData.size()), "#2563EB"),
                    createStatCard("Total Balance", "BWP " + calculateTotalBalance(), "#059669"),
                    createStatCard("Registered Customers", String.valueOf(getUniqueCustomers()), "#7C3AED")
                );
            }
        });
        
        // UPDATE ACCOUNT BUTTON
        updateBtn.setOnAction(e -> {
            AccountView selectedAccount = table.getSelectionModel().getSelectedItem();
            if (selectedAccount == null) {
                showAlert("Error", "Please select an account from the table to update!", Alert.AlertType.ERROR);
                return;
            }
            
            String ownerSelection = ownerIdCombo.getValue();
            if (ownerSelection == null || ownerSelection.isEmpty()) {
                showAlert("Error", "Please select a customer!", Alert.AlertType.ERROR);
                return;
            }
            
            String ownerId = ownerSelection.split(" - ")[0];
            
            if (validateAccountForm(accountNumberField.getText(), balanceField.getText(), branchField.getText(), ownerId)) {
                // Check if account number changed and if new number already exists
                if (!selectedAccount.getAccountNumber().equals(accountNumberField.getText())) {
                    boolean exists = accountData.stream().anyMatch(acc -> acc.getAccountNumber().equals(accountNumberField.getText()));
                    if (exists) {
                        showAlert("Error", "Account number already exists!", Alert.AlertType.ERROR);
                        return;
                    }
                }
                
                double newBalance = Double.parseDouble(balanceField.getText());
                String accType = accountTypeCombo.getValue();
                
                if (accType.equals("Investment") && newBalance < 500.00) {
                    showAlert("Error", "Investment account requires minimum BWP 500.00!", Alert.AlertType.ERROR);
                    return;
                }
                
                // Update the selected account
                selectedAccount.setAccountNumber(accountNumberField.getText());
                selectedAccount.setBalance(newBalance);
                selectedAccount.setAccountType(accType);
                selectedAccount.setBranch(branchField.getText());
                selectedAccount.setOwnerId(ownerId);
                
                showAlert("Success", "Account updated successfully!", Alert.AlertType.INFORMATION);
                clearManagerForm(accountNumberField, balanceField, accountTypeCombo, branchField, ownerIdCombo);
                table.refresh();
                
                // Refresh statistics
                statsBox.getChildren().clear();
                statsBox.getChildren().addAll(
                    createStatCard("Total Accounts", String.valueOf(accountData.size()), "#2563EB"),
                    createStatCard("Total Balance", "BWP " + calculateTotalBalance(), "#059669"),
                    createStatCard("Registered Customers", String.valueOf(getUniqueCustomers()), "#7C3AED")
                );
            }
        });
        
        // DELETE ACCOUNT BUTTON
        deleteBtn.setOnAction(e -> {
            AccountView selectedAccount = table.getSelectionModel().getSelectedItem();
            if (selectedAccount == null) {
                showAlert("Error", "Please select an account from the table to delete!", Alert.AlertType.ERROR);
                return;
            }
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText("Delete Account");
            confirmAlert.setContentText("Are you sure you want to delete account " + selectedAccount.getAccountNumber() + "?\n\nThis action cannot be undone!");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                accountData.remove(selectedAccount);
                showAlert("Success", "Account deleted successfully!", Alert.AlertType.INFORMATION);
                clearManagerForm(accountNumberField, balanceField, accountTypeCombo, branchField, ownerIdCombo);
                table.refresh();
                
                // Refresh statistics
                statsBox.getChildren().clear();
                statsBox.getChildren().addAll(
                    createStatCard("Total Accounts", String.valueOf(accountData.size()), "#2563EB"),
                    createStatCard("Total Balance", "BWP " + calculateTotalBalance(), "#059669"),
                    createStatCard("Registered Customers", String.valueOf(getUniqueCustomers()), "#7C3AED")
                );
            }
        });
        
        // CLEAR FORM BUTTON
        clearBtn.setOnAction(e -> {
            clearManagerForm(accountNumberField, balanceField, accountTypeCombo, branchField, ownerIdCombo);
            table.getSelectionModel().clearSelection();
        });
        
        // REFRESH CUSTOMERS BUTTON
        refreshBtn.setOnAction(e -> {
            customerOptions.clear();
            ObservableList<User> updatedCustomers = FXCollections.observableArrayList(
                users.filtered(u -> u.getRole().equals("Customer"))
            );
            for (User user : updatedCustomers) {
                customerOptions.add(user.getUserId() + " - " + user.getFullName());
            }
            customers.setAll(updatedCustomers);
            customerTable.refresh();
            showAlert("Success", "Customer list refreshed successfully!", Alert.AlertType.INFORMATION);
        });
        
        // TABLE ROW SELECTION - Auto-fill form when clicking a row
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                accountNumberField.setText(newVal.getAccountNumber());
                balanceField.setText(String.valueOf(newVal.getBalance()));
                accountTypeCombo.setValue(newVal.getAccountType());
                branchField.setText(newVal.getBranch());
                
                String ownerDisplay = newVal.getOwnerId();
                Optional<User> owner = users.stream()
                    .filter(u -> u.getUserId().equals(newVal.getOwnerId()))
                    .findFirst();
                if (owner.isPresent()) {
                    ownerDisplay = owner.get().getUserId() + " - " + owner.get().getFullName();
                }
                
                if (customerOptions.contains(ownerDisplay)) {
                    ownerIdCombo.setValue(ownerDisplay);
                } else {
                    // Refresh customer list and try again
                    customerOptions.clear();
                    ObservableList<User> allCustomers = FXCollections.observableArrayList(
                        users.filtered(u -> u.getRole().equals("Customer"))
                    );
                    for (User user : allCustomers) {
                        customerOptions.add(user.getUserId() + " - " + user.getFullName());
                    }
                    if (customerOptions.contains(ownerDisplay)) {
                        ownerIdCombo.setValue(ownerDisplay);
                    } else {
                        ownerIdCombo.setValue(null);
                    }
                }
            }
        });
        
        mainContent.getChildren().addAll(
            statsLabel, statsBox, 
            customersLabel, customerTable, 
            formLabel, formGrid, 
            tableLabel, table
        );
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #F9FAFB;");
        root.setCenter(scrollPane);
        
        Scene scene = new Scene(root, 1400, 900);
        primaryStage.setScene(scene);
    }
    
    private TableView<AccountView> createAccountTable() {
        TableView<AccountView> table = new TableView<>();
        table.setStyle("-fx-background-color: white;");
        
        TableColumn<AccountView, String> accountNumCol = new TableColumn<>("Account Number");
        accountNumCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accountNumCol.setPrefWidth(150);
        
        TableColumn<AccountView, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        typeCol.setPrefWidth(120);
        
        TableColumn<AccountView, Double> balanceCol = new TableColumn<>("Balance (BWP)");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setPrefWidth(150);
        
        TableColumn<AccountView, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));
        branchCol.setPrefWidth(150);
        
        TableColumn<AccountView, String> ownerCol = new TableColumn<>("Owner ID");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        ownerCol.setPrefWidth(120);
        
        table.getColumns().addAll(accountNumCol, typeCol, balanceCol, branchCol, ownerCol);
        
        return table;
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        card.setMinWidth(220);
        card.setMinHeight(100);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", 12));
        titleLabel.setTextFill(Color.web("#6B7280"));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        valueLabel.setTextFill(Color.web(color));
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private Button createFormButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefHeight(40);
        btn.setMinWidth(140);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
        return btn;
    }
    
    private HBox createHeader(String title, String subtitle) {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setStyle("-fx-background-color: linear-gradient(to right, #4F46E5, #7C3AED);");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(80);
        
        VBox titleBox = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);
        
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("System", 14));
        subtitleLabel.setTextFill(Color.web("#E0E7FF"));
        
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4F46E5; -fx-font-weight: bold; -fx-background-radius: 6;");
        logoutBtn.setPrefHeight(40);
        logoutBtn.setPrefWidth(100);
        logoutBtn.setOnAction(e -> showLoginView());
        
        header.getChildren().addAll(titleBox, spacer, logoutBtn);
        return header;
    }
    
    private void clearManagerForm(TextField accountNumber, TextField balance, 
                                  ComboBox<String> accountType, TextField branch, ComboBox<String> ownerId) {
        accountNumber.clear();
        balance.clear();
        accountType.setValue("Savings");
        branch.clear();
        ownerId.setValue(null);
    }
    
    private boolean validateAccountForm(String accountNumber, String balance, String branch, String ownerId) {
        if (accountNumber.isEmpty() || balance.isEmpty() || branch.isEmpty() || ownerId.isEmpty()) {
            showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
            return false;
        }
        try {
            double bal = Double.parseDouble(balance);
            if (bal < 0) {
                showAlert("Error", "Balance cannot be negative!", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid balance! Please enter a valid number.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private String calculateTotalBalance() {
        if (accountData.isEmpty()) return "0.00";
        double total = accountData.stream().mapToDouble(AccountView::getBalance).sum();
        return String.format("%.2f", total);
    }
    
    private long getUniqueCustomers() {
        return users.stream().filter(u -> u.getRole().equals("Customer")).count();
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

// ============================================================================
// MODEL CLASSES - Must be public top-level classes for JavaFX PropertyValueFactory
// ============================================================================

class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    private String role;
    private String userId;
    private String fullName;
    
    public User(String username, String password, String role, String userId, String fullName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.userId = userId;
        this.fullName = fullName;
    }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
}

class AccountView implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String accountNumber;
    private double balance;
    private String accountType;
    private String branch;
    private String ownerId;
    
    public AccountView(String accountNumber, double balance, String accountType, 
                      String branch, String ownerId) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.branch = branch;
        this.ownerId = ownerId;
    }
    
    @Override
    public String toString() {
        return accountNumber + " - " + accountType;
    }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
}

class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String type;
    private double amount;
    private String accountNumber;
    private String customerId;
    private String timestamp;
    
    public Transaction(String type, double amount, String accountNumber, 
                      String customerId, String timestamp) {
        this.type = type;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.timestamp = timestamp;
    }
    
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getAccountNumber() { return accountNumber; }
    public String getCustomerId() { return customerId; }
    public String getTimestamp() { return timestamp; }
}