package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DTO.SupplyContractProductDataDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IScheduledOrderRepository {
    void addProductToContract(int contractId, int productId, double price, int quantityForDiscount, double discountPercentage) throws SQLException;

    void updateProductInContract(SupplyContractProductDataDTO dto) throws SQLException;

    void removeProductFromContract(int contractId, int productId) throws SQLException;

    void clearContract(int contractId) throws SQLException;

    List<SupplyContractProductDataDTO> getProductsInContract(int contractId) throws SQLException;

    Optional<SupplyContractProductDataDTO> getProductInContract(int contractId, int productId) throws SQLException;
}
