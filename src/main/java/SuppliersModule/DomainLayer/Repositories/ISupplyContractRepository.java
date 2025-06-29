package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DTO.SupplyContractDTO;
import SuppliersModule.DataLayer.DTO.SupplyContractProductDataDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ISupplyContractRepository {
    SupplyContractDTO createContract(int supplierId) throws SQLException;

    Optional<SupplyContractDTO> getContractById(int contractId) throws SQLException;

    Optional<SupplyContractDTO> getContractBySupplierId(int supplierId) throws SQLException;

    List<SupplyContractDTO> getAllContracts() throws SQLException;

    void updateContract(SupplyContractDTO contract) throws SQLException;

    void deleteContract(int contractId) throws SQLException;

    void deleteAllBySupplierId(int supplierID);

    int getSupplyContractProductDataByContractId(int contractID);

    List<SupplyContractDTO> getContractsBySupplierId(int supplierId);

    List<SupplyContractDTO> getAllSupplyContracts();

    int insertSupplyContract(SupplyContractDTO newContract);

    void addProductToContract(int contractId, SupplyContractProductDataDTO productDataDTO);

    List<SupplyContractProductDataDTO> getByContractId(Integer integer);
}
