package banking.dao;

import banking.model.Customer;
import banking.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (first_name, surname, address, phone_number, email) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getSurname());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getPhoneNumber());
            pstmt.setString(5, customer.getEmail());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Retrieve the generated customer_id using last_insert_rowid()
                String getIdSql = "SELECT last_insert_rowid()";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(getIdSql)) {
                    if (rs.next()) {
                        customer.setCustomerId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error creating customer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customer: " + e.getMessage());
        }
        return null;
    }

    public Customer getCustomerByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customer by email: " + e.getMessage());
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_id";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all customers: " + e.getMessage());
        }
        return customers;
    }

    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET first_name = ?, surname = ?, address = ?, phone_number = ?, email = ? WHERE customer_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getSurname());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getPhoneNumber());
            pstmt.setString(5, customer.getEmail());
            pstmt.setInt(6, customer.getCustomerId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
        }
        return false;
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setSurname(rs.getString("surname"));
        customer.setAddress(rs.getString("address"));
        customer.setPhoneNumber(rs.getString("phone_number"));
        customer.setEmail(rs.getString("email"));
        return customer;
    }
}