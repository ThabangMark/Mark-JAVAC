package banking.view;

import banking.controller.AccountController;
import banking.controller.AuthController;
import banking.controller.CustomerController;
import banking.dao.CustomerDAO;
import banking.model.Account;
import banking.model.Customer;
import banking.model.Transaction;
import banking.model.User;
import javafx.collections.FXCollections;
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

import java.util.List;
import java.util.Optional;

public class CustomerDashboardView {
    private Stage stage;
    private AuthController authController;
    private AccountController accountController;
    private CustomerController customerController;
    private User currentUser;
    private Customer customer;
    private VBox contentArea;

    public CustomerDashboardView(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;
        this.accountController = new AccountController();
        this.customerController = new CustomerController();
        this.currentUser = authController.getCurrentUser();

        // Load customer data
        CustomerDAO customerDAO = new CustomerDAO();
        this.customer = customerDAO.getCustomerById(currentUser.getCustomerId());
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Top Navigation Bar
        HBox navbar = createNavBar();
        root.setTop(navbar);

        // Content Area
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: #f5f5f5;");

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        root.setCenter(scrollPane);

        // Show home by default
        showHome();

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Banking System - Customer Portal");
        stage.show();
    }

    private HBox createNavBar() {
        HBox navbar = new HBox(20);
        navbar.setPadding(new Insets(15, 30, 15, 30));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%);");

        Label brandLabel = new Label("🏦 Banking Portal");
        brandLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        brandLabel.setTextFill(Color.WHITE);

        Button homeBtn = createNavButton("Home");
        Button accountsBtn = createNavButton("My Accounts");
        Button transferBtn = createNavButton("Transfer");
        Button profileBtn = createNavButton("Profile");

        homeBtn.setOnAction(e -> showHome());
        accountsBtn.setOnAction(e -> showAccounts());
        transferBtn.setOnAction(e -> showTransfer());
        profileBtn.setOnAction(e -> showProfile());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("👤 " + customer.getFullName());
        userLabel.setFont(Font.font("Arial", 14));
        userLabel.setTextFill(Color.WHITE);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> {
            authController.logout();
            LoginView loginView = new LoginView(stage);
            loginView.show();
        });

        navbar.getChildren().addAll(brandLabel, homeBtn, accountsBtn, transferBtn, profileBtn, spacer, userLabel, logoutBtn);
        return navbar;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;"));
        return btn;
    }

    private void showHome() {
        contentArea.getChildren().clear();

        Label welcomeLabel = new Label("Welcome, " + customer.getFirstName() + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));

        // Account Summary Cards
        List<Account> accounts = accountController.getCustomerAccounts(customer.getCustomerId());

        double totalBalance = accounts.stream().mapToDouble(Account::getBalance).sum();

        HBox summaryBox = new HBox(20);
        summaryBox.setAlignment(Pos.CENTER);

        VBox accountsCard = createSummaryCard("Total Accounts", String.valueOf(accounts.size()), "#3498db");
        VBox balanceCard = createSummaryCard("Total Balance", "BWP " + String.format("%.2f", totalBalance), "#2ecc71");

        summaryBox.getChildren().addAll(accountsCard, balanceCard);

        // Recent Accounts
        Label accountsLabel = new Label("Your Accounts");
        accountsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        VBox accountsList = new VBox(10);
        for (Account account : accounts) {
            accountsList.getChildren().add(createAccountCard(account));
        }

        contentArea.getChildren().addAll(welcomeLabel, summaryBox, accountsLabel, accountsList);
    }

    private VBox createSummaryCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(250);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 14));
        titleLabel.setTextFill(Color.GRAY);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private HBox createAccountCard(Account account) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        VBox infoBox = new VBox(5);
        Label accountNumberLabel = new Label(account.getAccountNumber());
        accountNumberLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label accountTypeLabel = new Label(account.getAccountType());
        accountTypeLabel.setFont(Font.font("Arial", 14));
        accountTypeLabel.setTextFill(Color.GRAY);

        infoBox.getChildren().addAll(accountNumberLabel, accountTypeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label balanceLabel = new Label("BWP " + String.format("%.2f", account.getBalance()));
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        balanceLabel.setStyle("-fx-text-fill: #2ecc71;");

        Button depositBtn = new Button("Deposit");
        depositBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        depositBtn.setOnAction(e -> showDepositDialog(account));

        Button withdrawBtn = new Button("Withdraw");
        withdrawBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        withdrawBtn.setOnAction(e -> showWithdrawDialog(account));

        Button historyBtn = new Button("History");
        historyBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        historyBtn.setOnAction(e -> showTransactionHistory(account));

        HBox buttonBox = new HBox(10, depositBtn, withdrawBtn, historyBtn);

        card.getChildren().addAll(infoBox, spacer, balanceLabel, buttonBox);
        return card;
    }

    private void showAccounts() {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("My Accounts");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        List<Account> accounts = accountController.getCustomerAccounts(customer.getCustomerId());

        TableView<Account> accountTable = new TableView<>();
        accountTable.setStyle("-fx-background-color: white;");

        TableColumn<Account, String> numberCol = new TableColumn<>("Account Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        numberCol.setPrefWidth(200);

        TableColumn<Account, String> typeCol = new TableColumn<>("Account Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        typeCol.setPrefWidth(150);

        TableColumn<Account, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));
        branchCol.setPrefWidth(150);

        TableColumn<Account, Double> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setPrefWidth(150);
        balanceCol.setCellFactory(col -> new TableCell<Account, Double>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                if (empty || balance == null) {
                    setText(null);
                } else {
                    setText("BWP " + String.format("%.2f", balance));
                }
            }
        });

        TableColumn<Account, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(300);
        actionsCol.setCellFactory(param -> new TableCell<Account, Void>() {
            private final Button depositBtn = new Button("Deposit");
            private final Button withdrawBtn = new Button("Withdraw");
            private final Button historyBtn = new Button("History");

            {
                depositBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                withdrawBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                historyBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

                depositBtn.setOnAction(e -> {
                    Account account = getTableView().getItems().get(getIndex());
                    showDepositDialog(account);
                });

                withdrawBtn.setOnAction(e -> {
                    Account account = getTableView().getItems().get(getIndex());
                    showWithdrawDialog(account);
                });

                historyBtn.setOnAction(e -> {
                    Account account = getTableView().getItems().get(getIndex());
                    showTransactionHistory(account);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, depositBtn, withdrawBtn, historyBtn);
                    setGraphic(buttons);
                }
            }
        });

        accountTable.getColumns().addAll(numberCol, typeCol, branchCol, balanceCol, actionsCol);
        accountTable.setItems(FXCollections.observableArrayList(accounts));

        contentArea.getChildren().addAll(titleLabel, accountTable);
        VBox.setVgrow(accountTable, Priority.ALWAYS);
    }

    private void showTransfer() {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("Transfer Money");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(30));
        formBox.setMaxWidth(500);
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        List<Account> accounts = accountController.getCustomerAccounts(customer.getCustomerId());

        ComboBox<Account> fromAccountCombo = new ComboBox<>();
        fromAccountCombo.setItems(FXCollections.observableArrayList(accounts));
        fromAccountCombo.setPromptText("From Account");
        fromAccountCombo.setPrefWidth(400);
        fromAccountCombo.setCellFactory(lv -> new ListCell<Account>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                if (empty || account == null) {
                    setText(null);
                } else {
                    setText(account.getAccountNumber() + " - BWP " + String.format("%.2f", account.getBalance()));
                }
            }
        });
        fromAccountCombo.setButtonCell(new ListCell<Account>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                if (empty || account == null) {
                    setText(null);
                } else {
                    setText(account.getAccountNumber() + " - BWP " + String.format("%.2f", account.getBalance()));
                }
            }
        });

        ComboBox<Account> toAccountCombo = new ComboBox<>();
        toAccountCombo.setItems(FXCollections.observableArrayList(accounts));
        toAccountCombo.setPromptText("To Account");
        toAccountCombo.setPrefWidth(400);
        toAccountCombo.setCellFactory(lv -> new ListCell<Account>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                if (empty || account == null) {
                    setText(null);
                } else {
                    setText(account.getAccountNumber() + " - " + account.getAccountType());
                }
            }
        });
        toAccountCombo.setButtonCell(new ListCell<Account>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                if (empty || account == null) {
                    setText(null);
                } else {
                    setText(account.getAccountNumber() + " - " + account.getAccountType());
                }
            }
        });

        TextField amountField = new TextField();
        amountField.setPromptText("Amount (BWP)");
        amountField.setPrefHeight(40);

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description (optional)");
        descriptionArea.setPrefRowCount(3);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        Button transferBtn = new Button("Transfer Money");
        transferBtn.setPrefWidth(200);
        transferBtn.setPrefHeight(45);
        transferBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-weight: bold;");

        transferBtn.setOnAction(e -> {
            errorLabel.setVisible(false);

            try {
                Account fromAccount = fromAccountCombo.getValue();
                Account toAccount = toAccountCombo.getValue();
                String amountStr = amountField.getText().trim();

                if (fromAccount == null || toAccount == null || amountStr.isEmpty()) {
                    errorLabel.setText("Please fill all required fields");
                    errorLabel.setVisible(true);
                    return;
                }

                if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
                    errorLabel.setText("Cannot transfer to the same account");
                    errorLabel.setVisible(true);
                    return;
                }

                double amount = Double.parseDouble(amountStr);

                if (amount <= 0) {
                    errorLabel.setText("Amount must be greater than 0");
                    errorLabel.setVisible(true);
                    return;
                }

                if (fromAccount.getBalance() < amount) {
                    errorLabel.setText("Insufficient balance");
                    errorLabel.setVisible(true);
                    return;
                }

                // Perform transfer
                if (accountController.withdraw(fromAccount.getAccountNumber(), amount)) {
                    if (accountController.deposit(toAccount.getAccountNumber(), amount)) {
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Success");
                        success.setHeaderText("Transfer Successful!");
                        success.setContentText(String.format("BWP %.2f transferred from %s to %s",
                                amount, fromAccount.getAccountNumber(), toAccount.getAccountNumber()));
                        success.showAndWait();

                        // Clear form
                        fromAccountCombo.setValue(null);
                        toAccountCombo.setValue(null);
                        amountField.clear();
                        descriptionArea.clear();

                        showTransfer(); // Refresh
                    }
                }

            } catch (NumberFormatException ex) {
                errorLabel.setText("Invalid amount");
                errorLabel.setVisible(true);
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        formBox.getChildren().addAll(
                new Label("From Account:"), fromAccountCombo,
                new Label("To Account:"), toAccountCombo,
                new Label("Amount:"), amountField,
                new Label("Description:"), descriptionArea,
                errorLabel,
                transferBtn
        );

        contentArea.getChildren().addAll(titleLabel, formBox);
    }

    private void showProfile() {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("My Profile");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        VBox profileBox = new VBox(15);
        profileBox.setPadding(new Insets(30));
        profileBox.setMaxWidth(500);
        profileBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField firstNameField = new TextField(customer.getFirstName());
        TextField surnameField = new TextField(customer.getSurname());
        TextField addressField = new TextField(customer.getAddress());
        TextField phoneField = new TextField(customer.getPhoneNumber());
        TextField emailField = new TextField(customer.getEmail());

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Surname:"), 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        Button updateBtn = new Button("Update Profile");
        updateBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Button changePasswordBtn = new Button("Change Password");
        changePasswordBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        HBox buttonBox = new HBox(10, updateBtn, changePasswordBtn);

        updateBtn.setOnAction(e -> {
            try {
                customer.setFirstName(firstNameField.getText().trim());
                customer.setSurname(surnameField.getText().trim());
                customer.setAddress(addressField.getText().trim());
                customer.setPhoneNumber(phoneField.getText().trim());
                customer.setEmail(emailField.getText().trim());

                if (customerController.updateCustomer(customer)) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setContentText("Profile updated successfully!");
                    success.showAndWait();
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        changePasswordBtn.setOnAction(e -> showChangePasswordDialog());

        profileBox.getChildren().addAll(grid, errorLabel, buttonBox);
        contentArea.getChildren().addAll(titleLabel, profileBox);
    }

    private void showDepositDialog(Account account) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deposit");
        dialog.setHeaderText("Deposit to " + account.getAccountNumber());
        dialog.setContentText("Amount (BWP):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            try {
                double depositAmount = Double.parseDouble(amount);
                if (depositAmount <= 0) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Amount must be greater than 0");
                    error.showAndWait();
                    return;
                }
                if (accountController.deposit(account.getAccountNumber(), depositAmount)) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setContentText("Deposit successful!");
                    success.showAndWait();
                    showHome(); // Refresh
                }
            } catch (NumberFormatException e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText("Invalid amount");
                error.showAndWait();
            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText("Error: " + e.getMessage());
                error.showAndWait();
            }
        });
    }

    private void showWithdrawDialog(Account account) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Withdraw");
        dialog.setHeaderText("Withdraw from " + account.getAccountNumber());
        dialog.setContentText("Amount (BWP):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            try {
                double withdrawAmount = Double.parseDouble(amount);
                if (withdrawAmount <= 0) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Amount must be greater than 0");
                    error.showAndWait();
                    return;
                }
                if (withdrawAmount > account.getBalance()) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Insufficient balance!");
                    error.showAndWait();
                    return;
                }
                if (accountController.withdraw(account.getAccountNumber(), withdrawAmount)) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setContentText("Withdrawal successful!");
                    success.showAndWait();
                    showHome(); // Refresh
                }
            } catch (NumberFormatException e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText("Invalid amount");
                error.showAndWait();
            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText("Error: " + e.getMessage());
                error.showAndWait();
            }
        });
    }

    private void showTransactionHistory(Account account) {
        TransactionHistoryDialog historyDialog = new TransactionHistoryDialog(account, accountController);
        historyDialog.showAndWait();
    }

    private void showChangePasswordDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your new password");

        ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                String currentPassword = currentPasswordField.getText();
                String newPassword = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                if (!currentPassword.equals(currentUser.getPassword())) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Current password is incorrect");
                    error.showAndWait();
                    return null;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Passwords do not match");
                    error.showAndWait();
                    return null;
                }

                if (newPassword.length() < 6) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Password must be at least 6 characters");
                    error.showAndWait();
                    return null;
                }

                if (authController.changePassword(currentUser.getUserId(), newPassword)) {
                    currentUser.setPassword(newPassword);
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setContentText("Password changed successfully!");
                    success.showAndWait();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Error changing password");
                    error.showAndWait();
                }

                return newPassword;
            }
            return null;
        });

        dialog.showAndWait();
    }
}