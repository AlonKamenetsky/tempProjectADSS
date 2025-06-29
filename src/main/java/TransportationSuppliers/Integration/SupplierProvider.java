package TransportationSuppliers.Integration;

import SuppliersModule.DataLayer.DTO.ProductDTO;
import SuppliersModule.ServiceLayer.ServiceController;

import java.util.List;
import java.util.Optional;

public class SupplierProvider implements SupplierInterface{
    private final ServiceController serviceController;
    public SupplierProvider() {
        this.serviceController = ServiceController.getInstance();
    }
    @Override
    public List<ProductDTO> getAllProducts() {
        return serviceController.getAllProducts();

    }

    @Override
    public ProductDTO getProductById(int productId) {

        return serviceController.getProductById(productId);
    }

    @Override
    public Double getWeightByProductId(int productId) {
        return serviceController.getWeightByProductId(productId);
    }

    @Override
    public Optional<ProductDTO> getProductByName(String productName) {
        return serviceController.getProductByName(productName);
    }

//    @Override
//    public void placeUrgentOrderSingleProduct(int ItemID, int quantity) {
//
//    }
}
