package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteSupplierDaysDAO;
import SuppliersModule.DataLayer.DTO.SupplierDaysDTO;

import java.sql.SQLException;
import java.util.List;

public class SupplierDaysRepositoryImpl implements ISupplierDaysRepository {
    private final SqliteSupplierDaysDAO dao = new SqliteSupplierDaysDAO();

    @Override
    public void addDaysForSupplier(List<SupplierDaysDTO> days) throws SQLException {
        for (SupplierDaysDTO dto : days) {
            dao.insert(dto);
        }
    }

    @Override
    public void deleteAllDaysForSupplier(int supplierId) throws SQLException {
        dao.deleteAllDaysForSupplier(supplierId);
    }
}
