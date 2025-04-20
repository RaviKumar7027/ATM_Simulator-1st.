import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransferMoney {
    private Connection connection;

    public TransferMoney(Connection connection) {
        this.connection = connection;
    }

    public boolean transferMoney(String senderCard, String receiverCard, double amount) {
        try {
            connection.setAutoCommit(false); // transaction start

            // 1. Check sender balance
            String checkSenderSql = "SELECT balance FROM users WHERE card_number = ?";
            PreparedStatement checkSenderStmt = connection.prepareStatement(checkSenderSql);
            checkSenderStmt.setString(1, senderCard);
            ResultSet senderRs = checkSenderStmt.executeQuery();

            if (!senderRs.next()) {
                System.out.println("❌ Sender account not found.");
                return false;
            }

            double senderBalance = senderRs.getDouble("balance");
            if (amount > senderBalance) {
                System.out.println("❌ Insufficient balance.");
                return false;
            }

            // 2. Check receiver exists
            String checkReceiverSql = "SELECT balance FROM users WHERE card_number = ?";
            PreparedStatement checkReceiverStmt = connection.prepareStatement(checkReceiverSql);
            checkReceiverStmt.setString(1, receiverCard);
            ResultSet receiverRs = checkReceiverStmt.executeQuery();

            if (!receiverRs.next()) {
                System.out.println("❌ Receiver account not found.");
                return false;
            }

            // 3. Deduct from sender
            String deductSql = "UPDATE users SET balance = balance - ? WHERE card_number = ?";
            PreparedStatement deductStmt = connection.prepareStatement(deductSql);
            deductStmt.setDouble(1, amount);
            deductStmt.setString(2, senderCard);
            deductStmt.executeUpdate();

            // 4. Add to receiver
            String addSql = "UPDATE users SET balance = balance + ? WHERE card_number = ?";
            PreparedStatement addStmt = connection.prepareStatement(addSql);
            addStmt.setDouble(1, amount);
            addStmt.setString(2, receiverCard);
            addStmt.executeUpdate();

            // 5. Commit transaction
            connection.commit();
            System.out.println("✅ Money transferred successfully.");
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback(); // rollback if error
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Transfer failed: " + e.getMessage());
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("❌ Failed to reset autocommit: " + e.getMessage());
            }
        }
    }
}
