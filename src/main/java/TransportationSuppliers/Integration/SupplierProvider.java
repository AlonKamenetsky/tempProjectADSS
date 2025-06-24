package TransportationSuppliers.Integration;

import SuppliersModule.DataLayer.DTO.ProductDTO;

import java.util.List;
import java.util.Optional;

public class SupplierProvider implements SupplierInterface{
    @Override
    public List<ProductDTO> getAllProducts() {
        return List.of();
    }

    @Override
    public Optional<ProductDTO> getProductById(int productId) {
        return Optional.empty();
    }

    @Override
    public Optional<Float> getWeightByProductId(int productId) {
        return Optional.empty();
    }

    @Override
    public Optional<ProductDTO> getProductByName(String productName) {
        return Optional.empty();
    }

    @Override
    public void placeUrgentOrderSingleProduct(int ItemID, int quantity) {

    }
}
