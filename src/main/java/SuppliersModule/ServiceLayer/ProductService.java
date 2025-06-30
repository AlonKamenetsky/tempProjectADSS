package SuppliersModule.ServiceLayer;

import SuppliersModule.DataLayer.DTO.ProductDTO;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.ProductController;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class ProductService {
    ProductController productController;

    public ProductService() {
        try {
            this.productController = new ProductController();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int registerNewProduct(String productName, String productCompanyName, ProductCategory productCategory, float productWeight) {
        return this.productController.registerNewProduct(productName, productCompanyName, productCategory,  productWeight);
    }

    public boolean updateProduct(int productID, String productName, String productCompanyName, float productWeight) {
        return this.productController.updateProduct(productID, productName, productCompanyName, productWeight);
    }

    public boolean deleteProduct(int productID) {
        return this.productController.deleteProduct(productID);
    }

    public String[] getProductsAsString() {
        return this.productController.getAllProductsAsString();
    }

    public String getProductAsString(int productID) {
        return this.productController.getProductAsString(productID);
    }

    public ProductCategory getProductCategory(int productID) {
        return this.productController.getProductCategory(productID);
    }


    public List<ProductDTO> getAllProducts(){
        return this.productController.getAllProducts();
    }

    public void dropData() {
        productController.dropData();
    }
    public ProductDTO getProduct(int productID) {
        return productController.getProductByID(productID);
    }

    public Optional<ProductDTO> getProductByName(String productName) {
        return productController.getProductByName(productName);
    }
}