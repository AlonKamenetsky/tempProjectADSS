package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteSupplyContractProductDataDAO;
import SuppliersModule.DataLayer.DTO.SupplyContractProductDataDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ScheduledOrderRepositoryImpl implements IScheduledOrderRepository {

    private final SqliteSupplyContractProductDataDAO dao;

    public ScheduledOrderRepositoryImpl() {
        this.dao = new SqliteSupplyContractProductDataDAO();
    }

    @Override
    public void addProductToContract(int contractId, int productId, double price, int quantityForDiscount, double discountPercentage) throws SQLException {
        dao.insert(new SupplyContractProductDataDTO(contractId, productId, price, quantityForDiscount, discountPercentage));
    }

    @Override
    public void updateProductInContract(SupplyContractProductDataDTO dto) throws SQLException {
        dao.update(dto);
    }

    @Override
    public void removeProductFromContract(int contractId, int productId) throws SQLException {
        dao.delete(contractId, productId);
    }

    @Override
    public void clearContract(int contractId) throws SQLException {
        dao.deleteAllByContractId(contractId);
    }

    @Override
    public List<SupplyContractProductDataDTO> getProductsInContract(int contractId) throws SQLException {
        return dao.findByContractId(contractId);
    }

    @Override
    public Optional<SupplyContractProductDataDTO> getProductInContract(int contractId, int productId) throws SQLException {
        return dao.findByContractAndProduct(contractId, productId);
    }
}
