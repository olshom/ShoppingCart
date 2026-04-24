package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TestLocalizationService {

    private static final Map<String, String> SAMPLE_TRANSLATIONS = new LinkedHashMap<>();
    static {
        SAMPLE_TRANSLATIONS.put("selectLanguage", "Select a language");
        SAMPLE_TRANSLATIONS.put("confirmLanguage", "Confirm");
        SAMPLE_TRANSLATIONS.put("prompt1", "Enter number of items");
        SAMPLE_TRANSLATIONS.put("enterItems", "Enter items");
        SAMPLE_TRANSLATIONS.put("calculateTotal", "Calculate total");
        SAMPLE_TRANSLATIONS.put("totalLabel", "Total");
        SAMPLE_TRANSLATIONS.put("priceLabel", "Price");
        SAMPLE_TRANSLATIONS.put("quantityLabel", "Quantity");
    }

    private Connection conn;
    private PreparedStatement ps;
    private MockedStatic<SqlConnectionFactory> factoryMock;

    @BeforeEach
    void setUp() throws SQLException {
        conn = mock(Connection.class);
        ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenAnswer(invocation -> buildResultSet(SAMPLE_TRANSLATIONS));

        factoryMock = mockStatic(SqlConnectionFactory.class);
        factoryMock.when(SqlConnectionFactory::createConnection).thenReturn(conn);
    }

    @AfterEach
    void tearDown() {
        factoryMock.close();
    }

    private static ResultSet buildResultSet(Map<String, String> rows) throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        Iterator<Map.Entry<String, String>> it = rows.entrySet().iterator();
        AtomicReference<Map.Entry<String, String>> current = new AtomicReference<>();

        when(rs.next()).thenAnswer(inv -> {
            if (it.hasNext()) {
                current.set(it.next());
                return true;
            }
            return false;
        });
        when(rs.getString("key")).thenAnswer(inv -> current.get().getKey());
        when(rs.getString("value")).thenAnswer(inv -> current.get().getValue());

        return rs;
    }

    @Test
    void testConstructorSetsLanguage() throws SQLException {
        LocalizationService service = new LocalizationService("English");
        assertEquals("English", service.getSelectedLanguage());
    }

    @Test
    void testGetTranslationReturnsValue() throws SQLException {
        LocalizationService service = new LocalizationService("English");
        assertEquals("Select a language", service.getTranslation("selectLanguage"));
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
        assertEquals(SAMPLE_TRANSLATIONS.size(), translations.size());
    }

    @Test
    void testSwitchLanguageUpdatesSelectedLanguage() throws SQLException {
        LocalizationService service = new LocalizationService("English");
        service.getTranslations("Finnish");
        assertEquals("Finnish", service.getSelectedLanguage());
    }

    @Test
    void testGetTranslationsQueriesWithLanguage() throws SQLException {
        new LocalizationService("Swedish");
        verify(ps).setString(1, "Swedish");
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