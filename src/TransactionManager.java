import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private Connection connection;

    public TransactionManager(Connection connection) {
        this.connection = connection;
    }














    public void recordTransaction(String cardNumber, String type, double amount,String phone_number) {
        try {
            String sql = "INSERT INTO transactions (card_number, type, amount, phone_number,timestamp) VALUES (?, ?,?, ?, CURRENT_TIMESTAMP)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cardNumber);
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
            stmt.setString(4,phone_number);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Failed to record transaction: " + e.getMessage());
        }
    }

    public void showTransactionHistory(String cardNumber) {
        try {
            String sql = "SELECT * FROM transactions WHERE card_number = ? ORDER BY timestamp DESC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();

            System.out.println("=== Transaction History ===");
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                Timestamp time = rs.getTimestamp("timestamp");

                System.out.printf("%s of ₹%.2f on %s\n", type, amount, time.toString());
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to fetch history: " + e.getMessage());
        }
    }
}
