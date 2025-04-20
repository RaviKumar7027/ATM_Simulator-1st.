import java.sql.*;
import java.util.Scanner;


public class ATM {
    private PinManager pinManager;


    private Scanner scanner = new Scanner(System.in);
    private Connection connection;

    private TransactionManager transactionManager;


    public ATM() {
        connection = Database.getConnection();
        pinManager = new PinManager(scanner, connection);
        transactionManager = new TransactionManager(connection);

        if (connection != null) {
            showLogin();
        } else {
            System.out.println("❌ Database connection failed.");
        }
    }

    private void showLogin() {
        System.out.println("=== Welcome to ATM Simulator ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Choose option: ");
        int option = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (option == 1) {
            loginUser();
        } else if (option == 2) {
            showRegister();
            showLogin(); // back to login after registration
        } else {
            System.out.println("❌ Invalid choice.");
        }
    }

    private void loginUser() {
        System.out.print("Enter Card Number: ");
        String cardNumber = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        try {
            String sql = "SELECT * FROM users WHERE card_number = ? AND pin = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cardNumber);
            stmt.setString(2, pin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getString("card_number"),
                        rs.getString("pin"),
                        rs.getDouble("balance"),
                        rs.getString("phone_number")
                );
                showMenu(user);

            } else {
                System.out.println("❌ Invalid card number or PIN.");
            }
        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }
    }

    private void showRegister() {
        System.out.println("=== Register New User ===");
        System.out.print("Enter new card number: ");
        String cardNumber = scanner.nextLine();

        System.out.print("Set a 4-digit PIN: ");
        String pin = scanner.nextLine();

        System.out.print("Enter initial deposit amount: ");
        double balance = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();



        try {
            // Check if card number already exists
            String checkSql = "SELECT * FROM users WHERE card_number = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, cardNumber);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("❌ Card number already exists.");
                return;
            }

            String sql = "INSERT INTO users (card_number, pin, balance,phone_number) VALUES (?, ?, ?,?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cardNumber);
            stmt.setString(2, pin);
            stmt.setDouble(3, balance);
            stmt.setString(4, phone);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Registration successful. You can now login.");
            } else {
                System.out.println("❌ Registration failed.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }


    //phone me message ka system hai
    public void sendSMS(String phone, String message) {
        System.out.println("\n📱 SMS to " + phone + ": " + message);
    }







    private void showMenu(User user) {
        while (true) {
            System.out.println("\n=== ATM MENU ===");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Change PIN");
            System.out.println("5. Exit");
            System.out.println("6. View Transaction History");  // case 6
            System.out.println("7. Transfer Money");  // new

            // System.out.println("7. Export Transaction History to PDF");  // ✅ New Option



            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Current Balance: ₹" + user.getBalance());
                    break;
                case 2:
                    System.out.print("Enter amount to deposit: ");
                    double deposit = scanner.nextDouble();
                    user.setBalance(user.getBalance() + deposit);
                    updateBalance(user);
                    transactionManager.recordTransaction(user.getCardNumber(), "Deposit", deposit, user.getPhoneNumber());

                    sendSMS(user.getPhoneNumber(), "₹" + deposit + " deposited. Avl Bal: ₹" + user.getBalance());




                    System.out.println("✅ Deposit successful.");
                    break;
                case 3:
                    System.out.print("Enter amount to withdraw: ");
                    double withdraw = scanner.nextDouble();
                    if (withdraw <= user.getBalance()) {
                        user.setBalance(user.getBalance() - withdraw);
                        updateBalance(user);
                        transactionManager.recordTransaction(user.getCardNumber(), "Withdraw", withdraw, user.getPhoneNumber());


                        sendSMS(user.getPhoneNumber(), "₹" + withdraw + " withdrawn. Avl Bal: ₹" + user.getBalance());



                        System.out.println("✅ Withdrawal successful.");
                    } else {
                        System.out.println("❌ Insufficient balance.");
                    }
                    break;
                case 5:
                    System.out.println("Thank you for using the ATM!");
                    return;
                case 4:
                    pinManager.changePin(user); // ✅ yeh sahi tareeka hai
                    break;
                case 6:
                    transactionManager.showTransactionHistory(user.getCardNumber());
                    break;

                case 7:
                    System.out.print("Enter recipient card number: ");
                    String receiverCard = scanner.nextLine();

                    System.out.print("Enter amount to transfer: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine(); // consume newline

                    TransferMoney transferManager = new TransferMoney(connection);
                    boolean success = transferManager.transferMoney(user.getCardNumber(), receiverCard, amount);

                    if (success) {
                        user.setBalance(user.getBalance() - amount); // update local object
                        sendSMS(user.getPhoneNumber(), "₹" + amount + " transferred to " + receiverCard + ". Avl Bal: ₹" + user.getBalance());

                        transactionManager.recordTransaction(user.getCardNumber(), "Transfer to " + receiverCard, amount, user.getPhoneNumber());
                        transactionManager.recordTransaction(receiverCard, "Received from " + user.getCardNumber(), amount, null);
                    }
                    break;






                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void updateBalance(User user) {
        try {
            String sql = "UPDATE users SET balance = ? WHERE card_number = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, user.getBalance());
            stmt.setString(2, user.getCardNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Failed to update balance: " + e.getMessage());
        }
    }
}
