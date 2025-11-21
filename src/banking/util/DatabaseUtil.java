package banking.util;

import java.sql.*;

public class DatabaseUtil {
    private static final String URL = "jdbc:sqlite:banking_system.db";
    private static Connection connection = null;

    // Get database connection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    // Initialize database tables
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL," +
                    "customer_id INTEGER)");

            // Create Customers table
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "customer_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "first_name TEXT NOT NULL," +
                    "surname TEXT NOT NULL," +
                    "address TEXT," +
                    "phone_number TEXT," +
                    "email TEXT UNIQUE)");

            // Create Accounts table
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "account_number TEXT PRIMARY KEY," +
                    "customer_id INTEGER NOT NULL," +
                    "account_type TEXT NOT NULL," +
                    "balance REAL DEFAULT 0," +
                    "branch TEXT," +
                    "date_opened TEXT," +
                    "is_active INTEGER DEFAULT 1," +
                    "company_name TEXT," +
                    "company_address TEXT," +
                    "FOREIGN KEY(customer_id) REFERENCES customers(customer_id))");

            // Create Transactions table
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "account_number TEXT NOT NULL," +
                    "transaction_type TEXT NOT NULL," +
                    "amount REAL NOT NULL," +
                    "balance_after REAL NOT NULL," +
                    "description TEXT," +
                    "timestamp TEXT NOT NULL," +
                    "FOREIGN KEY(account_number) REFERENCES accounts(account_number))");

            System.out.println("Database initialized successfully!");

            // Insert default admin user if not exists
            insertDefaultAdmin();

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertDefaultAdmin() {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password, role, customer_id) VALUES (?, ?, ?, NULL)";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, "admin");
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, "admin123");
                    insertStmt.setString(3, "ADMIN");
                    insertStmt.executeUpdate();
                    System.out.println("Default admin user created (username: admin, password: admin123)");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating default admin: " + e.getMessage());
        }
    }

    // Close connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}