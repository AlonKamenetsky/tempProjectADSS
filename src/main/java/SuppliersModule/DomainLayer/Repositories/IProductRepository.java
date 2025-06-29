package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DTO.ProductDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IProductRepository {
    ProductDTO addProduct(String name, String companyName, String category,double weight) throws SQLException;

    List<ProductDTO> getAllProducts() throws SQLException;

    Optional<ProductDTO> getProductById(int productId) throws SQLException;

    Optional<ProductDTO> getProductByName(String productName) throws SQLException;

    void updateProduct(ProductDTO product) throws SQLException;

    void deleteProduct(int productId) throws SQLException;

}
