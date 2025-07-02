package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.SupplierDTO;
import TransportationSuppliers.data.Util.Database;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteSupplierDAO {

    public List<SupplierDTO> findAllScheduled() throws SQLException {
        return findBySupplyMethod("SCHEDULED");
    }

    public List<SupplierDTO> findAllOnDemand() throws SQLException {
        return findBySupplyMethod("ON_DEMAND");
    }

    public List<SupplierDTO> findBySupplyMethod(String method) throws SQLException {
        String sql = "SELECT * FROM suppliers WHERE supply_method = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setString(1, method);
            try (ResultSet rs = stmt.executeQuery()) {
                List<SupplierDTO> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
                return list;
            }
            catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }


    public Optional<SupplierDTO> findById(int supplierId) throws SQLException {
        String sql = "SELECT * FROM suppliers WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToDTO(rs)) : Optional.empty();
            }
            catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public SupplierDTO insert(SupplierDTO dto) throws SQLException {
        if (dto.supplierID() == -1) {
            String sql = """
                INSERT INTO suppliers(name, product_category, delivering_method,
                                      contact_name, phone_number, address, email,
                                      bank_account, payment_method, supply_method)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

                stmt.executeUpdate();

                // Get the generated ID using SQLite-specific syntax
                try (Statement idStmt = Database.getConnection().createStatement();
                     ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        return new SupplierDTO(
                                rs.getInt(1),
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
                    } else {
                        throw new SQLException("Failed to retrieve inserted supplier ID.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Supplier insertion failed", e);
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
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Supplier update failed", e);
            }
        }
    }



    public void delete(int supplierId) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
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
