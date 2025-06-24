package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteOrderDAO;
import SuppliersModule.DataLayer.DTO.OrderDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderRepositoryImpl implements IOrderRepository {

    private final SqliteOrderDAO orderDAO;

    public OrderRepositoryImpl() {
        this.orderDAO = new SqliteOrderDAO();
    }

    @Override
    public OrderDTO createOrder(int supplierId, String deliveryMethod, String orderDate, String deliveryDate,
                                double totalPrice, String orderStatus, String supplyMethod) throws SQLException {
        OrderDTO newOrder = new OrderDTO(
                null,            // orderID will be auto-generated
                supplierId,
                null,            // phoneNumber (loaded on read only)
                null,            // physicalAddress
                null,            // emailAddress
                null,            // contactName
                deliveryMethod,
                orderDate,
                deliveryDate,
                totalPrice,
                orderStatus,
                supplyMethod
        );
        return orderDAO.insert(newOrder);
    }

    @Override
    public List<OrderDTO> getAllOrders() throws SQLException {
        return orderDAO.findAll();
    }

    @Override
    public Optional<OrderDTO> getOrderById(int orderId) throws SQLException {
        return orderDAO.findById(orderId);
    }

    @Override
    public void updateOrder(OrderDTO order) throws SQLException {
        orderDAO.update(order);
    }

    @Override
    public void deleteOrder(int orderId) throws SQLException {
        orderDAO.delete(orderId);
    }
}
