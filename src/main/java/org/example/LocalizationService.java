package org.example;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class LocalizationService {
    private String selectedLanguage;
    private Map<String, String> translations;

    public LocalizationService(String language) throws SQLException {
        this.selectedLanguage = language;
        this.translations = new HashMap<>();
        this.translations = getTranslations(language);
    }

    public String getTranslation(String key){
        return translations.get(key);
    }

    public Map<String, String> getTranslations(String language) throws SQLException {
        this.selectedLanguage = language;
        String sql = "SELECT `key`, value FROM localization_strings WHERE language = ?";
        translations.clear();
        try (Connection conn = SqlConnectionFactory.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, language);
            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    translations.put(result.getString("key"), result.getString("value"));
                }
            }
            return translations;
        }
    }

    public String getSelectedLanguage() {
        return this.selectedLanguage;
    }
}
