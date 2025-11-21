package banking.model;

public class ChequeAccount extends Account {
    private String companyName;
    private String companyAddress;

    public ChequeAccount() {
        super();
    }

    public ChequeAccount(String accountNumber, String branch, Customer customer, String companyName, String companyAddress) {
        super(accountNumber, branch, customer);
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required for Cheque Account");
        }
        if (companyAddress == null || companyAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Company address is required for Cheque Account");
        }
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }

    @Override
    public double calculateInterest() {
        return 0.0; // Cheque accounts do not earn interest
    }

    @Override
    public String getAccountType() {
        return "Cheque Account";
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    @Override
    public String toString() {
        return super.toString() + ", company='" + companyName + "'";
    }
}