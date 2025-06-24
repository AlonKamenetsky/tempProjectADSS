package TransportationSuppliers.Integration;


import SuppliersModule.DataLayer.DTO.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface SupplierInterface {

//    static ServiceController GetInstance() {
//        return null;
//    }

    // What about Add product?

    List<ProductDTO> getAllProducts();

    Optional<ProductDTO> getProductById(int productId);

    Optional<Float> getWeightByProductId(int productId);

    Optional<ProductDTO> getProductByName(String productName);

    // with this method inventory module will ask supplier module to order a single product that is out of stock
    void placeUrgentOrderSingleProduct(int ItemID, int quantity);
}