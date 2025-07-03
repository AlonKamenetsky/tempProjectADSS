package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DTO.OrderProductForScheduledOrderDataDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IOrderProductForScheduledOrderDataRepository {

    boolean addProductToOrder(int orderId, int productId, int quantity, String day, String deliverySite) throws SQLException;

    Optional<Integer> getProductQuantity(int orderId, int productId) throws SQLException;

    boolean updateProductQuantity(int orderId, int productId, int newQuantity) throws SQLException;

    boolean removeProductFromOrder(int orderId, int productId) throws SQLException;

    int generateNextOrderId() throws SQLException;
    OrderProductForScheduledOrderDataDTO getByOrderIDProductID(int orderId, int productId) throws SQLException;
    List<OrderProductForScheduledOrderDataDTO> getByOrderID(int orderId) throws SQLException;
    int getNumberOfOrders() throws SQLException;
    List<OrderProductForScheduledOrderDataDTO> getAllByOrderId(int orderId) throws SQLException;


}
