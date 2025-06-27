package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.SupplyContractDTO;
import SuppliersModule.util.Database;

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
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public Optional<SupplyContractDTO> findById(int contractId) throws SQLException {
        String sql = "SELECT id, supplier_id FROM supply_contracts WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contractId);
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

    public Optional<SupplyContractDTO> findBySupplierId(int supplierId) throws SQLException {
        String sql = "SELECT id, supplier_id FROM supply_contracts WHERE supplier_id = ?";
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

    public SupplyContractDTO insert(SupplyContractDTO dto) throws SQLException {
        if (dto.supplyContractID() == null) {
            String sql = "INSERT INTO supply_contracts(supplier_id) VALUES (?)";
            try (PreparedStatement stmt = Database.getConnection()
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setInt(1, dto.supplierID());
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    keys.next();
                    return new SupplyContractDTO(keys.getInt(1), dto.supplierID());
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
        } else {
            String sql = "UPDATE supply_contracts SET supplier_id = ? WHERE id = ?";
            try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
                stmt.setInt(1, dto.supplierID());
                stmt.setInt(2, dto.supplyContractID());
                stmt.executeUpdate();
                return dto;
            }
            catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException();
            }
        }
    }

    public void delete(int contractId) throws SQLException {
        String sql = "DELETE FROM supply_contracts WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    private SupplyContractDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new SupplyContractDTO(
                rs.getInt("id"),
                rs.getInt("supplier_id")
        );
    }
}
