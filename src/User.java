public class User {

    private String cardNumber;
    private String pin;
    private double balance;
    private String phoneNumber;
    public User(String cardNumber,String pin,double balance,String phoneNumber){
      this.balance=balance;
      this.pin=pin;
      this.cardNumber=cardNumber;
      this.phoneNumber=phoneNumber;
    }




    // Getter and Setter
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }


    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    public void displayInfo() {
        System.out.println("Card Number: " + cardNumber);
        System.out.println("Balance: ₹" + balance);
    }


}
