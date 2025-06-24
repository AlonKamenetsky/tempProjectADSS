package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteSupplierDaysDAO;
import SuppliersModule.DataLayer.DTO.SupplierDaysDTO;

import java.sql.SQLException;
import java.util.List;

public class ScheduledSupplierRepositoryImpl implements IScheduledSupplierRepository {

    private final SqliteSupplierDaysDAO supplierDaysDAO;

    public ScheduledSupplierRepositoryImpl() {
        this.supplierDaysDAO = new SqliteSupplierDaysDAO();
    }

    @Override
    public void addDay(int supplierId, String day) throws SQLException {
        supplierDaysDAO.insert(new SupplierDaysDTO(supplierId, day));
    }

    @Override
    public void removeDay(int supplierId, String day) throws SQLException {
        supplierDaysDAO.delete(supplierId, day);
    }

    @Override
    public void clearSchedule(int supplierId) throws SQLException {
        supplierDaysDAO.deleteAllBySupplierId(supplierId);
    }

    @Override
    public List<SupplierDaysDTO> getSchedule(int supplierId) throws SQLException {
        return supplierDaysDAO.findBySupplierId(supplierId);
    }
}
