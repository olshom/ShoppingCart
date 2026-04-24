package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestLocalizationService {

    @Test
    void testConstructorSetsLanguage() throws SQLException {
        LocalizationService service = new LocalizationService("English");
        assertEquals("English", service.getSelectedLanguage());
    }

    @Test
    void testGetTranslationReturnsValue() throws SQLException {
        LocalizationService service = new LocalizationService("English");
        assertNotNull(service.getTranslation("selectLanguage"));
    }

    @Test
    void testGetTranslationReturnsNullForUnknownKey() throws SQLException {
        LocalizationService service = new LocalizationService("English");
        assertNull(service.getTranslation("nonExistentKey"));
    }

    @Test
    void testGetTranslationsReturnsNonEmptyMap() throws SQLException {
        LocalizationService service = new LocalizationService("English");
        Map<String, String> translations = service.getTranslations("English");
        assertFalse(translations.isEmpty());
    }

    @Test
    void testSwitchLanguageUpdatesSelectedLanguage() throws SQLException {
        LocalizationService service = new LocalizationService("English");
        service.getTranslations("Finnish");
        assertEquals("Finnish", service.getSelectedLanguage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"English", "Finnish", "Swedish", "Japanese", "Arabic"})
    void testAllLanguagesLoadTranslations(String language) throws SQLException {
        LocalizationService service = new LocalizationService(language);
        assertEquals(language, service.getSelectedLanguage());
        assertFalse(service.getTranslations(language).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"English", "Finnish", "Swedish", "Japanese", "Arabic"})
    void testAllLanguagesHaveRequiredKeys(String language) throws SQLException {
        LocalizationService service = new LocalizationService(language);
        assertNotNull(service.getTranslation("selectLanguage"));
        assertNotNull(service.getTranslation("confirmLanguage"));
        assertNotNull(service.getTranslation("prompt1"));
        assertNotNull(service.getTranslation("enterItems"));
        assertNotNull(service.getTranslation("calculateTotal"));
        assertNotNull(service.getTranslation("totalLabel"));
        assertNotNull(service.getTranslation("priceLabel"));
        assertNotNull(service.getTranslation("quantityLabel"));
    }
}