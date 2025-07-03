package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.OrderDTO;
import TransportationSuppliers.data.Util.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteOrderDAO {

    public List<OrderDTO> findAll() throws SQLException {
        String sql = """
                SELECT o.id AS order_id, o.supplier_id, o.delivering_method, o.order_date,
                       o.supply_date, o.total_price, o.order_status, o.supply_method,
                       s.contact_name, s.phone_number, s.address, s.email, o.delivery_site
                FROM orders o
                JOIN suppliers s ON o.supplier_id = s.id
                """;

        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<OrderDTO> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public Optional<OrderDTO> findById(int orderId) throws SQLException {
        String sql = """
                SELECT o.id AS order_id, o.supplier_id, o.delivering_method, o.order_date,
                       o.supply_date, o.total_price, o.order_status, o.supply_method,
                       s.contact_name, s.phone_number, s.address, s.email, s.delivery_site
                FROM orders o
                JOIN suppliers s ON o.supplier_id = s.id
                WHERE o.id = ?
                """;

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderId);
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

    public OrderDTO insert(OrderDTO dto) throws SQLException {
        String sql = """
                INSERT INTO orders (supplier_id, delivering_method, order_date, supply_date,
                                    total_price, order_status, supply_method, delivery_ste)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = Database.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, dto.supplierID());
            ps.setString(2, dto.deliveryMethod());
            ps.setString(3, dto.orderDate());
            ps.setString(4, dto.deliveryDate());
            ps.setDouble(5, dto.totalPrice());
            ps.setString(6, dto.orderStatus());
            ps.setString(7, dto.supplyMethod());
            ps.setString(8, dto.deliverySite());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new OrderDTO(
                        keys.getInt(1),
                        dto.supplierID(),
                        dto.phoneNumber(),
                        dto.physicalAddress(),
                        dto.emailAddress(),
                        dto.contactName(),
                        dto.deliveryMethod(),
                        dto.orderDate(),
                        dto.deliveryDate(),
                        dto.totalPrice(),
                        dto.orderStatus(),
                        dto.supplyMethod(),
                        dto.deliverySite()
                );
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

    public OrderDTO update(OrderDTO dto) throws SQLException {
        String sql = """
                UPDATE orders
                SET supplier_id = ?, delivering_method = ?, order_date = ?, supply_date = ?,
                    total_price = ?, order_status = ?, supply_method = ?, delivery_ste = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, dto.supplierID());
            ps.setString(2, dto.deliveryMethod());
            ps.setString(3, dto.orderDate());
            ps.setString(4, dto.deliveryDate());
            ps.setDouble(5, dto.totalPrice());
            ps.setString(6, dto.orderStatus());
            ps.setString(7, dto.supplyMethod());
            ps.setString(8, dto.deliverySite());
            ps.setInt(9, dto.orderID());
            ps.executeUpdate();
            return dto;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private OrderDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new OrderDTO(
                rs.getInt("order_id"),
                rs.getInt("supplier_id"),
                rs.getString("phone_number"),
                rs.getString("address"),
                rs.getString("email"),
                rs.getString("contact_name"),
                rs.getString("delivering_method"),
                rs.getString("order_date"),
                rs.getString("supply_date"),
                rs.getDouble("total_price"),
                rs.getString("order_status"),
                rs.getString("supply_method"),
                rs.getString("delivery_site")
        );
    }
    public int getNextOrderId() throws SQLException {
        String sql = "SELECT MAX(id) AS last_id FROM orders";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("last_id") + 1;
            } else {
                return 1; // No orders yet
            }
        }
    }

}
