package Transportation.Domain;

import SuppliersModule.DataLayer.DTO.ProductDTO;

import java.util.List;
import java.util.Optional;


public interface InventoryProvider {

    List<ProductDTO> getAllProducts();

    Optional<ProductDTO> getProductById(int productId);

    Optional<Float> getWeightByProductId(int productId);

    Optional<ProductDTO> getProductByName(String productName);

}