package banking.model;

public class InvestmentAccount extends Account {
    private static final double INTEREST_RATE = 0.05; // 5% monthly
    private static final double MINIMUM_OPENING_DEPOSIT = 500.00;

    public InvestmentAccount() {
        super();
    }

    public InvestmentAccount(String accountNumber, String branch, Customer customer, double initialDeposit) {
        super(accountNumber, branch, customer);
        if (initialDeposit < MINIMUM_OPENING_DEPOSIT) {
            throw new IllegalArgumentException("Investment account requires minimum opening deposit of BWP " + MINIMUM_OPENING_DEPOSIT);
        }
        this.balance = initialDeposit;
    }

    @Override
    public double calculateInterest() {
        return balance * INTEREST_RATE;
    }

    @Override
    public String getAccountType() {
        return "Investment Account";
    }

    public static double getInterestRate() {
        return INTEREST_RATE;
    }

    public static double getMinimumOpeningDeposit() {
        return MINIMUM_OPENING_DEPOSIT;
    }
}
