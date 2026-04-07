import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnectionFactory {
    private static final String url = "jdbc:mariadb://localhost:3306/shopping_cart_localization";
    private static final String user = "root";
    private static final String password = "tikape";

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
