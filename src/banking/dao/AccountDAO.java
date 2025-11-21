package banking.dao;

import banking.model.*;
import banking.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, branch, date_opened, is_active, company_name, company_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account.getAccountNumber());
            pstmt.setInt(2, account.getCustomer().getCustomerId());
            pstmt.setString(3, account.getAccountType());
            pstmt.setDouble(4, account.getBalance());
            pstmt.setString(5, account.getBranch());
            pstmt.setString(6, account.getDateOpened().toString());
            pstmt.setInt(7, account.isActive() ? 1 : 0);

            if (account instanceof ChequeAccount) {
                ChequeAccount chequeAccount = (ChequeAccount) account;
                pstmt.setString(8, chequeAccount.getCompanyName());
                pstmt.setString(9, chequeAccount.getCompanyAddress());
            } else {
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.VARCHAR);
            }

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
        }
        return false;
    }

    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching account: " + e.getMessage());
        }
        return null;
    }

    public List<Account> getAccountsByCustomerId(int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY date_opened DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching accounts: " + e.getMessage());
        }
        return accounts;
    }

    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY date_opened DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all accounts: " + e.getMessage());
        }
        return accounts;
    }

    public boolean updateAccountBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating account balance: " + e.getMessage());
        }
        return false;
    }

    public boolean updateAccount(Account account) {
        String sql = "UPDATE accounts SET balance = ?, is_active = ? WHERE account_number = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, account.getBalance());
            pstmt.setInt(2, account.isActive() ? 1 : 0);
            pstmt.setString(3, account.getAccountNumber());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating account: " + e.getMessage());
        }
        return false;
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        String accountType = rs.getString("account_type");
        String accountNumber = rs.getString("account_number");
        String branch = rs.getString("branch");
        double balance = rs.getDouble("balance");
        boolean isActive = rs.getInt("is_active") == 1;
        LocalDateTime dateOpened = LocalDateTime.parse(rs.getString("date_opened"));

        // Get customer
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.getCustomerById(rs.getInt("customer_id"));

        Account account;

        switch (accountType) {
            case "Savings Account":
                account = new SavingsAccount(accountNumber, branch, customer);
                break;
            case "Investment Account":
                account = new InvestmentAccount();
                account.setAccountNumber(accountNumber);
                account.setBranch(branch);
                account.setCustomer(customer);
                break;
            case "Cheque Account":
                String companyName = rs.getString("company_name");
                String companyAddress = rs.getString("company_address");
                account = new ChequeAccount(accountNumber, branch, customer, companyName, companyAddress);
                break;
            default:
                throw new SQLException("Unknown account type: " + accountType);
        }

        account.setBalance(balance);
        account.setActive(isActive);
        account.setDateOpened(dateOpened);

        return account;
    }
}
