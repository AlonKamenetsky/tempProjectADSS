package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.ProductController;

import java.sql.SQLException;


public class ProductService {
    ProductController productController;

    public ProductService() {
        try {
            this.productController = new ProductController();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int registerNewProduct(String productName, String productCompanyName, ProductCategory productCategory, double productWeight) {
        return this.productController.registerNewProduct(productName, productCompanyName, productCategory,  productWeight);
    }

    public boolean updateProduct(int productID, String productName, String productCompanyName, double productWeight) {
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

    public void dropData() {
        productController.dropData();
    }
}

