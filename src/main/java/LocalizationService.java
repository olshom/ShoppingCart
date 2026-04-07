import java.sql.*;
import java.util.HashMap;

public class LocalizationService {
    private String selectedLanguage;
    private HashMap<String, String> translations;

    public LocalizationService(String language) {
        this.selectedLanguage = language;
        this.translations = new HashMap<String, String>();
        this.translations = getTranslations(language);
    }

    public String getTranslation(String key){
        return translations.get(key);
    }

    public HashMap<String, String> getTranslations (String language){
        this.selectedLanguage = language;
        String sql = "SELECT `key`, value FROM localization_strings WHERE language = ?";

        try (Connection conn = SqlConnectionFactory.createConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, language);
            ResultSet result = ps.executeQuery();
            while (result.next()){
                translations.put(result.getString("key"), result.getString("value"));
            }
            return translations;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSelectedLanguage() {
        return this.selectedLanguage;
    }
}
