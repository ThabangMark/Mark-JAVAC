package banking.controller;

import banking.dao.AccountDAO;
import banking.dao.CustomerDAO;
import banking.dao.TransactionDAO;
import banking.model.*;
import java.util.List;
import java.util.Random;

public class AccountController {
    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;
    private TransactionDAO transactionDAO;

    public AccountController() {
        this.accountDAO = new AccountDAO();
        this.customerDAO = new CustomerDAO();
        this.transactionDAO = new TransactionDAO();
    }

    public Account openAccount(int customerId, String accountType, String branch, String companyName, String companyAddress, double initialDeposit) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        String accountNumber = generateAccountNumber();
        Account account;

        switch (accountType) {
            case "Savings":
                account = new SavingsAccount(accountNumber, branch, customer);
                if (initialDeposit > 0) {
                    account.deposit(initialDeposit);
                }
                break;

            case "Investment":
                if (initialDeposit < InvestmentAccount.getMinimumOpeningDeposit()) {
                    throw new IllegalArgumentException("Investment account requires minimum deposit of BWP " + InvestmentAccount.getMinimumOpeningDeposit());
                }
                account = new InvestmentAccount(accountNumber, branch, customer, initialDeposit);
                break;

            case "Cheque":
                if (companyName == null || companyName.trim().isEmpty()) {
                    throw new IllegalArgumentException("Company name is required for Cheque Account");
                }
                if (companyAddress == null || companyAddress.trim().isEmpty()) {
                    throw new IllegalArgumentException("Company address is required for Cheque Account");
                }
                account = new ChequeAccount(accountNumber, branch, customer, companyName, companyAddress);
                if (initialDeposit > 0) {
                    account.deposit(initialDeposit);
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid account type");
        }

        if (accountDAO.createAccount(account)) {
            if (initialDeposit > 0) {
                Transaction transaction = new Transaction(
                        accountNumber,
                        Transaction.TransactionType.DEPOSIT,
                        initialDeposit,
                        account.getBalance(),
                        "Initial deposit"
                );
                transactionDAO.createTransaction(transaction);
            }
            return account;
        }

        return null;
    }

    public boolean deposit(String accountNumber, double amount) {
        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        account.deposit(amount);
        boolean success = accountDAO.updateAccountBalance(accountNumber, account.getBalance());

        if (success) {
            Transaction transaction = new Transaction(
                    accountNumber,
                    Transaction.TransactionType.DEPOSIT,
                    amount,
                    account.getBalance(),
                    "Deposit"
            );
            transactionDAO.createTransaction(transaction);
        }

        return success;
    }

    public boolean withdraw(String accountNumber, double amount) {
        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        account.withdraw(amount);
        boolean success = accountDAO.updateAccountBalance(accountNumber, account.getBalance());

        if (success) {
            Transaction transaction = new Transaction(
                    accountNumber,
                    Transaction.TransactionType.WITHDRAWAL,
                    amount,
                    account.getBalance(),
                    "Withdrawal"
            );
            transactionDAO.createTransaction(transaction);
        }

        return success;
    }

    public boolean payInterest(String accountNumber) {
        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        double interest = account.calculateInterest();
        if (interest <= 0) {
            return false;
        }

        account.applyInterest();
        boolean success = accountDAO.updateAccountBalance(accountNumber, account.getBalance());

        if (success) {
            Transaction transaction = new Transaction(
                    accountNumber,
                    Transaction.TransactionType.INTEREST,
                    interest,
                    account.getBalance(),
                    "Monthly interest payment"
            );
            transactionDAO.createTransaction(transaction);
        }

        return success;
    }

    public void payInterestToAllAccounts() {
        List<Account> accounts = accountDAO.getAllAccounts();
        for (Account account : accounts) {
            if (account.isActive() && account.calculateInterest() > 0) {
                try {
                    payInterest(account.getAccountNumber());
                } catch (Exception e) {
                    System.err.println("Error paying interest to account " + account.getAccountNumber() + ": " + e.getMessage());
                }
            }
        }
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountDAO.getAccountByNumber(accountNumber);
    }

    public List<Account> getCustomerAccounts(int customerId) {
        return accountDAO.getAccountsByCustomerId(customerId);
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    public List<Transaction> getAccountTransactions(String accountNumber) {
        return transactionDAO.getTransactionsByAccountNumber(accountNumber);
    }

    private String generateAccountNumber() {
        Random random = new Random();
        long number = 1000000000L + (long) (random.nextDouble() * 9000000000L);
        return String.valueOf(number);
    }
}