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
){
    @Override
    public String toString() {
        return "SupplierDTO{" +
                "supplierID=" + supplierID +
                ", supplierName='" + supplierName + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", deliveryMethod='" + deliveryMethod + '\'' +
                ", contactName='" + contactName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", supplyMethod='" + supplyMethod + '\'' +
                '}';
    }

}