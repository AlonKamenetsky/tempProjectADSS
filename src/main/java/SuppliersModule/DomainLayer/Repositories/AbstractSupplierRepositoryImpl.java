package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteSupplierDAO;
import SuppliersModule.DataLayer.DTO.SupplierDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class AbstractSupplierRepositoryImpl implements ISupplierRepositoryBase {

    protected final SqliteSupplierDAO dao = new SqliteSupplierDAO();

    protected abstract String getSupplyMethod(); // "SCHEDULED" or "ON_DEMAND"

    @Override
    public SupplierDTO addSupplier(SupplierDTO supplier) throws SQLException {
        if (!supplier.supplyMethod().equalsIgnoreCase(getSupplyMethod())) {
            throw new IllegalArgumentException("Wrong supply method for this repository");
        }
        return dao.insert(supplier);
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() throws SQLException {
        return dao.findBySupplyMethod(getSupplyMethod());
    }

    @Override
    public Optional<SupplierDTO> getSupplierById(int id) throws SQLException {
        return dao.findById(id).filter(s -> s.supplyMethod().equalsIgnoreCase(getSupplyMethod()));
    }

    @Override
    public void updateSupplier(SupplierDTO supplier) throws SQLException {
        if (!supplier.supplyMethod().equalsIgnoreCase(getSupplyMethod())) {
            throw new IllegalArgumentException("Wrong supply method for this repository");
        }
        dao.insert(supplier); // insert() handles update if ID is not null
    }

    @Override
    public void deleteSupplier(int id) throws SQLException {
        dao.delete(id);
    }
}
