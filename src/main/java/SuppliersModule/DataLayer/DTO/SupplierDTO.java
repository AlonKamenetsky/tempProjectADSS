package SuppliersModule.DataLayer.DTO;

public record SupplierDTO (
    Integer supplierID,
     String supplierName,
     String productCategory,
     String deliveryMethod,
     String contactName,
     String emailAddress,
     String phoneNumber,
     String address,
     String bankAccount,
     String paymentMethod,
     String supplyMethod
){}