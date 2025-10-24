package BankingSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

class ControllerResult<T> {
    private boolean success;
    private String message;
    private T data;

    public ControllerResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }

    public static <T> ControllerResult<T> success(String message, T data) {
        return new ControllerResult<>(true, message, data);
    }

    public static <T> ControllerResult<T> failure(String message) {
        return new ControllerResult<>(false, message, null);
    }
}

class UserCredential {
    private String userId;
    private String fullName;
    private String username;
    private String password;
    private String role;

    public UserCredential(String userId, String fullName, String username,
                          String password, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}

class RegistrationData {
    private UserCredential userCredential;
    private Customer customer;

    public RegistrationData(UserCredential userCredential, Customer customer) {
        this.userCredential = userCredential;
        this.customer = customer;
    }

    public UserCredential getUserCredential() { return userCredential; }
    public Customer getCustomer() { return customer; }
}

class LoginController {
    private Map<String, UserCredential> users;
    private Map<String, String> userIdToCustomerId;
    private CustomerController customerController;
    private int userIdCounter = 1;

    public LoginController() {
        users = new HashMap<>();
        userIdToCustomerId = new HashMap<>();
        users.put("admin", new UserCredential("M001", "Bank Manager", "admin", "admin123", "Manager"));
    }

    public void setCustomerController(CustomerController customerController) {
        this.customerController = customerController;
    }

    public ControllerResult<UserCredential> login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return ControllerResult.failure("Username cannot be empty!");
        }

        if (password == null || password.trim().isEmpty()) {
            return ControllerResult.failure("Password cannot be empty!");
        }

        UserCredential user = users.get(username);
        if (user == null) {
            return ControllerResult.failure("Invalid username or password!");
        }

        if (!user.getPassword().equals(password)) {
            return ControllerResult.failure("Invalid username or password!");
        }

        return ControllerResult.success("Login successful!", user);
    }

    public ControllerResult<RegistrationData> registerCustomer(
            String fullName, String username, String password, String confirmPassword,
            String address, String phone, String email, boolean isEmployed) {

        if (fullName == null || fullName.trim().isEmpty()) {
            return ControllerResult.failure("Full name cannot be empty!");
        }

        if (username == null || username.trim().isEmpty()) {
            return ControllerResult.failure("Username cannot be empty!");
        }

        if (password == null || password.trim().isEmpty()) {
            return ControllerResult.failure("Password cannot be empty!");
        }

        if (!password.equals(confirmPassword)) {
            return ControllerResult.failure("Passwords do not match!");
        }

        if (password.length() < 6) {
            return ControllerResult.failure("Password must be at least 6 characters!");
        }

        if (users.containsKey(username)) {
            return ControllerResult.failure("Username already exists!");
        }

        if (address == null || address.trim().isEmpty()) {
            return ControllerResult.failure("Address cannot be empty!");
        }

        if (phone == null || phone.trim().isEmpty()) {
            return ControllerResult.failure("Phone number cannot be empty!");
        }

        if (email == null || email.trim().isEmpty()) {
            return ControllerResult.failure("Email cannot be empty!");
        }

        String userId = String.format("U%03d", userIdCounter++);
        UserCredential newUser = new UserCredential(userId, fullName, username, password, "Customer");

        String[] nameParts = fullName.trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String surname = nameParts.length > 1 ? nameParts[1] : "";

        if (customerController == null) {
            return ControllerResult.failure("System error: CustomerController not initialized!");
        }

        ControllerResult<Customer> customerResult = customerController.registerCustomer(
                userId, firstName, surname, address, phone, email, isEmployed
        );

        if (!customerResult.isSuccess()) {
            return ControllerResult.failure("Registration failed: " + customerResult.getMessage());
        }

        users.put(username, newUser);

        RegistrationData regData = new RegistrationData(newUser, customerResult.getData());

        return ControllerResult.success(
                "Registration successful!\nUser ID: " + userId +
                        "\nCustomer ID: " + customerResult.getData().getCustomerId(),
                regData
        );
    }

    public void linkUserToCustomer(String userId, String customerId) {
        userIdToCustomerId.put(userId, customerId);
    }

    public String getCustomerId(String userId) {
        return userIdToCustomerId.get(userId);
    }

    public List<UserCredential> getAllCustomers() {
        List<UserCredential> customers = new ArrayList<>();
        for (UserCredential user : users.values()) {
            if (user.getRole().equals("Customer")) {
                customers.add(user);
            }
        }
        return customers;
    }
}

class TransferResult {
    private double sourceBalance;
    private double destinationBalance;

    public TransferResult(double sourceBalance, double destinationBalance) {
        this.sourceBalance = sourceBalance;
        this.destinationBalance = destinationBalance;
    }

    public double getSourceBalance() { return sourceBalance; }
    public double getDestinationBalance() { return destinationBalance; }
}

class AccountController {
    private Bank bank;

    public AccountController(Bank bank) {
        this.bank = bank;
    }

    public ControllerResult<Double> deposit(String accountNumber, double amount, String description) {
        if (amount <= 0) {
            return ControllerResult.failure("Amount must be greater than zero!");
        }

        Account account = findAccount(accountNumber);
        if (account == null) {
            return ControllerResult.failure("Account not found!");
        }

        try {
            if (account instanceof AbstractAccount) {
                ((AbstractAccount) account).deposit(amount, description);
            } else {
                account.deposit(amount);
            }
            return ControllerResult.success(
                    String.format("Successfully deposited BWP %.2f", amount),
                    account.getBalance()
            );
        } catch (Exception e) {
            return ControllerResult.failure("Deposit failed: " + e.getMessage());
        }
    }

    public ControllerResult<Double> withdraw(String accountNumber, double amount, String description) {
        if (amount <= 0) {
            return ControllerResult.failure("Amount must be greater than zero!");
        }

        Account account = findAccount(accountNumber);
        if (account == null) {
            return ControllerResult.failure("Account not found!");
        }

        try {
            boolean success = account.withdraw(amount);
            if (success) {
                return ControllerResult.success(
                        String.format("Successfully withdrew BWP %.2f", amount),
                        account.getBalance()
                );
            } else {
                return ControllerResult.failure("Withdrawal failed! Check account type and balance.");
            }
        } catch (Exception e) {
            return ControllerResult.failure("Withdrawal failed: " + e.getMessage());
        }
    }

    public ControllerResult<TransferResult> transfer(String fromAccountNumber,
                                                     String toAccountNumber, double amount) {
        if (amount <= 0) {
            return ControllerResult.failure("Amount must be greater than zero!");
        }

        if (fromAccountNumber.equals(toAccountNumber)) {
            return ControllerResult.failure("Cannot transfer to the same account!");
        }

        Account fromAccount = findAccount(fromAccountNumber);
        Account toAccount = findAccount(toAccountNumber);

        if (fromAccount == null) {
            return ControllerResult.failure("Source account not found!");
        }

        if (toAccount == null) {
            return ControllerResult.failure("Destination account not found!");
        }

        if (fromAccount.getBalance() < amount) {
            return ControllerResult.failure("Insufficient funds in source account!");
        }

        try {
            boolean withdrawSuccess = fromAccount.withdraw(amount);
            if (!withdrawSuccess) {
                return ControllerResult.failure("Transfer failed! Source account does not allow withdrawals.");
            }

            if (toAccount instanceof AbstractAccount) {
                ((AbstractAccount) toAccount).deposit(amount, "Transfer from " + fromAccountNumber);
            } else {
                toAccount.deposit(amount);
            }

            TransferResult result = new TransferResult(
                    fromAccount.getBalance(),
                    toAccount.getBalance()
            );

            return ControllerResult.success(
                    String.format("Successfully transferred BWP %.2f", amount),
                    result
            );
        } catch (Exception e) {
            return ControllerResult.failure("Transfer failed: " + e.getMessage());
        }
    }

    private Account findAccount(String accountNumber) {
        for (Customer customer : bank.getCustomers()) {
            Account account = customer.getAccountByNumber(accountNumber);
            if (account != null) {
                return account;
            }
        }
        return null;
    }
}

class CustomerController {
    private Bank bank;
    private LoginController loginController;

    public CustomerController(Bank bank, LoginController loginController) {
        this.bank = bank;
        this.loginController = loginController;
    }

    public ControllerResult<Customer> registerCustomer(String userId, String firstName, String surname,
                                                       String address, String phone,
                                                       String email, boolean isEmployed) {
        try {
            String customerId = "CUST" + System.currentTimeMillis();

            Customer customer = new Customer(customerId, firstName, surname, address, phone, email, isEmployed);

            bank.getCustomers().add(customer);

            loginController.linkUserToCustomer(userId, customer.getCustomerId());

            return ControllerResult.success(
                    "Customer registered successfully! ID: " + customer.getCustomerId(),
                    customer
            );
        } catch (Exception e) {
            return ControllerResult.failure("Registration failed: " + e.getMessage());
        }
    }
}

class InterestPaymentSummary {
    private int accountsProcessed;
    private double totalInterestPaid;
    private List<String> errors;

    public InterestPaymentSummary(int accountsProcessed, double totalInterestPaid, List<String> errors) {
        this.accountsProcessed = accountsProcessed;
        this.totalInterestPaid = totalInterestPaid;
        this.errors = errors;
    }

    public int getAccountsProcessed() { return accountsProcessed; }
    public double getTotalInterestPaid() { return totalInterestPaid; }
    public List<String> getErrors() { return errors; }
}

class ManagerController {
    private Bank bank;
    private LoginController loginController;

    public ManagerController(Bank bank, LoginController loginController) {
        this.bank = bank;
        this.loginController = loginController;
    }

    public List<Customer> getAllCustomers() {
        return bank.getCustomers();
    }

    public ControllerResult<Account> createSavingsAccount(String userId, String branch) {
        String customerId = loginController.getCustomerId(userId);
        if (customerId == null) {
            return ControllerResult.failure("Customer not found! Please register customer profile first.");
        }

        Customer customer = bank.findCustomerById(customerId);
        if (customer == null) {
            return ControllerResult.failure("Customer not found in bank records!");
        }

        try {
            SavingsAccount account = bank.createSavingsAccount(customer, branch);
            return ControllerResult.success(
                    "Savings account created successfully!\nAccount Number: " + account.getAccountNumber(),
                    account
            );
        } catch (Exception e) {
            return ControllerResult.failure("Account creation failed: " + e.getMessage());
        }
    }

    public ControllerResult<Account> createInvestmentAccount(String userId, String branch,
                                                             double initialBalance) {
        String customerId = loginController.getCustomerId(userId);
        if (customerId == null) {
            return ControllerResult.failure("Customer not found! Please register customer profile first.");
        }

        Customer customer = bank.findCustomerById(customerId);
        if (customer == null) {
            return ControllerResult.failure("Customer not found in bank records!");
        }

        if (initialBalance < 500.00) {
            return ControllerResult.failure("Investment account requires minimum BWP 500.00!");
        }

        try {
            InvestmentAccount account = bank.createInvestmentAccount(customer, branch, initialBalance);
            return ControllerResult.success(
                    "Investment account created successfully!\nAccount Number: " + account.getAccountNumber(),
                    account
            );
        } catch (Exception e) {
            return ControllerResult.failure("Account creation failed: " + e.getMessage());
        }
    }

    public ControllerResult<Account> createChequeAccount(String userId, String branch,
                                                         String employerName, String employerAddress) {
        String customerId = loginController.getCustomerId(userId);
        if (customerId == null) {
            return ControllerResult.failure("Customer not found! Please register customer profile first.");
        }

        Customer customer = bank.findCustomerById(customerId);
        if (customer == null) {
            return ControllerResult.failure("Customer not found in bank records!");
        }

        if (!customer.isEmployed()) {
            return ControllerResult.failure("Customer must be employed to open a cheque account!");
        }

        if (employerName == null || employerName.trim().isEmpty()) {
            return ControllerResult.failure("Employer name is required!");
        }

        if (employerAddress == null || employerAddress.trim().isEmpty()) {
            return ControllerResult.failure("Employer address is required!");
        }

        try {
            ChequeAccount account = bank.createChequeAccount(customer, branch, employerName, employerAddress);
            return ControllerResult.success(
                    "Cheque account created successfully!\nAccount Number: " + account.getAccountNumber(),
                    account
            );
        } catch (Exception e) {
            return ControllerResult.failure("Account creation failed: " + e.getMessage());
        }
    }

    public ControllerResult<InterestPaymentSummary> payMonthlyInterest() {
        int accountsProcessed = 0;
        double totalInterest = 0.0;
        List<String> errors = new ArrayList<>();

        try {
            for (Customer customer : bank.getCustomers()) {
                for (Account account : customer.getAccounts()) {
                    try {
                        double balanceBefore = account.getBalance();
                        account.payInterest();
                        double balanceAfter = account.getBalance();
                        double interest = balanceAfter - balanceBefore;

                        if (interest > 0) {
                            totalInterest += interest;
                            accountsProcessed++;
                        }
                    } catch (Exception e) {
                        errors.add("Error processing account " + account.getAccountNumber() + ": " + e.getMessage());
                    }
                }
            }

            InterestPaymentSummary summary = new InterestPaymentSummary(
                    accountsProcessed, totalInterest, errors
            );

            return ControllerResult.success(
                    "Interest payment completed successfully!",
                    summary
            );
        } catch (Exception e) {
            return ControllerResult.failure("Interest payment failed: " + e.getMessage());
        }
    }
}
