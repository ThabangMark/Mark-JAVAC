package banking.dao;

import banking.model.User;
import banking.util.DatabaseUtil;
import java.sql.*;

public class UserDAO {

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password, role, customer_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole().name());
            if (user.getCustomerId() != null) {
                pstmt.setInt(4, user.getCustomerId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Retrieve the generated user_id using last_insert_rowid()
                String getIdSql = "SELECT last_insert_rowid()";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(getIdSql)) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(User.UserRole.valueOf(rs.getString("role")));

        int customerId = rs.getInt("customer_id");
        if (!rs.wasNull()) {
            user.setCustomerId(customerId);
        }

        return user;
    }
}