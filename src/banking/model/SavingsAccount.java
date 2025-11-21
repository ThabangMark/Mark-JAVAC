package banking.model;

public class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.0005; // 0.05% monthly

    public SavingsAccount() {
        super();
    }

    public SavingsAccount(String accountNumber, String branch, Customer customer) {
        super(accountNumber, branch, customer);
    }

    @Override
    public void withdraw(double amount) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Withdrawals are not allowed on Savings Accounts");
    }

    @Override
    public double calculateInterest() {
        return balance * INTEREST_RATE;
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    public static double getInterestRate() {
        return INTEREST_RATE;
    }
}