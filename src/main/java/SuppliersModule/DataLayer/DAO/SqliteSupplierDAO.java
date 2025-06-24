package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.SupplierDTO;
import SuppliersModule.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteSupplierDAO {

    public List<SupplierDTO> findAll() throws SQLException {
        String sql = "SELECT * FROM suppliers";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<SupplierDTO> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
            return list;
        }
    }

    public Optional<SupplierDTO> findById(int supplierId) throws SQLException {
        String sql = "SELECT * FROM suppliers WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToDTO(rs)) : Optional.empty();
            }
        }
    }

    public SupplierDTO insert(SupplierDTO dto) throws SQLException {
        if (dto.supplierID() == null) {
            String sql = """
                    INSERT INTO suppliers(name, product_category, delivering_method,
                                          contact_name, phone_number, address, email,
                                          bank_account, payment_method, supply_method)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement stmt = Database.getConnection()
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, dto.supplierName());
                stmt.setString(2, dto.productCategory());
                stmt.setString(3, dto.deliveryMethod());
                stmt.setString(4, dto.contactName());
                stmt.setString(5, dto.phoneNumber());
                stmt.setString(6, dto.address());
                stmt.setString(7, dto.emailAddress());
                stmt.setString(8, dto.bankAccount());
                stmt.setString(9, dto.paymentMethod());
                stmt.setString(10, dto.supplyMethod());

                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    keys.next();
                    return new SupplierDTO(
                            keys.getInt(1),
                            dto.supplierName(),
                            dto.productCategory(),
                            dto.deliveryMethod(),
                            dto.contactName(),
                            dto.emailAddress(),
                            dto.phoneNumber(),
                            dto.address(),
                            dto.bankAccount(),
                            dto.paymentMethod(),
                            dto.supplyMethod()
                    );
                }
            }
        } else {
            String sql = """
                    UPDATE suppliers
                    SET name = ?, product_category = ?, delivering_method = ?,
                        contact_name = ?, phone_number = ?, address = ?, email = ?,
                        bank_account = ?, payment_method = ?, supply_method = ?
                    WHERE id = ?
                    """;
            try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
                stmt.setString(1, dto.supplierName());
                stmt.setString(2, dto.productCategory());
                stmt.setString(3, dto.deliveryMethod());
                stmt.setString(4, dto.contactName());
                stmt.setString(5, dto.phoneNumber());
                stmt.setString(6, dto.address());
                stmt.setString(7, dto.emailAddress());
                stmt.setString(8, dto.bankAccount());
                stmt.setString(9, dto.paymentMethod());
                stmt.setString(10, dto.supplyMethod());
                stmt.setInt(11, dto.supplierID());
                stmt.executeUpdate();
                return dto;
            }
        }
    }

    public void delete(int supplierId) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            stmt.executeUpdate();
        }
    }

    private SupplierDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new SupplierDTO(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("product_category"),
                rs.getString("delivering_method"),
                rs.getString("contact_name"),
                rs.getString("email"),
                rs.getString("phone_number"),
                rs.getString("address"),
                rs.getString("bank_account"),
                rs.getString("payment_method"),
                rs.getString("supply_method")
        );
    }
}
