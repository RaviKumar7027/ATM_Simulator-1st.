public class User {

    private String cardNumber;
    private String pin;
    private double balance;
    public User(String cardNumber,String pin,double balance){
      this.balance=balance;
      this.pin=pin;
      this.cardNumber=cardNumber;
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
