package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DTO.OrderDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IOrderRepository {
    OrderDTO createOrder(int supplierId, String deliveryMethod, String orderDate, String deliveryDate,
                         double totalPrice, String orderStatus, String supplyMethod) throws SQLException;

    List<OrderDTO> getAllOrders() throws SQLException;

    Optional<OrderDTO> getOrderById(int orderId) throws SQLException;

    void updateOrder(OrderDTO order) throws SQLException;

    void deleteOrder(int orderId) throws SQLException;

    void update(OrderDTO updatedOrder);

    Optional<OrderDTO> findById(int orderID);

    Optional<OrderDTO> findAll();

    void insertOrder(OrderDTO orderDTO);

}
