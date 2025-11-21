package banking.view;

import banking.controller.AccountController;
import banking.model.Account;
import banking.model.Transaction;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionView {
    private VBox contentArea;
    private AccountController accountController;
    private TableView<Transaction> transactionTable;
    private ComboBox<String> accountFilter;

    public TransactionView(VBox contentArea, AccountController accountController) {
        this.contentArea = contentArea;
        this.accountController = accountController;
    }

    public void show() {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("Recent Transactions");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        Label filterLabel = new Label("Filter by Account:");
        accountFilter = new ComboBox<>();
        accountFilter.setPromptText("All Accounts");
        accountFilter.setPrefWidth(250);

        List<Account> accounts = accountController.getAllAccounts();
        accountFilter.getItems().add("All Accounts");
        for (Account account : accounts) {
            accountFilter.getItems().add(account.getAccountNumber() + " - " + account.getAccountType());
        }
        accountFilter.setValue("All Accounts");
        accountFilter.setOnAction(e -> loadTransactions());

        filterBox.getChildren().addAll(filterLabel, accountFilter);

        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerBox.getChildren().addAll(titleLabel, spacer, filterBox);

        transactionTable = new TableView<>();
        transactionTable.setStyle("-fx-background-color: white;");

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        idCol.setPrefWidth(80);

        TableColumn<Transaction, String> accountCol = new TableColumn<>("Account Number");
        accountCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accountCol.setPrefWidth(150);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(120);
        typeCol.setCellFactory(col -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(type);
                    switch (type) {
                        case "DEPOSIT":
                            setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                            break;
                        case "WITHDRAWAL":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        case "INTEREST":
                            setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(120);
        amountCol.setCellFactory(col -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText("BWP " + String.format("%.2f", amount));
                }
            }
        });

        TableColumn<Transaction, Double> balanceCol = new TableColumn<>("Balance After");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balanceAfter"));
        balanceCol.setPrefWidth(120);
        balanceCol.setCellFactory(col -> new TableCell<Transaction, Double>() {
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

        TableColumn<Transaction, LocalDateTime> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        dateCol.setPrefWidth(180);
        dateCol.setCellFactory(col -> new TableCell<Transaction, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime timestamp, boolean empty) {
                super.updateItem(timestamp, empty);
                if (empty || timestamp == null) {
                    setText(null);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    setText(timestamp.format(formatter));
                }
            }
        });

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);

        transactionTable.getColumns().addAll(idCol, accountCol, typeCol, amountCol, balanceCol, dateCol, descCol);
        loadTransactions();

        contentArea.getChildren().addAll(headerBox, new Separator(), transactionTable);
        VBox.setVgrow(transactionTable, Priority.ALWAYS);
    }

    private void loadTransactions() {
        String selectedFilter = accountFilter.getValue();

        if (selectedFilter == null || selectedFilter.equals("All Accounts")) {
            List<Account> accounts = accountController.getAllAccounts();
            List<Transaction> allTransactions = new ArrayList<>();
            for (Account account : accounts) {
                allTransactions.addAll(accountController.getAccountTransactions(account.getAccountNumber()));
            }
            allTransactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));
            transactionTable.setItems(FXCollections.observableArrayList(allTransactions));
        } else {
            String accountNumber = selectedFilter.split(" - ")[0];
            List<Transaction> transactions = accountController.getAccountTransactions(accountNumber);
            transactionTable.setItems(FXCollections.observableArrayList(transactions));
        }
    }
}