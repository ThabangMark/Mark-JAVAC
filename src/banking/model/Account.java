package banking.model;

import java.time.LocalDateTime;

public abstract class Account implements IAccount {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected LocalDateTime dateOpened;
    protected Customer customer;
    protected boolean isActive;

    public Account() {
        this.dateOpened = LocalDateTime.now();
        this.isActive = true;
    }

    public Account(String accountNumber, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.customer = customer;
        this.balance = 0.0;
        this.dateOpened = LocalDateTime.now();
        this.isActive = true;
    }

    @Override
    public void deposit(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        if (!isActive) {
            throw new IllegalStateException("Account is not active");
        }
        balance += amount;
    }

    @Override
    public void withdraw(double amount) throws IllegalArgumentException, UnsupportedOperationException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (!isActive) {
            throw new IllegalStateException("Account is not active");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        balance -= amount;
    }

    @Override
    public abstract double calculateInterest();

    @Override
    public void applyInterest() {
        double interest = calculateInterest();
        if (interest > 0) {
            balance += interest;
        }
    }

    @Override
    public abstract String getAccountType();

    // Getters and Setters
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public LocalDateTime getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(LocalDateTime dateOpened) {
        this.dateOpened = dateOpened;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return getAccountType() + "{" +
                "accountNumber='" + accountNumber + '\'' +
                ", balance=" + String.format("%.2f", balance) +
                ", branch='" + branch + '\'' +
                '}';
    }
}
