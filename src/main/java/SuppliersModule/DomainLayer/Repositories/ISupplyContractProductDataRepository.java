package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DTO.SupplyContractProductDataDTO;

import java.sql.SQLException;
import java.util.List;

public interface ISupplyContractProductDataRepository {
    List<SupplyContractProductDataDTO> findByContractId(int contractId) throws SQLException;
    void insert(SupplyContractProductDataDTO dto) throws SQLException;
    void deleteAllByContractId(int contractId) throws SQLException;
}
