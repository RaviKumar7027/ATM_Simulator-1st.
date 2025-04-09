import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private Connection connection;

    public TransactionManager(Connection connection) {
        this.connection = connection;
    }

    public void exportTransactionHistoryToPDF(String cardNumber, String fileName) {
        List<Transaction> transactions = getTransactions(cardNumber);
        if (transactions.isEmpty()) {
            System.out.println("⚠️ No transactions to export.");
            return;
        }

        PDFExportManager.generateTransactionPDF(transactions, fileName);
        System.out.println("✅ PDF exported successfully as " + fileName);
    }

    public List<Transaction> getTransactions(String cardNumber) {
        List<Transaction> transactions = new ArrayList<>();

        try {
            String sql = "SELECT * FROM transactions WHERE card_number = ? ORDER BY timestamp DESC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction tx = new Transaction();
                tx.setCardNumber(cardNumber);
                tx.setType(rs.getString("type"));
                tx.setAmount(rs.getDouble("amount"));
                tx.setDate(rs.getTimestamp("timestamp"));  // Make sure your Transaction class has this
                transactions.add(tx);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching transactions: " + e.getMessage());
        }

        return transactions;
    }













    public void recordTransaction(String cardNumber, String type, double amount) {
        try {
            String sql = "INSERT INTO transactions (card_number, type, amount, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cardNumber);
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
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
