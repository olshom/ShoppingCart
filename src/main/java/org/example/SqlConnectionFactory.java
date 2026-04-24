package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SqlConnectionFactory {
    private static final Properties PROPS = loadProperties();

    private static final String URL = PROPS.getProperty("DB_URL");
    private static final String USER = PROPS.getProperty("DB_USER");
    private static final String PASSWORD = PROPS.getProperty("DB_PASSWORD");

    private SqlConnectionFactory() {
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        Path envFile = Path.of(".env");
        if (Files.exists(envFile)) {
            try (InputStream is = Files.newInputStream(envFile)) {
                props.load(is);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to load .env file", e);
            }
        }
        return props;
    }

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
