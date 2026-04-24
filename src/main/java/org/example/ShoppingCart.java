package org.example;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ShoppingCart {
    public static Locale getLocale(int choice) {
        switch (choice) {
            case 1: return new Locale("en", "US");
            case 2: return new Locale("fi", "FI");
            case 3: return new Locale("sv", "SE");
            case 4: return new Locale("ja", "JP");
            case 5: return new Locale("ar", "AR");
            default: return new Locale("en", "US");
        }
    }

    public static int calculateTotal(int[] prices, int[] quantities) {
        int result = 0;
        for (int i = 0; i < prices.length; i++) {
            result += prices[i] * quantities[i];
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("Select a language: ");
        System.out.println("1. English");
        System.out.println("2. Finnish");
        System.out.println("3. Swedish");
        System.out.println("4. Japanese");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice < 1 || choice > 4) {
            System.out.println("Invalid choice. Defaulting to English.");
        }
        Locale locale = getLocale(choice);

        ResourceBundle resourceBundle = ResourceBundle.getBundle("MessagesBundle", locale);
        System.out.println(resourceBundle.getString("prompt1"));
        int numberOfItems = scanner.nextInt();

        int[] prices = new int[numberOfItems];
        int[] quantities = new int[numberOfItems];
        for (int i = 0; i < numberOfItems; i++) {
            System.out.println(resourceBundle.getString("prompt2"));
            prices[i] = scanner.nextInt();
            System.out.println(resourceBundle.getString("prompt3"));
            quantities[i] = scanner.nextInt();
        }

        System.out.println(resourceBundle.getString("prompt4") + calculateTotal(prices, quantities));
    }
}