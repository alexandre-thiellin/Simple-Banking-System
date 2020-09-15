package banking;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Card {

    private String cardNumber;
    private String pinCode;
    private int balance = 0;

    public Card() {

        generateCardNumber();
        generatePinCode();
    }

    public Card(String cardNumber, String pinCode, int balance) {

        this.cardNumber = cardNumber;
        this.pinCode = pinCode;
        this.balance = balance;
    }

    private void generateCardNumber() {

        cardNumber = "400000";
        Random r = new Random();
        for (int i = 0; i < 9; i++) {
            cardNumber += ""+r.nextInt(10);
        }

        char[] chars = cardNumber.toCharArray();
        int[] digits = new int[15];

        for (int i = 0; i < 15; i++) {
            digits[i] = Integer.parseInt(chars[i]+"");
            if(i % 2 == 0) {
                digits[i] *= 2;
            }
            if(digits[i] > 9) {
                digits[i] -= 9;
            }
        }

        int sum = Arrays.stream(digits).sum();
        boolean valid = false;
        int i = 0;

        while(!valid && i < 10) {

            if((sum + i) % 10 == 0) {
                valid = true;
                cardNumber += i;
            }
            i++;
        }
    }

    private void generatePinCode() {

        pinCode = "";
        Random r = new Random();
        for (int i = 0; i < 4; i++) {
            pinCode += ""+r.nextInt(10);
        }
    }

    public String getCardNumber() {

        return cardNumber;
    }

    public String getPinCode() {

        return pinCode;
    }

    public int getBalance() {

        return balance;
    }

    public void setBalance(int balance) {

        this.balance = balance;
    }

    @Override
    public String toString() {

        return "Your card number:\n" +
                cardNumber + "\n" +
                "Your card PIN:\n" +
                pinCode + "\n";
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cardNumber.equals(card.cardNumber);
    }

    @Override
    public int hashCode() {

        return Objects.hash(cardNumber, pinCode);
    }
}
