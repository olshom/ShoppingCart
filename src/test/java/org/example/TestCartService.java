package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TestCartService {

    private CartService cartService;
    private Connection conn;
    private PreparedStatement insertCartStmt;
    private PreparedStatement insertItemStmt;
    private ResultSet generatedKeys;
    private MockedStatic<SqlConnectionFactory> factoryMock;

    @BeforeEach
    void setUp() throws SQLException {
        cartService = new CartService();

        conn = mock(Connection.class);
        insertCartStmt = mock(PreparedStatement.class);
        insertItemStmt = mock(PreparedStatement.class);
        generatedKeys = mock(ResultSet.class);

        when(conn.prepareStatement(contains("INSERT INTO cart_records"), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(insertCartStmt);
        when(conn.prepareStatement(contains("INSERT INTO cart_items")))
                .thenReturn(insertItemStmt);

        when(insertCartStmt.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(42);

        factoryMock = mockStatic(SqlConnectionFactory.class);
        factoryMock.when(SqlConnectionFactory::createConnection).thenReturn(conn);
    }

    @AfterEach
    void tearDown() {
        factoryMock.close();
    }

    @Test
    void testSaveCartReturnsGeneratedId() throws SQLException {
        int cartId = cartService.saveCart(3, 100, "English");
        assertEquals(42, cartId);
    }

    @Test
    void testSaveCartPassesCorrectParameters() throws SQLException {
        cartService.saveCart(5, 200, "Swedish");

        verify(insertCartStmt).setInt(1, 5);
        verify(insertCartStmt).setInt(2, 200);
        verify(insertCartStmt).setString(3, "Swedish");
        verify(insertCartStmt).executeUpdate();
    }

    @Test
    void testSaveCartThrowsWhenNoGeneratedKey() throws SQLException {
        when(generatedKeys.next()).thenReturn(false);

        SQLException ex = assertThrows(SQLException.class,
                () -> cartService.saveCart(1, 10, "English"));
        assertTrue(ex.getMessage().contains("Failed to retrieve generated cart ID"));
    }

    @Test
    void testSaveCartItemPassesCorrectParameters() throws SQLException {
        cartService.saveCartItem(7, 1, 25, 2);

        verify(insertItemStmt).setInt(1, 7);
        verify(insertItemStmt).setInt(2, 1);
        verify(insertItemStmt).setInt(3, 25);
        verify(insertItemStmt).setInt(4, 2);
        verify(insertItemStmt).setDouble(5, 50.0);
        verify(insertItemStmt).executeUpdate();
    }

    @Test
    void testSaveCartItemDoesNotThrow() {
        assertDoesNotThrow(() -> cartService.saveCartItem(1, 1, 25, 2));
    }

    @Test
    void testSaveFullCartInsertsCartAndAllItems() throws SQLException {
        int[] prices = {10, 20, 30};
        int[] quantities = {1, 2, 3};

        cartService.saveFullCart(3, 140, "Finnish", prices, quantities);

        verify(insertCartStmt).setInt(1, 3);
        verify(insertCartStmt).setInt(2, 140);
        verify(insertCartStmt).setString(3, "Finnish");
        verify(insertCartStmt).executeUpdate();

        verify(insertItemStmt, times(3)).executeUpdate();
        verify(insertItemStmt).setInt(3, 10);
        verify(insertItemStmt).setInt(3, 20);
        verify(insertItemStmt).setInt(3, 30);
    }

    @Test
    void testSaveFullCartDoesNotThrow() {
        int[] prices = {10, 20};
        int[] quantities = {2, 3};
        assertDoesNotThrow(() -> cartService.saveFullCart(2, 80, "Finnish", prices, quantities));
    }

    @Test
    void testSaveCartUsesFactoryConnection() throws SQLException {
        cartService.saveCart(1, 10, "English");
        factoryMock.verify(SqlConnectionFactory::createConnection);
    }
}