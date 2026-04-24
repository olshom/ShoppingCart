package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class TestShoppingCart {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @ParameterizedTest
    @CsvSource({
        "'5',     '2',     10",
        "'5:10',  '2:3',   40",
        "'100',   '0',     0",
        "'0',     '5',     0",
        "'',      '',      0"
    })
    void testCalculateTotal(String priceStr, String quantityStr, int expected) {
        int[] prices = parseIntArray(priceStr);
        int[] quantities = parseIntArray(quantityStr);
        assertEquals(expected, ShoppingCart.calculateTotal(prices, quantities));
    }

    @ParameterizedTest
    @CsvSource({
        "1, en, US",
        "2, fi, FI",
        "3, sv, SE",
        "4, ja, JP",
        "5, ar, AR",
        "0, en, US",
        "99, en, US",
        "-1, en, US"
    })
    void testGetLocale(int choice, String language, String country) {
        Locale locale = ShoppingCart.getLocale(choice);
        assertEquals(language, locale.getLanguage());
        assertEquals(country, locale.getCountry());
    }

    @ParameterizedTest
    @CsvSource({
        "en, US",
        "fi, FI",
        "sv, SE",
        "ja, JP",
        "ar, AR"
    })
    void testResourceBundleLoadsAllKeys(String language, String country) {
        Locale locale = new Locale(language, country);
        ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", locale);
        assertNotNull(bundle.getString("prompt1"));
        assertNotNull(bundle.getString("prompt2"));
        assertNotNull(bundle.getString("prompt3"));
        assertNotNull(bundle.getString("prompt4"));
    }

    @ParameterizedTest
    @CsvSource({
        "1, '2\n10\n3\n20\n4', 110",
        "2, '1\n15\n3',        45",
        "3, '1\n7\n5',         35",
        "4, '1\n8\n2',         16"
    })
    void testMainWithValidChoice(int choice, String itemsInput, int expectedTotal) {
        String input = choice + "\n" + itemsInput + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        ShoppingCart.main(new String[]{});

        String result = output.toString();
        assertTrue(result.contains("Select a language"));
        assertTrue(result.contains(String.valueOf(expectedTotal)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"9\n1\n5\n2", "0\n1\n10\n1", "-1\n1\n10\n1"})
    void testMainWithInvalidChoiceDefaultsToEnglish(String input) {
        System.setIn(new ByteArrayInputStream((input + "\n").getBytes()));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        ShoppingCart.main(new String[]{});

        String result = output.toString();
        assertTrue(result.contains("Invalid choice. Defaulting to English."));
    }

    private int[] parseIntArray(String str) {
        if (str == null || str.isEmpty()) {
            return new int[]{};
        }
        String[] parts = str.split(":");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }
}