package org.example;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

class TestSqlConnectionFactory {

    @Test
    void testCreateConnectionDelegatesToDriverManager() throws SQLException {
        Connection mockConn = mock(Connection.class);
        try (MockedStatic<DriverManager> mockedDM = mockStatic(DriverManager.class)) {
            mockedDM.when(() -> DriverManager.getConnection(nullable(String.class), nullable(String.class), nullable(String.class)))
                    .thenReturn(mockConn);

            Connection conn = SqlConnectionFactory.createConnection();

            assertSame(mockConn, conn);
            mockedDM.verify(() -> DriverManager.getConnection(nullable(String.class), nullable(String.class), nullable(String.class)));
        }
    }

    @Test
    void testCreateConnectionPropagatesSQLException() {
        SQLException toThrow = new SQLException("connection refused");
        try (MockedStatic<DriverManager> mockedDM = mockStatic(DriverManager.class)) {
            mockedDM.when(() -> DriverManager.getConnection(nullable(String.class), nullable(String.class), nullable(String.class)))
                    .thenThrow(toThrow);

            SQLException thrown = assertThrows(SQLException.class,
                    SqlConnectionFactory::createConnection);
            assertEquals("connection refused", thrown.getMessage());
        }
    }
}