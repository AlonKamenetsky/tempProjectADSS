package SuppliersModule.DomainLayer.Repositories;

public class OnDemandSupplierRepositoryImpl extends AbstractSupplierRepositoryImpl implements  IOnDemandSupplierRepository {
    @Override
    protected String getSupplyMethod() {
        return "ON_DEMAND";
    }

}
