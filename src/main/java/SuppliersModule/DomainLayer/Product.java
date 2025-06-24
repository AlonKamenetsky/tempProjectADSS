package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.DTO.ProductDTO;
import SuppliersModule.DomainLayer.Enums.ProductCategory;

public class Product {
    int productId;
    String productName;
    String productCompanyName;
    ProductCategory productCategory;


    public Product(int productId, String productName, String productCompanyName, ProductCategory productCategory) {
        this.productId = productId;
        this.productName = productName;
        this.productCompanyName = productCompanyName;
        this.productCategory = productCategory;


    }

    public Product(ProductDTO dto) {
        this.productId = dto.productId();
        this.productName = dto.productName();
        this.productCompanyName = dto.productCompanyName();
        this.productCategory = ProductCategory.valueOf(dto.productCategory());

    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;

    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;

    }

    public String getProductCompanyName() {
        return productCompanyName;
    }

    public void setProductCompanyName(String productCompanyName) {
        this.productCompanyName = productCompanyName;

    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;

    }

    public String toString() {
        return this.productId + "\t" + this.productName + "\t" + this.productCompanyName + "\t" + this.productCategory;
    }
}
