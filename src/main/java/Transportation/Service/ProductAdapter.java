package Transportation.Service;

import SuppliersModule.DataLayer.DTO.ProductDTO;
import Transportation.Domain.ProductProvider;
import TransportationSuppliers.Integration.SupplierProvider;

import java.util.List;

public class ProductAdapter implements ProductProvider {
    private SupplierProvider supplierProvider;

    @Override
    public String getItemById(int id) {
        return supplierProvider.getProductById(id).productName();
    }

    public boolean doesItemExist(String itemName) {
        return supplierProvider.getProductByName(itemName).isPresent();
    }

    public List<ProductDTO> viewAllProducts() {
        return supplierProvider.getAllProducts();
    }
}