package SuppliersModule.DomainLayer;

import IntegrationInventoryAndSupplier.MutualProduct;
import SuppliersModule.DataLayer.DTO.ProductDTO;
import SuppliersModule.DomainLayer.Enums.ProductCategory;

import java.util.ArrayList;
import java.util.List;

public class ProductController {



    ArrayList<Product> productsArrayList;
    int numberOfProducts;

    public ProductController() {
        this.numberOfProducts = 0;

        this.productsArrayList = new ArrayList<>();



        ArrayList<ProductDTO> productsDTO = this.productControllerDTO.getAllProducts();
        for (ProductDTO productDTO : productsDTO) {
            this.productsArrayList.add(productDTO.convertDTOToEntity());
            this.numberOfProducts++;
        }
    }

    private Product getProductByID(int id) {
        for (Product product : this.productsArrayList)
            if (product.productId == id)
                return product;

        return null;
    }

    public int registerNewProduct(String productName, String productCompanyName, ProductCategory productCategory) {
        Product product = new Product(this.numberOfProducts++, productName, productCompanyName, productCategory);

        product.productDTO.Insert();

        this.productsArrayList.add(product);
        return product.getProductId();
    }

    public boolean updateProduct(int productID, String productName, String productCompanyName) {
        for (Product product : this.productsArrayList) {
            if (product.getProductId() == productID) {
                product.setProductName(productName);
                product.setProductCompanyName(productCompanyName);
                return true;
            }
        }

        return false;
    }

    public boolean deleteProduct(int productID) {
        Product product = getProductByID(productID);
        if (product == null)
            return false;

        product.productDTO.Delete();

        return this.productsArrayList.removeIf(p -> p.productId == productID);
    }

    public String[] getAllProductsAsString() {
        String[] productsAsString = new String[this.productsArrayList.size()];
        for (int i = 0; i < this.productsArrayList.size(); i++)
            productsAsString[i] = this.productsArrayList.get(i).toString();

        return productsAsString;
    }

    public String getProductAsString(int productID) {
        for (Product product : this.productsArrayList)
            if (product.getProductId() == productID)
                return product.toString();

        return null;
    }

    public ProductCategory getProductCategory(int productID) {
        for (Product product : this.productsArrayList)
            if (product.getProductId() == productID)
                return product.getProductCategory();

        return null;
    }


}
