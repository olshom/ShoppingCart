import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

public class TestShoppingCart {

    @Test
    void testSingleItem() {
        assertEquals(10, ShoppingCart.calculateTotal(new int[]{5}, new int[]{2}));
    }

    @Test
    void testMultipleItems() {
        assertEquals(40, ShoppingCart.calculateTotal(new int[]{5, 10}, new int[]{2, 3}));
    }

    @Test
    void testZeroQuantity() {
        assertEquals(0, ShoppingCart.calculateTotal(new int[]{100}, new int[]{0}));
    }

    @Test
    void testZeroPrice() {
        assertEquals(0, ShoppingCart.calculateTotal(new int[]{0}, new int[]{5}));
    }

    @Test
    void testEmptyCart() {
        assertEquals(0, ShoppingCart.calculateTotal(new int[]{}, new int[]{}));
    }

    @ParameterizedTest
    @CsvSource({
        "1, en, US",
        "2, fi, FI",
        "3, sv, SE",
        "4, ja, JP",
        "5, ar, AR"
    })
    void testValidLanguageChoices(int choice, String language, String country) {
        Locale locale = ShoppingCart.getLocale(choice);
        assertEquals(language, locale.getLanguage());
        assertEquals(country, locale.getCountry());
    }

    @Test
    void testInvalidChoiceDefaultsToEnglish() {
        Locale locale = ShoppingCart.getLocale(0);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
    }

    @Test
    void testOutOfRangeChoiceDefaultsToEnglish() {
        Locale locale = ShoppingCart.getLocale(99);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
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
}