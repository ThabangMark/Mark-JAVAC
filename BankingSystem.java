import java.util.*;
import java.time.LocalDateTime;

// ==================== INTERFACES ====================

/**
 * Base Account Interface - demonstrates Interface principle
 */
interface Account {
    void deposit(double amount);
    boolean withdraw(double amount);
    double getBalance();
    String getAccountNumber();
    void payInterest();
    String getAccountType();
}

/**
 * Interface for accounts that support interest calculation
 */
interface InterestBearing {
    double calculateInterest();
    double getInterestRate();
}

// ==================== ABSTRACT CLASSES ====================

/**
 * Abstract Account Class - demonstrates Abstraction and base for Inheritance
 */
abstract class AbstractAccount implements Account {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected Customer customer;
    protected LocalDateTime dateOpened;
    protected List<Transaction> transactionHistory;
    
    public AbstractAccount(String accountNumber, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.customer = customer;
        this.balance = 0.0;
        this.dateOpened = LocalDateTime.now();
        this.transactionHistory = new ArrayList<>();
    }
    
    // Method Overloading - deposit with transaction description
    @Override
    public void deposit(double amount) {
        deposit(amount, "Standard Deposit");
    }
    
    // Overloaded method
    public void deposit(double amount, String description) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add(new Transaction("DEPOSIT", amount, balance, description));
            System.out.println("Deposited BWP " + amount + " to account " + accountNumber);
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }
    
    @Override
    public double getBalance() {
        return balance;
    }
    
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public String getBranch() {
        return branch;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public LocalDateTime getDateOpened() {
        return dateOpened;
    }
    
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }
    
    // Abstract methods - must be implemented by subclasses (Polymorphism)
    public abstract void payInterest();
    public abstract String getAccountType();
    
    // Method to display account summary
    public void displayAccountSummary() {
        System.out.println("\n--- Account Summary ---");
        System.out.println("Account Type: " + getAccountType());
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Balance: BWP " + String.format("%.2f", balance));
        System.out.println("Branch: " + branch);
        System.out.println("Customer: " + customer.getFullName());
    }
}

// ==================== CONCRETE ACCOUNT CLASSES ====================

/**
 * Savings Account - demonstrates Inheritance and Method Overriding
 */
class SavingsAccount extends AbstractAccount implements InterestBearing {
    private static final double MONTHLY_INTEREST_RATE = 0.0005; // 0.05% monthly
    
    public SavingsAccount(String accountNumber, String branch, Customer customer) {
        super(accountNumber, branch, customer);
    }
    
    // Method Overriding - specific implementation for SavingsAccount
    @Override
    public boolean withdraw(double amount) {
        // Savings account does not allow withdrawals - Polymorphic behavior
        System.out.println("Withdrawals not allowed on Savings Account");
        transactionHistory.add(new Transaction("WITHDRAWAL_FAILED", amount, balance, "Withdrawals not permitted"));
        return false;
    }
    
    // Overloaded withdraw method
    public boolean withdraw(double amount, String reason) {
        System.out.println("Withdrawal attempt (" + reason + ") denied on Savings Account");
        return false;
    }
    
    @Override
    public void payInterest() {
        double interest = calculateInterest();
        balance += interest;
        transactionHistory.add(new Transaction("INTEREST", interest, balance, "Monthly interest payment"));
        System.out.println("Interest of BWP " + String.format("%.2f", interest) + " paid to Savings Account " + accountNumber);
    }
    
    @Override
    public double calculateInterest() {
        return balance * MONTHLY_INTEREST_RATE;
    }
    
    @Override
    public double getInterestRate() {
        return MONTHLY_INTEREST_RATE;
    }
    
    @Override
    public String getAccountType() {
        return "Savings Account";
    }
}

/**
 * Investment Account - demonstrates Inheritance and Method Overriding
 */
class InvestmentAccount extends AbstractAccount implements InterestBearing {
    private static final double MONTHLY_INTEREST_RATE = 0.05; // 5% monthly
    private static final double MINIMUM_OPENING_BALANCE = 500.00;
    
    public InvestmentAccount(String accountNumber, String branch, Customer customer, double initialDeposit) {
        super(accountNumber, branch, customer);
        if (initialDeposit < MINIMUM_OPENING_BALANCE) {
            throw new IllegalArgumentException("Investment Account requires minimum opening balance of BWP " + MINIMUM_OPENING_BALANCE);
        }
        this.balance = initialDeposit;
        transactionHistory.add(new Transaction("OPENING_DEPOSIT", initialDeposit, balance, "Account opening"));
    }
    
    // Method Overriding - specific implementation for InvestmentAccount
    @Override
    public boolean withdraw(double amount) {
        return withdraw(amount, "Standard Withdrawal");
    }
    
    // Method Overloading - withdraw with description
    public boolean withdraw(double amount, String description) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance, description));
            System.out.println("Withdrew BWP " + amount + " from Investment Account " + accountNumber);
            return true;
        } else {
            System.out.println("Insufficient funds or invalid amount for withdrawal");
            transactionHistory.add(new Transaction("WITHDRAWAL_FAILED", amount, balance, "Insufficient funds"));
            return false;
        }
    }
    
    @Override
    public void payInterest() {
        double interest = calculateInterest();
        balance += interest;
        transactionHistory.add(new Transaction("INTEREST", interest, balance, "Monthly interest payment"));
        System.out.println("Interest of BWP " + String.format("%.2f", interest) + " paid to Investment Account " + accountNumber);
    }
    
    @Override
    public double calculateInterest() {
        return balance * MONTHLY_INTEREST_RATE;
    }
    
    @Override
    public double getInterestRate() {
        return MONTHLY_INTEREST_RATE;
    }
    
    @Override
    public String getAccountType() {
        return "Investment Account";
    }
}

/**
 * Cheque Account - demonstrates Inheritance and Method Overriding
 */
class ChequeAccount extends AbstractAccount {
    private String employerName;
    private String employerAddress;
    
    public ChequeAccount(String accountNumber, String branch, Customer customer, 
                        String employerName, String employerAddress) {
        super(accountNumber, branch, customer);
        if (!customer.isEmployed()) {
            throw new IllegalArgumentException("Cheque Account can only be opened for employed customers");
        }
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }
    
    // Method Overriding - specific implementation for ChequeAccount
    @Override
    public boolean withdraw(double amount) {
        return withdraw(amount, "ATM Withdrawal");
    }
    
    // Method Overloading - withdraw with description
    public boolean withdraw(double amount, String description) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance, description));
            System.out.println("Withdrew BWP " + amount + " from Cheque Account " + accountNumber);
            return true;
        } else {
            System.out.println("Insufficient funds or invalid amount for withdrawal");
            transactionHistory.add(new Transaction("WITHDRAWAL_FAILED", amount, balance, "Insufficient funds"));
            return false;
        }
    }
    
    @Override
    public void payInterest() {
        // Cheque accounts don't pay interest - Polymorphic behavior
        System.out.println("No interest paid on Cheque Account " + accountNumber);
    }
    
    @Override
    public String getAccountType() {
        return "Cheque Account";
    }
    
    // Method Overloading - deposit salary
    public void depositSalary(double amount, String employerRef) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add(new Transaction("SALARY", amount, balance, "Salary from " + employerName + " - Ref: " + employerRef));
            System.out.println("Salary of BWP " + amount + " credited to account " + accountNumber);
        }
    }
    
    public String getEmployerName() {
        return employerName;
    }
    
    public String getEmployerAddress() {
        return employerAddress;
    }
    
    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }
    
    public void setEmployerAddress(String employerAddress) {
        this.employerAddress = employerAddress;
    }
}

// ==================== TRANSACTION CLASS ====================

/**
 * Transaction class to track account activity - demonstrates Encapsulation
 */
class Transaction {
    private String transactionId;
    private String type;
    private double amount;
    private double balanceAfter;
    private String description;
    private LocalDateTime timestamp;
    
    public Transaction(String type, double amount, double balanceAfter, String description) {
        this.transactionId = "TXN" + System.currentTimeMillis();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters - Encapsulation
    public String getTransactionId() { return transactionId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: BWP %.2f - %s (Balance: BWP %.2f)", 
            timestamp.toString(), type, amount, description, balanceAfter);
    }
}

// ==================== CUSTOMER CLASS ====================

/**
 * Customer Class - demonstrates Encapsulation and Composition
 */
class Customer {
    private String customerId;
    private String firstName;
    private String surname;
    private String address;
    private String phoneNumber;
    private String email;
    private boolean employed;
    private List<Account> accounts;
    
    public Customer(String customerId, String firstName, String surname, String address, 
                   String phoneNumber, String email, boolean employed) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.employed = employed;
        this.accounts = new ArrayList<>();
    }
    
    public void addAccount(Account account) {
        accounts.add(account);
        System.out.println("Account " + account.getAccountNumber() + " added for customer " + getFullName());
    }
    
    // Method Overloading - get accounts by type
    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }
    
    public List<Account> getAccounts(String accountType) {
        List<Account> filteredAccounts = new ArrayList<>();
        for (Account account : accounts) {
            if (account.getAccountType().equalsIgnoreCase(accountType)) {
                filteredAccounts.add(account);
            }
        }
        return filteredAccounts;
    }
    
    public Account getAccountByNumber(String accountNumber) {
        return accounts.stream()
            .filter(account -> account.getAccountNumber().equals(accountNumber))
            .findFirst()
            .orElse(null);
    }
    
    public void displayAccounts() {
        System.out.println("\nAccounts for " + getFullName() + ":");
        for (Account account : accounts) {
            System.out.println("- " + account.getAccountType() + " (" + account.getAccountNumber() + 
                             "): BWP " + String.format("%.2f", account.getBalance()));
        }
    }
    
    public int getAccountCount() {
        return accounts.size();
    }
    
    public double getTotalBalance() {
        double total = 0;
        for (Account account : accounts) {
            total += account.getBalance();
        }
        return total;
    }
    
    // Getters and Setters - Encapsulation
    public String getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
    public String getFullName() { return firstName + " " + surname; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public boolean isEmployed() { return employed; }
    
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setEmployed(boolean employed) { this.employed = employed; }
}

// ==================== BANK CLASS ====================

/**
 * Bank Class - demonstrates Composition and manages the entire system
 */
class Bank {
    private String bankName;
    private List<Customer> customers;
    private int accountCounter;
    
    public Bank(String bankName) {
        this.bankName = bankName;
        this.customers = new ArrayList<>();
        this.accountCounter = 1000;
    }
    
    // Method Overloading - register customer with minimal info
    public Customer registerCustomer(String firstName, String surname, String phoneNumber) {
        return registerCustomer(firstName, surname, "", phoneNumber, "", false);
    }
    
    // Full registration
    public Customer registerCustomer(String firstName, String surname, String address, 
                                   String phoneNumber, String email, boolean employed) {
        String customerId = "CUST" + System.currentTimeMillis();
        Customer customer = new Customer(customerId, firstName, surname, address, 
                                       phoneNumber, email, employed);
        customers.add(customer);
        System.out.println("Customer " + customer.getFullName() + " registered successfully (ID: " + customerId + ")");
        return customer;
    }
    
    public SavingsAccount createSavingsAccount(Customer customer, String branch) {
        String accountNumber = "SAV" + (++accountCounter);
        SavingsAccount account = new SavingsAccount(accountNumber, branch, customer);
        customer.addAccount(account);
        return account;
    }
    
    public InvestmentAccount createInvestmentAccount(Customer customer, String branch, double initialDeposit) {
        String accountNumber = "INV" + (++accountCounter);
        InvestmentAccount account = new InvestmentAccount(accountNumber, branch, customer, initialDeposit);
        customer.addAccount(account);
        return account;
    }
    
    public ChequeAccount createChequeAccount(Customer customer, String branch, 
                                           String employerName, String employerAddress) {
        String accountNumber = "CHQ" + (++accountCounter);
        ChequeAccount account = new ChequeAccount(accountNumber, branch, customer, employerName, employerAddress);
        customer.addAccount(account);
        return account;
    }
    
    public void payMonthlyInterest() {
        System.out.println("\n=== Paying Monthly Interest ===");
        for (Customer customer : customers) {
            for (Account account : customer.getAccounts()) {
                account.payInterest();
            }
        }
    }
    
    public Customer findCustomerById(String customerId) {
        return customers.stream()
            .filter(customer -> customer.getCustomerId().equals(customerId))
            .findFirst()
            .orElse(null);
    }
    
    // Method Overloading - find customer by different criteria
    public Customer findCustomerByPhone(String phoneNumber) {
        return customers.stream()
            .filter(customer -> customer.getPhoneNumber().equals(phoneNumber))
            .findFirst()
            .orElse(null);
    }
    
    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }
    
    public void displayAllCustomers() {
        System.out.println("\n=== All Customers at " + bankName + " ===");
        for (Customer customer : customers) {
            System.out.println("\nCustomer: " + customer.getFullName() + " (ID: " + customer.getCustomerId() + ")");
            System.out.println("Total Accounts: " + customer.getAccountCount());
            System.out.println("Total Balance: BWP " + String.format("%.2f", customer.getTotalBalance()));
            customer.displayAccounts();
        }
    }
    
    public String getBankName() {
        return bankName;
    }
}

// ==================== EXCEPTION CLASSES ====================

class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

class InvalidAccountTypeException extends Exception {
    public InvalidAccountTypeException(String message) {
        super(message);
    }
}

// ==================== DEMONSTRATION CLASS ====================

public class BankingSystem {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   BANKING SYSTEM DEMONSTRATION");
        System.out.println("   Showcasing ALL OOP Principles");
        System.out.println("========================================\n");
        
        // Create bank
        Bank bank = new Bank("Botswana National Bank");
        
        try {
            // Register customers
            Customer thabang = bank.registerCustomer("Thabang", "Mark", "123 Main St, Gaborone", 
                                                 "72123456", "thabang.mark@email.com", true);
            Customer tshiamo = bank.registerCustomer("Tshiamo", "Denis", "456 Oak Ave, Francistown", 
                                                 "75987654", "tshiamo.denis@email.com", false);
            
            // Create accounts
            System.out.println("\n--- Creating Accounts ---");
            SavingsAccount thabangSavings = bank.createSavingsAccount(thabang, "Main Branch");
            InvestmentAccount thabangInvestment = bank.createInvestmentAccount(thabang, "Main Branch", 1000.0);
            ChequeAccount thabangCheque = bank.createChequeAccount(thabang, "Main Branch", "ABC Company", "789 Business St");
            
            SavingsAccount tshiamoSavings = bank.createSavingsAccount(tshiamo, "North Branch");
            InvestmentAccount tshiamoInvestment = bank.createInvestmentAccount(tshiamo, "North Branch", 750.0);
            
            // Demonstrate method overloading - deposits with and without description
            System.out.println("\n--- Demonstrating Method Overloading ---");
            thabangSavings.deposit(500.0); // Using overloaded method without description
            thabangSavings.deposit(300.0, "Birthday Gift"); // Using overloaded method with description
            
            thabangInvestment.deposit(200.0);
            thabangCheque.depositSalary(2000.0, "SAL2024-OCT"); // Specialized overloaded method
            
            tshiamoSavings.deposit(300.0);
            tshiamoInvestment.deposit(250.0, "Investment Contribution");
            
            // Demonstrate polymorphism - withdraw behaves differently per account type
            System.out.println("\n--- Demonstrating Polymorphism (Method Overriding) ---");
            thabangSavings.withdraw(100.0); // Will fail - no withdrawals allowed
            thabangInvestment.withdraw(50.0); // Will succeed
            thabangCheque.withdraw(200.0, "Shopping"); // Using overloaded withdraw
            
            // Display all customers
            bank.displayAllCustomers();
            
            // Pay monthly interest - demonstrates polymorphism
            bank.payMonthlyInterest();
            
            // Display after interest
            System.out.println("\n=== After Interest Payment ===");
            bank.displayAllCustomers();
            
            // Demonstrate getting accounts by type
            System.out.println("\n--- Getting Specific Account Types ---");
            List<Account> thabangSavingsAccounts = thabang.getAccounts("Savings Account");
            System.out.println("Thabang has " + thabangSavingsAccounts.size() + " Savings Account(s)");
            
            System.out.println("\n========================================");
            System.out.println("   OOP PRINCIPLES DEMONSTRATED:");
            System.out.println("   ✓ Interfaces (Account, InterestBearing)");
            System.out.println("   ✓ Abstraction (AbstractAccount)");
            System.out.println("   ✓ Inheritance (All account types extend AbstractAccount)");
            System.out.println("   ✓ Polymorphism (Overriding withdraw, payInterest methods)");
            System.out.println("   ✓ Encapsulation (Private fields, getters/setters)");
            System.out.println("   ✓ Method Overloading (Multiple versions of deposit, withdraw, etc.)");
            System.out.println("========================================\n");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}