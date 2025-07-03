package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteOrderProductForScheduledOrderDataDAO;
import SuppliersModule.DataLayer.DTO.OrderProductForScheduledOrderDataDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderProductForScheduledOrderDataRepository implements IOrderProductForScheduledOrderDataRepository {

    private final SqliteOrderProductForScheduledOrderDataDAO dao;

    public OrderProductForScheduledOrderDataRepository() {
        this.dao = new SqliteOrderProductForScheduledOrderDataDAO();
    }

    @Override
    public boolean addProductToOrder(int orderId, int productId, int quantity, String day, String deliverySite) throws SQLException {
        return dao.insert(orderId, productId, quantity, day, deliverySite);
    }

    @Override
    public Optional<Integer> getProductQuantity(int orderId, int productId) throws SQLException {
        return dao.getQuantity(orderId, productId);
    }

    @Override
    public boolean updateProductQuantity(int orderId, int productId, int newQuantity) throws SQLException {
        return dao.updateQuantity(orderId, productId, newQuantity);
    }

    @Override
    public boolean removeProductFromOrder(int orderId, int productId) throws SQLException {
        return dao.delete(orderId, productId);
    }


    @Override
    public int generateNextOrderId() throws SQLException {
        return dao.generateNextOrderId();
    }

    @Override
    public OrderProductForScheduledOrderDataDTO getByOrderIDProductID(int orderId, int productId) throws SQLException {
        return null;
    }

    @Override
    public List<OrderProductForScheduledOrderDataDTO> getByOrderID(int orderId) throws SQLException {
        return List.of();
    }
    @Override
    public int getNumberOfOrders() throws SQLException {
        return dao.getNumberOfOrders();
    }
    @Override
    public List<OrderProductForScheduledOrderDataDTO> getAllByOrderId(int orderId) throws SQLException {
        return dao.getAllByOrderId(orderId);
    }


}
