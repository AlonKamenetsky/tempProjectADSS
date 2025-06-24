package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteSupplierDAO;
import SuppliersModule.DataLayer.DAO.SqliteSupplierDaysDAO;
import SuppliersModule.DataLayer.DTO.SupplierDTO;
import SuppliersModule.DataLayer.DTO.SupplierDaysDTO;

import java.sql.SQLException;
import java.util.List;

public class ScheduledSupplierRepositoryImpl extends AbstractSupplierRepositoryImpl implements IScheduledSupplierRepository{
    @Override
    protected String getSupplyMethod() {
        return "SCHEDULED";
    }

}
