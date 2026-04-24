package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TestCartService {

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService();
    }

    @Test
    void testSaveCartReturnsGeneratedId() throws SQLException {
        int cartId = cartService.saveCart(3, 100, "English");
        assertTrue(cartId > 0);
    }

    @Test
    void testSaveCartItem() throws SQLException {
        int cartId = cartService.saveCart(1, 50, "English");
        assertDoesNotThrow(() -> cartService.saveCartItem(cartId, 1, 25, 2));
    }

    @Test
    void testSaveCartItemStoresCorrectData() throws SQLException {
        int cartId = cartService.saveCart(1, 50, "English");
        cartService.saveCartItem(cartId, 1, 25, 2);

        try (Connection conn = SqlConnectionFactory.createConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT price, quantity, subtotal FROM cart_items WHERE cart_record_id = ? AND item_number = ?")) {
            ps.setInt(1, cartId);
            ps.setInt(2, 1);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(25, rs.getInt("price"));
                assertEquals(2, rs.getInt("quantity"));
                assertEquals(50.0, rs.getDouble("subtotal"));
            }
        }
    }

    @Test
    void testSaveFullCart() {
        int[] prices = {10, 20};
        int[] quantities = {2, 3};
        assertDoesNotThrow(() -> cartService.saveFullCart(2, 80, "Finnish", prices, quantities));
    }

    @Test
    void testSaveFullCartStoresAllItems() throws SQLException {
        int[] prices = {10, 20, 30};
        int[] quantities = {1, 2, 3};
        int cartId = cartService.saveCart(3, 140, "English");

        for (int i = 0; i < prices.length; i++) {
            cartService.saveCartItem(cartId, i + 1, prices[i], quantities[i]);
        }

        try (Connection conn = SqlConnectionFactory.createConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) AS cnt FROM cart_items WHERE cart_record_id = ?")) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(3, rs.getInt("cnt"));
            }
        }
    }

    @Test
    void testSaveCartStoresCorrectData() throws SQLException {
        int cartId = cartService.saveCart(5, 200, "Swedish");

        try (Connection conn = SqlConnectionFactory.createConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT total_items, total_cost, language FROM cart_records WHERE id = ?")) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(5, rs.getInt("total_items"));
                assertEquals(200, rs.getInt("total_cost"));
                assertEquals("Swedish", rs.getString("language"));
            }
        }
    }
}