package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DTO.SupplierDaysDTO;

import java.sql.SQLException;
import java.util.List;

public interface IScheduledSupplierRepository {
    void addDay(int supplierId, String day) throws SQLException;

    void removeDay(int supplierId, String day) throws SQLException;

    void clearSchedule(int supplierId) throws SQLException;

    List<SupplierDaysDTO> getSchedule(int supplierId) throws SQLException;
}
