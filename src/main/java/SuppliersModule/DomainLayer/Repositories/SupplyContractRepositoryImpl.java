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

    }

    @Override
    public int getSupplyContractProductDataByContractId(int contractID) {
        return 0;
    }

    @Override
    public List<SupplyContractDTO> getContractsBySupplierId(int supplierId) {
        return List.of();
    }

    @Override
    public List<SupplyContractDTO> getAllSupplyContracts() {
        return List.of();
    }

    @Override
    public int insertSupplyContract(SupplyContractDTO newContract) {
        return 0;
    }

    @Override
    public void addProductToContract(int contractId, SupplyContractProductDataDTO productDataDTO) {

    }

    @Override
    public List<SupplyContractProductDataDTO> getByContractId(Integer integer) {
        return List.of();
    }
}
