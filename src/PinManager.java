
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class PinManager {

    private final Scanner scanner;
    private final Connection connection;

    public PinManager(Scanner scanner, Connection connection) {
        this.scanner = scanner;
        this.connection = connection;
    }

    public void changePin(User user) {
        System.out.print("Enter current PIN: ");
        String currentPin = scanner.nextLine();

        if (!currentPin.equals(user.getPin())) {
            System.out.println("❌ Incorrect current PIN.");
            return;
        }

        System.out.print("Enter new 4-digit PIN: ");
        String newPin = scanner.nextLine();

        if (newPin.length() != 4 || !newPin.matches("\\d+")) {
            System.out.println("❌ PIN must be exactly 4 digits.");
            return;
        }

        try {
            String sql = "UPDATE users SET pin = ? WHERE card_number = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, newPin);
            stmt.setString(2, user.getCardNumber());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                user.setPin(newPin); // update user object too
                System.out.println("✅ PIN changed successfully.");
            } else {
                System.out.println("❌ Failed to change PIN.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
