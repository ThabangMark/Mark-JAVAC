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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class CustomerManagementView {
    private VBox contentArea;
    private CustomerController customerController;
    private AccountController accountController;
    private TableView<Customer> customerTable;

    public CustomerManagementView(VBox contentArea, CustomerController customerController, AccountController accountController) {
        this.contentArea = contentArea;
        this.customerController = customerController;
        this.accountController = accountController;
    }

    public void show() {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("Customer Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Button addButton = new Button("+ Add New Customer");
        addButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        addButton.setOnAction(e -> showAddCustomerDialog());

        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerBox.getChildren().addAll(titleLabel, spacer, addButton);

        customerTable = new TableView<>();
        customerTable.setStyle("-fx-background-color: white;");

        TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        idCol.setPrefWidth(80);

        TableColumn<Customer, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setPrefWidth(150);

        TableColumn<Customer, String> surnameCol = new TableColumn<>("Surname");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        surnameCol.setPrefWidth(150);

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(250);

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setPrefWidth(150);

        TableColumn<Customer, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(param -> new TableCell<Customer, Void>() {
            private final Button viewButton = new Button("View Accounts");
            private final Button deleteButton = new Button("Delete");

            {
                viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                viewButton.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    showCustomerAccounts(customer);
                });

                deleteButton.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    deleteCustomer(customer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        customerTable.getColumns().addAll(idCol, firstNameCol, surnameCol, emailCol, phoneCol, actionsCol);
        loadCustomers();

        contentArea.getChildren().addAll(headerBox, new Separator(), customerTable);
        VBox.setVgrow(customerTable, Priority.ALWAYS);
    }

    private void loadCustomers() {
        List<Customer> customers = customerController.getAllCustomers();
        customerTable.setItems(FXCollections.observableArrayList(customers));
    }

    private void showAddCustomerDialog() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Add New Customer");
        dialog.setHeaderText("Enter customer details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Surname:"), 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    customerController.registerCustomer(
                            firstNameField.getText(),
                            surnameField.getText(),
                            addressField.getText(),
                            phoneField.getText(),
                            emailField.getText()
                    );
                    return null;
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
        loadCustomers();
    }

    private void showCustomerAccounts(Customer customer) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Customer Accounts");
        dialog.setHeaderText(customer.getFullName() + "'s Accounts");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        List<Account> accounts = accountController.getCustomerAccounts(customer.getCustomerId());

        if (accounts.isEmpty()) {
            Label noAccountsLabel = new Label("No accounts found for this customer");
            content.getChildren().add(noAccountsLabel);
        } else {
            for (Account account : accounts) {
                VBox accountBox = new VBox(5);
                accountBox.setPadding(new Insets(10));
                accountBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

                Label typeLabel = new Label(account.getAccountType());
                typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

                Label numberLabel = new Label("Account #: " + account.getAccountNumber());
                Label balanceLabel = new Label("Balance: BWP " + String.format("%.2f", account.getBalance()));
                Label branchLabel = new Label("Branch: " + account.getBranch());

                accountBox.getChildren().addAll(typeLabel, numberLabel, balanceLabel, branchLabel);
                content.getChildren().add(accountBox);
            }
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void deleteCustomer(Customer customer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Customer");
        alert.setHeaderText("Delete " + customer.getFullName() + "?");
        alert.setContentText("This action cannot be undone. All associated accounts will also be deleted.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (customerController.deleteCustomer(customer.getCustomerId())) {
                    loadCustomers();
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setContentText("Customer deleted successfully");
                    success.showAndWait();
                }
            }
        });
    }
}