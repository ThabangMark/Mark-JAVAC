package banking.view;

import banking.controller.AccountController;
import banking.model.Account;
import banking.model.Transaction;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionHistoryDialog extends Dialog<Void> {
    private Account account;
    private AccountController accountController;

    public TransactionHistoryDialog(Account account, AccountController accountController) {
        this.account = account;
        this.accountController = accountController;

        setTitle("Transaction History");
        setHeaderText("Account: " + account.getAccountNumber() + " - " + account.getAccountType());

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(800);
        content.setPrefHeight(500);

        Label accountInfoLabel = new Label("Customer: " + account.getCustomer().getFullName());
        accountInfoLabel.setFont(Font.font("Arial", 14));

        Label balanceLabel = new Label("Current Balance: BWP " + String.format("%.2f", account.getBalance()));
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<Transaction> transactionTable = new TableView<>();

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        idCol.setPrefWidth(50);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);
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
        balanceCol.setPrefWidth(130);
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
        dateCol.setPrefWidth(150);
        dateCol.setCellFactory(col -> new TableCell<Transaction, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime timestamp, boolean empty) {
                super.updateItem(timestamp, empty);
                if (empty || timestamp == null) {
                    setText(null);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    setText(timestamp.format(formatter));
                }
            }
        });

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);

        transactionTable.getColumns().addAll(idCol, typeCol, amountCol, balanceCol, dateCol, descCol);

        List<Transaction> transactions = accountController.getAccountTransactions(account.getAccountNumber());
        transactionTable.setItems(FXCollections.observableArrayList(transactions));

        if (transactions.isEmpty()) {
            Label noTransactionsLabel = new Label("No transactions found for this account");
            content.getChildren().addAll(accountInfoLabel, balanceLabel, new Separator(), noTransactionsLabel);
        } else {
            content.getChildren().addAll(accountInfoLabel, balanceLabel, new Separator(), transactionTable);
        }

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }
}