package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DTO.SupplierDTO;
import SuppliersModule.DataLayer.DTO.SupplyContractDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ISupplierRepositoryBase {
    SupplierDTO addSupplier(SupplierDTO supplier) throws SQLException;

    List<SupplierDTO> getAllSuppliers() throws SQLException;

    Optional<SupplierDTO> getSupplierById(int id) throws SQLException;

    void updateSupplier(SupplierDTO supplier) throws SQLException;

    void deleteSupplier(int id) throws SQLException;

    List<SupplyContractDTO> getAllContracts();
}
