package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteSupplyContractProductDataDAO;
import SuppliersModule.DataLayer.DTO.SupplyContractProductDataDTO;

import java.sql.SQLException;
import java.util.List;

public class SupplyContractProductDataRepositoryImpl implements ISupplyContractProductDataRepository {
    private final SqliteSupplyContractProductDataDAO dao = new SqliteSupplyContractProductDataDAO();

    @Override
    public List<SupplyContractProductDataDTO> findByContractId(int contractId) throws SQLException {
        return dao.findByContractId(contractId);
    }

    @Override
    public void insert(SupplyContractProductDataDTO dto) throws SQLException {
        dao.insert(dto);
    }

    @Override
    public void deleteAllByContractId(int contractId) throws SQLException {
        dao.deleteAllByContractId(contractId);
    }

    @Override
    public List<SupplyContractProductDataDTO> findAll() throws SQLException {
        return dao.findAll();
    }

}

