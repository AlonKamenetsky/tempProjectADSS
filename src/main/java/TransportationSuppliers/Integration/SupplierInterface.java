package TransportationSuppliers.Integration;


import SuppliersModule.DataLayer.DTO.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface SupplierInterface {

    List<ProductDTO> getAllProducts();

    ProductDTO getProductById(int productId);

    Double getWeightByProductId(int productId);

    Optional<ProductDTO> getProductByName(String productName);

    // with this method inventory module will ask supplier module to order a single product that is out of stock
    //void placeUrgentOrderSingleProduct(int ItemID, int quantity);
}