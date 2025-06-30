package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.ProductDTO;
import SuppliersModule.util.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteProductDAO {

    public List<ProductDTO> findAll() throws SQLException {
        String sql = "SELECT id, name, company_name, product_category, product_weight FROM products";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<ProductDTO> list = new ArrayList<>();
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

    public Optional<ProductDTO> findById(int productId) throws SQLException {
        String sql = "SELECT id, name, company_name, product_category, product_weight FROM products WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, productId);
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

    public Optional<ProductDTO> findByName(String productName) throws SQLException {
        String sql = "SELECT id, name, company_name, product_category, product_weight FROM products WHERE name = ? COLLATE NOCASE";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setString(1, productName);
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

    public ProductDTO insert(ProductDTO dto) throws SQLException {
        if (dto.productId() == null) {
            String sql = "INSERT INTO products(name, company_name, product_category, product_weight) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
                stmt.setString(1, dto.productName());
                stmt.setString(2, dto.productCompanyName());
                stmt.setString(3, dto.productCategory());
                stmt.setDouble(4, dto.productWeight());
                stmt.executeUpdate();

                // Get the generated ID using SQLite-specific syntax
                try (Statement idStmt = Database.getConnection().createStatement();
                     ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        return new ProductDTO(
                                rs.getInt(1),
                                dto.productName(),
                                dto.productCompanyName(),
                                dto.productCategory(),
                                dto.productWeight()
                        );
                    } else {
                        throw new SQLException("Failed to retrieve inserted product ID.");
                    }
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
            String sql = "UPDATE products SET name = ?, company_name = ?, product_category = ?, product_weight = ? WHERE id = ?";
            try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
                stmt.setString(1, dto.productName());
                stmt.setString(2, dto.productCompanyName());
                stmt.setString(3, dto.productCategory());
                stmt.setDouble(4, dto.productWeight());
                stmt.setInt(5, dto.productId());
                stmt.executeUpdate();
                return dto;
            }
            catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException();
            }
        }
    }


    public void delete(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    private ProductDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new ProductDTO(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("company_name"),
                rs.getString("product_category"),
                rs.getDouble("product_weight")
        );
    }

}
