package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.OrderProductDataDTO;
import TransportationSuppliers.data.Util.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        catch (SQLException e) {
            e.printStackTrace();
            return null;
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
            catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
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
            catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insert(OrderProductDataDTO dto) throws SQLException {
        String sql = "INSERT INTO order_product_data(order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, dto.orderID());
            stmt.setInt(2, dto.productID());
            stmt.setInt(3, dto.productQuantity());
            stmt.setDouble(4, dto.productPrice());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public void update(OrderProductDataDTO dto) throws SQLException {
        String sql = "UPDATE order_product_data SET quantity = ?, price = ? WHERE order_id = ? AND product_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, dto.productQuantity());
            stmt.setDouble(2, dto.productPrice());
            stmt.setInt(3, dto.orderID());
            stmt.setInt(4, dto.productID());
            stmt.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public void delete(int orderId, int productId) throws SQLException {
        String sql = "DELETE FROM order_product_data WHERE order_id = ? AND product_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    private OrderProductDataDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new OrderProductDataDTO(
                rs.getInt("order_id"),
                rs.getInt("product_id"),
                rs.getInt("quantity"),
                rs.getDouble("price")
        );
    }
    public void deleteAllByOrderId(int orderId) throws SQLException {
        String sql = "DELETE FROM order_product_data WHERE order_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to delete all products from order ID " + orderId, e);
        }
    }

}
