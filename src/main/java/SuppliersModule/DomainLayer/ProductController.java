package SuppliersModule.DomainLayer;


import SuppliersModule.DataLayer.DTO.ProductDTO;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Repositories.IProductRepository;
import SuppliersModule.DomainLayer.Repositories.ProductRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

public class ProductController {

    private final IProductRepository productRepository;
    private final List<ProductDTO> productsArrayList;

    public ProductController() throws SQLException {
        this.productRepository = new ProductRepositoryImpl();
        this.productsArrayList = productRepository.getAllProducts();
    }

    private ProductDTO getProductByID(int id) {
        for (ProductDTO product : this.productsArrayList)
            if (product.productId() == id)
                return product;

        return null;
    }

    public int registerNewProduct(String productName, String productCompanyName, ProductCategory productCategory, double productWeight) {
        try {
            ProductDTO dto = productRepository.addProduct(productName, productCompanyName, productCategory.name(),  productWeight);
            this.productsArrayList.add(dto);
            return dto.productId();
        } catch (SQLException e) {
            e.printStackTrace(); // or handle it with logging / user feedback
            return -1; // signal failure
        }
    }


    public boolean updateProduct(int productID, String productName, String productCompanyName, double productWeight) {
        try {
            ProductDTO existing = getProductByID(productID);
            if (existing == null)
                return false;

            ProductDTO updated = new ProductDTO(
                    productID,
                    productName,
                    productCompanyName,
                    existing.productCategory(),
                    productWeight
            );

            productRepository.updateProduct(updated);

            // Replace in the local list
            for (int i = 0; i < productsArrayList.size(); i++) {
                if (productsArrayList.get(i).productId().equals(productID)) {
                    productsArrayList.set(i, updated);
                    break;
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace(); // or log appropriately
            return false;
        }
    }


    public boolean deleteProduct(int productID) {
        try {
            ProductDTO existing = getProductByID(productID);
            if (existing == null)
                return false;

            productRepository.deleteProduct(productID);

            // Remove from the local cache
            return productsArrayList.removeIf(p -> p.productId().equals(productID));
        } catch (SQLException e) {
            e.printStackTrace(); // or use proper logging
            return false;
        }
    }


    public String[] getAllProductsAsString() {
        String[] productsAsString = new String[this.productsArrayList.size()];
        for (int i = 0; i < this.productsArrayList.size(); i++)
            productsAsString[i] = this.productsArrayList.get(i).toString();

        return productsAsString;
    }

    public String getProductAsString(int productID) {
        for (ProductDTO product : this.productsArrayList)
            if (product.productId() == productID)
                return product.toString(); // may not work

        return null;
    }

    public ProductCategory getProductCategory(int productID) {
        for (ProductDTO product : this.productsArrayList)
            if (product.productId()== productID)
                return ProductCategory.valueOf(product.productCategory());

        return null;
    }


    public void dropData() {
        this.productsArrayList.clear();
    }
}
