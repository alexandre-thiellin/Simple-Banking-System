package banking;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class BankingSystem {

    private HashSet<Card> cards = new HashSet<>();

    private final Scanner scanner = new Scanner(System.in);

    private Connection connection = null;

    private boolean logged = false;

    private Card cardLogged = null;

    public BankingSystem(String fileName) {

        try {

            String url = "jdbc:sqlite:"+fileName;

            String query = "CREATE TABLE IF NOT EXISTS card (\n"
                    + "	id INTEGER PRIMARY KEY,\n"
                    + "	number TEXT,\n"
                    + "	pin TEXT,\n"
                    + " balance INTEGER DEFAULT 0\n"
                    + ");";

            connection = DriverManager.getConnection(url);

            Statement statement = connection.createStatement();

            try {
                statement.executeQuery(query);
            } catch (SQLException e) {

            }

            getCardsFromDB();

        } catch (SQLException e) {

            System.out.println(e.getMessage());
        }
    }

    public void run() {

        boolean finished = false;
        String action;

        while(!finished) {

            if(!logged) {

                System.out.println("1. Create an account");
                System.out.println("2. Log into account");
                System.out.println("0. Exit");

                action = scanner.nextLine();
                System.out.println();

                switch (action) {
                    case "1":
                        createAccount();
                        break;
                    case "2":
                        logIntoAccount();
                        break;
                    case "0":
                        finished = true;
                        break;
                    default:
                        break;
                }
            } else {

                System.out.println("1. Balance");
                System.out.println("2. Add income");
                System.out.println("3. Do transfer");
                System.out.println("4. Close account");
                System.out.println("5. Log out");
                System.out.println("0. Exit");

                action = scanner.nextLine();
                System.out.println();

                switch (action) {
                    case "1":
                        System.out.println("Balance: "+cardLogged.getBalance()+"\n");
                        break;
                    case "2":
                        addIncome();
                        break;
                    case "3":
                        doTransfer();
                        break;
                    case "4":
                        closeAccount();
                        break;
                    case "5":
                        logOut();
                        break;
                    case "0":
                        finished = true;
                        break;
                    default:
                        break;
                }
            }
        }
        System.out.println("Bye!");

        close();
    }

    private void createAccount() {

        boolean valid = false;
        Card card = null;

        while(!valid) {
            card = new Card();
            if(!cards.contains(card)){
                valid = true;
            }
        }
        cards.add(card);
        System.out.println("Your card has been created");
        System.out.println(card);

        insertCardIntoDB(card);
    }

    private void logIntoAccount() {

        System.out.println("Enter your card number:");
        String cardNumber = scanner.nextLine();

        System.out.println("Enter your PIN:");
        String pinCode = scanner.nextLine();

        System.out.println();

        for (Card c : cards) {
            if(c.getCardNumber().equals(cardNumber) && c.getPinCode().equals(pinCode)) {

                System.out.println("You have successfully logged in!\n");

                cardLogged = c;
                logged = true;
            }
        }
        System.out.println("Wrong card number or PIN!\n");
    }

    private void logOut() {

        System.out.println("You have successfully logged out!\n");
        cardLogged = null;
        logged = false;
    }

    private void addIncome() {

        System.out.println("Enter income:");

        int income = Integer.parseInt(scanner.nextLine());

        cardLogged.setBalance(income);

        System.out.println("Income was added!");
    }

    private void doTransfer() {

        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String cardNumber = scanner.nextLine();

        Card card = null;

        if(verifyCheckSum(cardNumber)) {

            for (Card c : cards) {
                if(c.getCardNumber().equals(cardNumber)) {
                    card = c;
                }
            }

            if(card != null) {

                System.out.println("Enter how much money you want to transfer:");
                int money = Integer.parseInt(scanner.nextLine());

                if(money <= cardLogged.getBalance()) {
                    cardLogged.setBalance(cardLogged.getBalance()-money);
                    card.setBalance(money);
                    System.out.println("Success!");
                } else {
                    System.out.println("Not enough money!");
                }
            } else {
                System.out.println("Such a card does not exist.");
            }
        } else {
            System.out.println("Probably you made mistake in the card number. Please try again!");
        }
        System.out.println();
    }

    private void closeAccount() {

        deleteFromDB(cardLogged);
        cardLogged = null;
        logged = false;
    }

    private boolean verifyCheckSum(String cardNumber) {

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

        if(10 - (sum % 10) == Integer.parseInt(chars[15]+"")) {
            return true;
        } else {
            return false;
        }
    }

    private void insertCardIntoDB(Card card) {

        String query = "INSERT INTO card VALUES(?,?,?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){

            //preparedStatement.setInt(1,0);
            preparedStatement.setString(2, card.getCardNumber());
            preparedStatement.setString(3, card.getPinCode());
            preparedStatement.setInt(4, card.getBalance());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            System.out.println(e.getMessage());
        }
    }

    private void getCardsFromDB() {

        String query = "SELECT * FROM card";

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

            while(resultSet.next()) {

                Card card = new Card(resultSet.getString("number"), resultSet.getString("pin"), resultSet.getInt("balance"));

                cards.add(card);
            }

        } catch (SQLException e) {

            System.out.println(e.getMessage()+"1");
        }
    }

    private void deleteFromDB(Card card) {

        String query = "DELETE FROM card WHERE number = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, card.getCardNumber());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close() {

        try {
            if(connection != null) {
                connection.close();
            }
            if(scanner != null) {
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
