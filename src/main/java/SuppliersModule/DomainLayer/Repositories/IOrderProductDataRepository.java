package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DTO.OrderProductDataDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IOrderProductDataRepository {
    void addProductToOrder(int orderId, int productId, int quantity, int price) throws SQLException;

    void updateProductInOrder(OrderProductDataDTO dto) throws SQLException;

    void removeProductFromOrder(int orderId, int productId) throws SQLException;

    List<OrderProductDataDTO> getProductsInOrder(int orderId) throws SQLException;

    Optional<OrderProductDataDTO> getProductInOrder(int orderId, int productId) throws SQLException;
}
