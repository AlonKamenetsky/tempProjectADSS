package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.OrderProductDataDTO;
import SuppliersModule.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteOrderProductDataDAO {

    public List<OrderProductDataDTO> findAll() throws SQLException {
        String sql = "SELECT order_id, product_id, quantity, price FROM order_product_data";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<OrderProductDataDTO> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
            return list;
        }
    }

    public List<OrderProductDataDTO> findByOrderId(int orderId) throws SQLException {
        String sql = "SELECT order_id, product_id, quantity, price FROM order_product_data WHERE order_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<OrderProductDataDTO> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
                return list;
            }
        }
    }

    public Optional<OrderProductDataDTO> findByOrderAndProduct(int orderId, int productId) throws SQLException {
        String sql = "SELECT order_id, product_id, quantity, price FROM order_product_data WHERE order_id = ? AND product_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToDTO(rs)) : Optional.empty();
            }
        }
    }

    public void insert(OrderProductDataDTO dto) throws SQLException {
        String sql = "INSERT INTO order_product_data(order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, dto.orderID());
            stmt.setInt(2, dto.productID());
            stmt.setInt(3, dto.productQuantity());
            stmt.setInt(4, dto.productPrice());
            stmt.executeUpdate();
        }
    }

    public void update(OrderProductDataDTO dto) throws SQLException {
        String sql = "UPDATE order_product_data SET quantity = ?, price = ? WHERE order_id = ? AND product_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, dto.productQuantity());
            stmt.setInt(2, dto.productPrice());
            stmt.setInt(3, dto.orderID());
            stmt.setInt(4, dto.productID());
            stmt.executeUpdate();
        }
    }

    public void delete(int orderId, int productId) throws SQLException {
        String sql = "DELETE FROM order_product_data WHERE order_id = ? AND product_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }

    private OrderProductDataDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new OrderProductDataDTO(
                rs.getInt("order_id"),
                rs.getInt("product_id"),
                rs.getInt("quantity"),
                rs.getInt("price")
        );
    }
}
