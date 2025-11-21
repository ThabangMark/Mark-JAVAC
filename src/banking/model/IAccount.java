package banking.model;

public interface IAccount {
    void deposit(double amount) throws IllegalArgumentException;
    void withdraw(double amount) throws IllegalArgumentException, UnsupportedOperationException;
    double calculateInterest();
    void applyInterest();
    String getAccountType();
    double getBalance();
    String getAccountNumber();
}
