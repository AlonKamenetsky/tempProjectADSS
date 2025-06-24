package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.DTO.SupplierDTO;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;
import SuppliersModule.DomainLayer.Enums.WeekDay;

import java.util.ArrayList;

public class OnDemandSupplier extends Supplier {

    ArrayList<Order> orderHistory;

    public OnDemandSupplier(int supplierId, String supplierName, ProductCategory productCategory, DeliveringMethod supplierDeliveringMethod, ContactInfo supplierContactInfo, PaymentInfo supplierPaymentInfo) {
        super(supplierId, supplierName, productCategory, supplierDeliveringMethod, supplierContactInfo, supplierPaymentInfo);
        this.supplierDTO = new SupplierDTO(
                supplierId,
                supplierName,
                productCategory.toString(),
                supplierDeliveringMethod.toString(),
                supplierContactInfo.getName(),
                supplierContactInfo.getEmail(),
                supplierContactInfo.getPhoneNumber(),
                supplierContactInfo.getAddress(),
                supplierPaymentInfo.getSupplierBankAccount(),
                supplierPaymentInfo.getSupplierPaymentMethod().toString(),
                SupplyMethod.ON_DEMAND.toString()
        );
    }
    @Override
    public SupplyMethod getSupplyMethod() {
        return SupplyMethod.ON_DEMAND;
    }
}
