package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DTO.SupplierDaysDTO;

import java.sql.SQLException;
import java.util.List;

public interface ISupplierDaysRepository {
    void addDaysForSupplier(List<SupplierDaysDTO> days) throws SQLException;

    void deleteAllDaysForSupplier(int supplierId) throws SQLException;
}
