import java.sql.*;

public class CartService {
    public int saveCart(int totalItems, int totalCost, String language) throws SQLException {
        String sql = "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";
        try (Connection conn = SqlConnectionFactory.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, totalItems);
            ps.setInt(2, totalCost);
            ps.setString(3, language);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new SQLException("Failed to retrieve generated cart ID");
        }
    }

    public void saveCartItem(int cartId, int itemNumber, int price, int quantity) throws SQLException {
        String sql = "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = SqlConnectionFactory.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, itemNumber);
            ps.setInt(3, price);
            ps.setInt(4, quantity);
            ps.setDouble(5, (double) price * quantity);
            ps.executeUpdate();
        }
    }

    public void saveFullCart(int totalItems, int totalCost, String language,
                            int[] prices, int[] quantities) throws SQLException {
        int cartId = saveCart(totalItems, totalCost, language);
        for (int i = 0; i < prices.length; i++) {
            saveCartItem(cartId, i + 1, prices[i], quantities[i]);
        }
    }
}
