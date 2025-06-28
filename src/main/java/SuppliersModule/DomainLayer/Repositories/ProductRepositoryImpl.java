package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteProductDAO;
import SuppliersModule.DataLayer.DTO.ProductDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryImpl implements IProductRepository {

    private final SqliteProductDAO productDAO;

    public ProductRepositoryImpl() {
        this.productDAO = new SqliteProductDAO();
    }

    @Override
    public ProductDTO addProduct(String name, String companyName, String category, double weight) throws SQLException {
        ProductDTO newProduct = new ProductDTO(null, name, companyName, category,weight );
        return productDAO.insert(newProduct);
    }

    @Override
    public List<ProductDTO> getAllProducts() throws SQLException {
        return productDAO.findAll();
    }

    @Override
    public Optional<ProductDTO> getProductById(int productId) throws SQLException {
        return productDAO.findById(productId);
    }

    @Override
    public Optional<ProductDTO> getProductByName(String productName) throws SQLException {
        return productDAO.findByName(productName);
    }

    @Override
    public void updateProduct(ProductDTO product) throws SQLException {
        productDAO.insert(product); // insert() handles update if ID is not null
    }

    @Override
    public void deleteProduct(int productId) throws SQLException {
        productDAO.delete(productId);
    }
}
