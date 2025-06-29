package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.OrderProductForScheduledOrderDataDTO;
import SuppliersModule.util.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteOrderProductForScheduledOrderDataDAO {

    public boolean insert(int orderId, int productId, int productQuantity, String day) throws SQLException {
        String sql = """
            INSERT INTO order_product_for_scheduled_order_data (order_id, product_id, product_quantity, day)
            VALUES (?, ?, ?, ?);
        """;
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            stmt.setInt(3, productQuantity);
            stmt.setString(4, day);
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Integer> getQuantity(int orderId, int productId) throws SQLException {
        String sql = """
            SELECT product_quantity FROM order_product_for_scheduled_order_data
            WHERE order_id = ? AND product_id = ?;
        """;
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(rs.getInt("product_quantity")) : Optional.empty();
            }
        }
    }

    public boolean updateQuantity(int orderId, int productId, int newQuantity) throws SQLException {
        String sql = """
            UPDATE order_product_for_scheduled_order_data
            SET product_quantity = ?
            WHERE order_id = ? AND product_id = ?;
        """;
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, orderId);
            stmt.setInt(3, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int orderId, int productId) throws SQLException {
        String sql = """
            DELETE FROM order_product_for_scheduled_order_data
            WHERE order_id = ? AND product_id = ?;
        """;
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Object[]> findAll() throws SQLException {
        String sql = "SELECT order_id, product_id, product_quantity, day FROM order_product_for_scheduled_order_data;";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Object[]> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("product_quantity"),
                        rs.getString("day")
                });
            }
            return list;
        }
    }

    public int generateNextOrderId() throws SQLException {
        String sql = "SELECT MAX(order_id) FROM order_product_for_scheduled_order_data";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int maxId = rs.next() ? rs.getInt(1) : 0;
            return maxId + 1;
        }
    }
    public int getNumberOfOrders() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT order_id) AS total_orders FROM order_product_for_scheduled_order_data";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt("total_orders") : 0;
        }
    }
    public List<OrderProductForScheduledOrderDataDTO> getAllByOrderId(int orderId) throws SQLException {
        String sql = """
        SELECT order_id, product_id, product_quantity, day
        FROM order_product_for_scheduled_order_data
        WHERE order_id = ?
    """;

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<OrderProductForScheduledOrderDataDTO> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new OrderProductForScheduledOrderDataDTO(
                            rs.getInt("order_id"),
                            rs.getInt("product_id"),
                            rs.getInt("product_quantity"),
                            rs.getString("day")
                    ));
                }
                return list;
            }
        }
    }




}
