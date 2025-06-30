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

    @Override
    public void update(OrderDTO updatedOrder) {

    }

    @Override
    public Optional<OrderDTO> findById(int orderID) {
        return Optional.empty();
    }

    @Override
    public Optional<OrderDTO> findAll() {
        return Optional.empty();
    }

    @Override
    public void insertOrder(OrderDTO orderDTO) {
        try {
            orderDAO.insert(orderDTO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
