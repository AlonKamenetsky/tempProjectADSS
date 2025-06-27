package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.SupplyContractProductDataDTO;
import SuppliersModule.util.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteSupplyContractProductDataDAO {

    public List<SupplyContractProductDataDTO> findAll() throws SQLException {
        String sql = "SELECT * FROM supply_contract_product_data";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<SupplyContractProductDataDTO> list = new ArrayList<>();
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

    public List<SupplyContractProductDataDTO> findByContractId(int contractId) throws SQLException {
        String sql = "SELECT * FROM supply_contract_product_data WHERE contract_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<SupplyContractProductDataDTO> list = new ArrayList<>();
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

    public Optional<SupplyContractProductDataDTO> findByContractAndProduct(int contractId, int productId) throws SQLException {
        String sql = "SELECT * FROM supply_contract_product_data WHERE contract_id = ? AND product_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            stmt.setInt(2, productId);
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

    public void insert(SupplyContractProductDataDTO dto) throws SQLException {
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
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public void update(SupplyContractProductDataDTO dto) throws SQLException {
        String sql = """
                UPDATE supply_contract_product_data
                SET product_price = ?, quantity_for_discount = ?, discount_percentage = ?
                WHERE contract_id = ? AND product_id = ?
                """;
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, dto.productPrice());
            stmt.setInt(2, dto.quantityForDiscount());
            stmt.setDouble(3, dto.discountPercentage());
            stmt.setInt(4, dto.supplyContractID());
            stmt.setInt(5, dto.productID());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public void delete(int contractId, int productId) throws SQLException {
        String sql = "DELETE FROM supply_contract_product_data WHERE contract_id = ? AND product_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public void deleteAllByContractId(int contractId) throws SQLException {
        String sql = "DELETE FROM supply_contract_product_data WHERE contract_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    private SupplyContractProductDataDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new SupplyContractProductDataDTO(
                rs.getInt("contract_id"),
                rs.getInt("product_id"),
                rs.getDouble("product_price"),
                rs.getInt("quantity_for_discount"),
                rs.getDouble("discount_percentage")
        );
    }
}
