package SuppliersModule.DataLayer.DAO;

import SuppliersModule.DataLayer.DTO.ProductDTO;
import SuppliersModule.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteProductDAO {

    public List<ProductDTO> findAll() throws SQLException {
        String sql = "SELECT id, name, company_name, product_category FROM products";
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<ProductDTO> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
            return list;
        }
    }

    public Optional<ProductDTO> findById(int productId) throws SQLException {
        String sql = "SELECT id, name, company_name, product_category FROM products WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToDTO(rs)) : Optional.empty();
            }
        }
    }

    public Optional<ProductDTO> findByName(String productName) throws SQLException {
        String sql = "SELECT id, name, company_name, product_category FROM products WHERE name = ? COLLATE NOCASE";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setString(1, productName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToDTO(rs)) : Optional.empty();
            }
        }
    }

    public ProductDTO insert(ProductDTO dto) throws SQLException {
        if (dto.productId() == null) {
            String sql = "INSERT INTO products(name, company_name, product_category) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = Database.getConnection()
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, dto.productName());
                stmt.setString(2, dto.productCompanyName());
                stmt.setString(3, dto.productCategory());
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    keys.next();
                    return new ProductDTO(
                            keys.getInt(1),
                            dto.productName(),
                            dto.productCompanyName(),
                            dto.productCategory()
                    );
                }
            }
        } else {
            String sql = "UPDATE products SET name = ?, company_name = ?, product_category = ? WHERE id = ?";
            try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
                stmt.setString(1, dto.productName());
                stmt.setString(2, dto.productCompanyName());
                stmt.setString(3, dto.productCategory());
                stmt.setInt(4, dto.productId());
                stmt.executeUpdate();
                return dto;
            }
        }
    }

    public void delete(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }

    private ProductDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new ProductDTO(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("company_name"),
                rs.getString("product_category")
        );
    }
}
