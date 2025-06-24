package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.SupplierDaysDTO;
import SuppliersModule.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteSupplierDaysDAO {

    public List<SupplierDaysDTO> findAll() throws SQLException {
        String sql = "SELECT id, day FROM suppliers_days";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<SupplierDaysDTO> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new SupplierDaysDTO(rs.getInt("id"), rs.getString("day")));
            }
            return list;
        }
    }

    public List<SupplierDaysDTO> findBySupplierId(int supplierId) throws SQLException {
        String sql = "SELECT id, day FROM suppliers_days WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<SupplierDaysDTO> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new SupplierDaysDTO(rs.getInt("id"), rs.getString("day")));
                }
                return list;
            }
        }
    }

    public void insert(SupplierDaysDTO dto) throws SQLException {
        String sql = "INSERT INTO suppliers_days(id, day) VALUES (?, ?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, dto.supplierID());
            stmt.setString(2, dto.day());
            stmt.executeUpdate();
        }
    }

    public void delete(int supplierId, String day) throws SQLException {
        String sql = "DELETE FROM suppliers_days WHERE id = ? AND day = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            stmt.setString(2, day);
            stmt.executeUpdate();
        }
    }

    public void deleteAllBySupplierId(int supplierId) throws SQLException {
        String sql = "DELETE FROM suppliers_days WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            stmt.executeUpdate();
        }
    }
}
