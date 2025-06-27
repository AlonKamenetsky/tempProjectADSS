package SuppliersModule.DomainLayer.Repositories;

public class ScheduledSupplierRepositoryImpl extends AbstractSupplierRepositoryImpl implements IScheduledSupplierRepository{
    @Override
    protected String getSupplyMethod() {
        return "SCHEDULED";
    }


}
