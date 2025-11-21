package banking.view;

import banking.controller.AccountController;
import banking.controller.CustomerController;
import banking.model.Account;
import banking.model.Customer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class AccountManagementView {
    private VBox contentArea;
    private AccountController accountController;
    private CustomerController customerController;
    private TableView<Account> accountTable;

    public AccountManagementView(VBox contentArea, AccountController accountController, CustomerController customerController) {
        this.contentArea = contentArea;
        this.accountController = accountController;
        this.customerController = customerController;
    }

    public void show() {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("Account Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Button addButton = new Button("+ Open New Account");
        addButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        addButton.setOnAction(e -> showOpenAccountDialog());

        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerBox.getChildren().addAll(titleLabel, spacer, addButton);

        accountTable = new TableView<>();
        accountTable.setStyle("-fx-background-color: white;");

        TableColumn<Account, String> numberCol = new TableColumn<>("Account Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        numberCol.setPrefWidth(150);

        TableColumn<Account, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        typeCol.setPrefWidth(150);

        TableColumn<Account, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(data -> {
            Customer customer = data.getValue().getCustomer();
            return new javafx.beans.property.SimpleStringProperty(customer.getFullName());
        });
        customerCol.setPrefWidth(200);

        TableColumn<Account, Double> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setPrefWidth(120);
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

        TableColumn<Account, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));
        branchCol.setPrefWidth(120);

        TableColumn<Account, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(250);
        actionsCol.setCellFactory(param -> new TableCell<Account, Void>() {
            private final Button depositButton = new Button("Deposit");
            private final Button withdrawButton = new Button("Withdraw");
            private final Button historyButton = new Button("History");

            {
                depositButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                withdrawButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                historyButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

                depositButton.setOnAction(e -> {
                    Account account = getTableView().getItems().get(getIndex());
                    showDepositDialog(account);
                });

                withdrawButton.setOnAction(e -> {
                    Account account = getTableView().getItems().get(getIndex());
                    showWithdrawDialog(account);
                });

                historyButton.setOnAction(e -> {
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
                    HBox buttons = new HBox(5, depositButton, withdrawButton, historyButton);
                    setGraphic(buttons);
                }
            }
        });

        accountTable.getColumns().addAll(numberCol, typeCol, customerCol, balanceCol, branchCol, actionsCol);
        loadAccounts();

        contentArea.getChildren().addAll(headerBox, new Separator(), accountTable);
        VBox.setVgrow(accountTable, Priority.ALWAYS);
    }

    private void loadAccounts() {
        List<Account> accounts = accountController.getAllAccounts();
        accountTable.setItems(FXCollections.observableArrayList(accounts));
    }

    private void showOpenAccountDialog() {
        Dialog<Account> dialog = new Dialog<>();
        dialog.setTitle("Open New Account");
        dialog.setHeaderText("Enter account details");

        ButtonType openButtonType = new ButtonType("Open Account", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(openButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<Customer> customerCombo = new ComboBox<>();
        customerCombo.setItems(FXCollections.observableArrayList(customerController.getAllCustomers()));
        customerCombo.setPromptText("Select Customer");
        customerCombo.setCellFactory(lv -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                setText(empty ? null : customer.getFullName());
            }
        });
        customerCombo.setButtonCell(new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                setText(empty ? null : customer.getFullName());
            }
        });

        ComboBox<String> accountTypeCombo = new ComboBox<>();
        accountTypeCombo.setItems(FXCollections.observableArrayList("Savings", "Investment", "Cheque"));
        accountTypeCombo.setPromptText("Select Account Type");

        TextField branchField = new TextField();
        branchField.setPromptText("Branch Name");

        TextField initialDepositField = new TextField("0");
        initialDepositField.setPromptText("Initial Deposit");

        TextField companyNameField = new TextField();
        companyNameField.setPromptText("Company Name");
        companyNameField.setVisible(false);

        TextField companyAddressField = new TextField();
        companyAddressField.setPromptText("Company Address");
        companyAddressField.setVisible(false);

        accountTypeCombo.setOnAction(e -> {
            boolean isCheque = "Cheque".equals(accountTypeCombo.getValue());
            companyNameField.setVisible(isCheque);
            companyAddressField.setVisible(isCheque);
        });

        grid.add(new Label("Customer:"), 0, 0);
        grid.add(customerCombo, 1, 0);
        grid.add(new Label("Account Type:"), 0, 1);
        grid.add(accountTypeCombo, 1, 1);
        grid.add(new Label("Branch:"), 0, 2);
        grid.add(branchField, 1, 2);
        grid.add(new Label("Initial Deposit:"), 0, 3);
        grid.add(initialDepositField, 1, 3);
        grid.add(new Label("Company Name:"), 0, 4);
        grid.add(companyNameField, 1, 4);
        grid.add(new Label("Company Address:"), 0, 5);
        grid.add(companyAddressField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == openButtonType) {
                try {
                    Customer customer = customerCombo.getValue();
                    String accountType = accountTypeCombo.getValue();
                    String branch = branchField.getText();
                    double initialDeposit = Double.parseDouble(initialDepositField.getText());
                    String companyName = companyNameField.getText();
                    String companyAddress = companyAddressField.getText();

                    Account account = accountController.openAccount(
                            customer.getCustomerId(),
                            accountType,
                            branch,
                            companyName,
                            companyAddress,
                            initialDeposit
                    );

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setContentText("Account opened successfully!\nAccount Number: " + account.getAccountNumber());
                    success.showAndWait();

                    return account;
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error: " + e.getMessage());
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
        loadAccounts();
    }

    private void showDepositDialog(Account account) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deposit");
        dialog.setHeaderText("Deposit to " + account.getAccountNumber());
        dialog.setContentText("Amount (BWP):");

        dialog.showAndWait().ifPresent(amount -> {
            try {
                double depositAmount = Double.parseDouble(amount);
                if (accountController.deposit(account.getAccountNumber(), depositAmount)) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setContentText("Deposit successful!");
                    success.showAndWait();
                    loadAccounts();
                }
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

        dialog.showAndWait().ifPresent(amount -> {
            try {
                double withdrawAmount = Double.parseDouble(amount);
                if (accountController.withdraw(account.getAccountNumber(), withdrawAmount)) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setContentText("Withdrawal successful!");
                    success.showAndWait();
                    loadAccounts();
                }
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
}