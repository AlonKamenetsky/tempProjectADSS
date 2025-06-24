package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteSupplyContractDAO;
import SuppliersModule.DataLayer.DTO.SupplyContractDTO;

import java.sql.SQLException;
import java.util.Optional;

public class OnDemandSupplierRepositoryImpl implements IOnDemandSupplierRepository {

    private final SqliteSupplyContractDAO contractDAO;

    public OnDemandSupplierRepositoryImpl() {
        this.contractDAO = new SqliteSupplyContractDAO();
    }

    @Override
    public SupplyContractDTO createContract(int supplierId) throws SQLException {
        return contractDAO.insert(new SupplyContractDTO(null, supplierId));
    }

    @Override
    public Optional<SupplyContractDTO> getContractById(int contractId) throws SQLException {
        return contractDAO.findById(contractId);
    }

    @Override
    public Optional<SupplyContractDTO> getContractBySupplierId(int supplierId) throws SQLException {
        return contractDAO.findBySupplierId(supplierId);
    }

    @Override
    public void updateContract(SupplyContractDTO contract) throws SQLException {
        contractDAO.insert(contract); // insert() handles update if ID is not null
    }

    @Override
    public void deleteContract(int contractId) throws SQLException {
        contractDAO.delete(contractId);
    }
}
