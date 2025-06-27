package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteOrderProductDataDAO;
import SuppliersModule.DataLayer.DTO.OrderProductDataDTO;
import SuppliersModule.DataLayer.DTO.SupplyContractProductDataDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderProductDataRepositoryImpl implements IOrderProductDataRepository {

    private final SqliteOrderProductDataDAO dao;

    public OrderProductDataRepositoryImpl() {
        this.dao = new SqliteOrderProductDataDAO();
    }

    @Override
    public void addProductToOrder(int orderId, int productId, int quantity, double price) throws SQLException {
        dao.insert(new OrderProductDataDTO(orderId, productId, quantity, price));
    }

    @Override
    public void updateProductInOrder(OrderProductDataDTO dto) throws SQLException {
        dao.update(dto);
    }

    @Override
    public void removeProductFromOrder(int orderId, int productId) throws SQLException {
        dao.delete(orderId, productId);
    }

    @Override
    public List<OrderProductDataDTO> getProductsInOrder(int orderId) throws SQLException {
        return dao.findByOrderId(orderId);
    }

    @Override
    public Optional<OrderProductDataDTO> getProductInOrder(int orderId, int productId) throws SQLException {
        return dao.findByOrderAndProduct(orderId, productId);
    }

    @Override
    public List<OrderProductDataDTO> findByOrderId(int orderID) {
        return List.of();
    }

    @Override
    public void deleteAllByOrderId(int orderID) {

    }

    @Override
    public void insert(OrderProductDataDTO dto) {

    }

    @Override
    public List<SupplyContractProductDataDTO> findByContractId(Integer integer) {
        return List.of();
    }

    @Override
    public List<SupplyContractProductDataDTO> findContractProductsByContractId(Integer integer) {
        return List.of();
    }
}
