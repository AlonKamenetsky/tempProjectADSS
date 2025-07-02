package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.SupplyContractDTO;
import SuppliersModule.DataLayer.DTO.SupplyContractProductDataDTO;
import TransportationSuppliers.data.Util.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteSupplyContractDAO {

    public List<SupplyContractDTO> findAll() throws SQLException {
        String sql = "SELECT id, supplier_id FROM supply_contracts";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<SupplyContractDTO> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
            return list;
        }
    }

    public Optional<SupplyContractDTO> findById(int contractId) throws SQLException {
        String sql = "SELECT id, supplier_id FROM supply_contracts WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToDTO(rs)) : Optional.empty();
            }
        }
    }

    public Optional<SupplyContractDTO> findBySupplierId(int supplierId) throws SQLException {
        String sql = "SELECT id, supplier_id FROM supply_contracts WHERE supplier_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToDTO(rs)) : Optional.empty();
            }
        }
    }

    public SupplyContractDTO insert(SupplyContractDTO dto) throws SQLException {
        if (dto.supplyContractID() == null) {
            String sql = "INSERT INTO supply_contracts(supplier_id) VALUES (?)";
            try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, dto.supplierID());
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    keys.next();
                    return new SupplyContractDTO(keys.getInt(1), dto.supplierID());
                }
            }
        } else {
            String sql = "UPDATE supply_contracts SET supplier_id = ? WHERE id = ?";
            try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
                stmt.setInt(1, dto.supplierID());
                stmt.setInt(2, dto.supplyContractID());
                stmt.executeUpdate();
                return dto;
            }
        }
    }

    public void delete(int contractId) throws SQLException {
        String sql = "DELETE FROM supply_contracts WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            stmt.executeUpdate();
        }
    }

    public void deleteAllBySupplierId(int supplierID) throws SQLException {
        String sql = "DELETE FROM supply_contracts WHERE supplier_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, supplierID);
            stmt.executeUpdate();
        }
    }

    public int getSupplyContractProductDataByContractId(int contractID) {
        String sql = "SELECT COUNT(*) FROM supply_contract_product_data WHERE contract_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contractID);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<SupplyContractDTO> getContractsBySupplierId(int supplierId) {
        String sql = "SELECT id, supplier_id FROM supply_contracts WHERE supplier_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<SupplyContractDTO> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<SupplyContractDTO> getAllSupplyContracts() {
        try {
            return findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public int insertSupplyContract(SupplyContractDTO dto) throws SQLException {
        String sql = "INSERT INTO supply_contracts(supplier_id) VALUES (?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, dto.supplierID());
            stmt.executeUpdate();

            try (Statement idStmt = Database.getConnection().createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    return rs.getInt(1); // Return the generated contract ID
                } else {
                    throw new SQLException("Failed to retrieve inserted contract ID.");

                }
            }
        }
    }


    public void addProductToContract(int contractId, SupplyContractProductDataDTO dto) {
        String sql = """
            INSERT INTO supply_contract_product_data(contract_id, product_id, product_price, quantity_for_discount, discount_percentage)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, dto.supplyContractID());
            stmt.setInt(2, dto.productID());
            stmt.setDouble(3, dto.productPrice());
            stmt.setInt(4, dto.quantityForDiscount());
            stmt.setDouble(5, dto.discountPercentage());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SupplyContractProductDataDTO> getByContractId(Integer contractId) {
        String sql = "SELECT * FROM supply_contract_product_data WHERE contract_id = ?";
        List<SupplyContractProductDataDTO> list = new ArrayList<>();
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new SupplyContractProductDataDTO(
                            rs.getInt("contract_id"),
                            rs.getInt("product_id"),
                            rs.getDouble("product_price"),
                            rs.getInt("quantity_for_discount"),
                            rs.getDouble("discount_percentage")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private SupplyContractDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new SupplyContractDTO(
                rs.getInt("id"),
                rs.getInt("supplier_id")
        );
    }
}
