package SuppliersModule.DomainLayer.Repositories;

import SuppliersModule.DataLayer.DAO.SqliteSupplyContractDAO;
import SuppliersModule.DataLayer.DTO.SupplierDTO;
import SuppliersModule.DataLayer.DTO.SupplyContractDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OnDemandSupplierRepositoryImpl extends AbstractSupplierRepositoryImpl implements  IOnDemandSupplierRepository {
    @Override
    protected String getSupplyMethod() {
        return "ON_DEMAND";
    }

}
