package org.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TestSqlConnectionFactory {

    @Test
    void testCreateConnectionReturnsNonNull() throws SQLException {
        Connection conn = SqlConnectionFactory.createConnection();
        assertNotNull(conn);
        conn.close();
    }

    @Test
    void testConnectionIsValid() throws SQLException {
        try (Connection conn = SqlConnectionFactory.createConnection()) {
            assertTrue(conn.isValid(5));
        }
    }

    @Test
    void testConnectionIsNotClosed() throws SQLException {
        try (Connection conn = SqlConnectionFactory.createConnection()) {
            assertFalse(conn.isClosed());
        }
    }

    @Test
    void testConnectionUsesCorrectDatabase() throws SQLException {
        try (Connection conn = SqlConnectionFactory.createConnection()) {
            String catalog = conn.getCatalog();
            assertEquals("shopping_cart_localization", catalog);
        }
    }
}