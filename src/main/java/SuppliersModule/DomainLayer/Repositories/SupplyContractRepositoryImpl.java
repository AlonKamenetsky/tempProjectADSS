package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteSupplyContractDAO;
import SuppliersModule.DataLayer.DTO.SupplyContractDTO;
import SuppliersModule.DataLayer.DTO.SupplyContractProductDataDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SupplyContractRepositoryImpl implements ISupplyContractRepository {

    private final SqliteSupplyContractDAO dao;

    public SupplyContractRepositoryImpl() {
        this.dao = new SqliteSupplyContractDAO();
    }

    @Override
    public SupplyContractDTO createContract(int supplierId) throws SQLException {
        return dao.insert(new SupplyContractDTO(null, supplierId));
    }

    @Override
    public Optional<SupplyContractDTO> getContractById(int contractId) throws SQLException {
        return dao.findById(contractId);
    }

    @Override
    public Optional<SupplyContractDTO> getContractBySupplierId(int supplierId) throws SQLException {
        return dao.findBySupplierId(supplierId);
    }

    @Override
    public List<SupplyContractDTO> getAllContracts() throws SQLException {
        return dao.findAll();
    }

    @Override
    public void updateContract(SupplyContractDTO contract) throws SQLException {
        dao.insert(contract); // insert handles update if ID is not null
    }

    @Override
    public void deleteContract(int contractId) throws SQLException {
        dao.delete(contractId);
    }

    @Override
    public void deleteAllBySupplierId(int supplierID) {
        try {
            dao.deleteAllBySupplierId(supplierID);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete contracts for supplier " + supplierID, e);
        }
    }

    @Override
    public int getSupplyContractProductDataByContractId(int contractID) {
        return dao.getSupplyContractProductDataByContractId(contractID);
    }

    @Override
    public List<SupplyContractDTO> getContractsBySupplierId(int supplierId) {
        return dao.getContractsBySupplierId(supplierId);
    }

    @Override
    public List<SupplyContractDTO> getAllSupplyContracts() {
        return dao.getAllSupplyContracts();
    }

    @Override
    public int insertSupplyContract(SupplyContractDTO newContract) {
        try {
            return dao.insertSupplyContract(newContract);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addProductToContract(int contractId, SupplyContractProductDataDTO productDataDTO) {
        dao.addProductToContract(contractId, productDataDTO);
    }

    @Override
    public List<SupplyContractProductDataDTO> getByContractId(Integer contractId) {
        return dao.getByContractId(contractId);
    }
}
