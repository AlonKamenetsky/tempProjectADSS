package SuppliersModule.DataLayer.DTO;

public record OrderDTO (
    Integer orderID,
    Integer supplierID,
     String phoneNumber,
     String physicalAddress,
     String emailAddress,
     String contactName,
     String deliveryMethod,
     String orderDate,    // Stored as ISO‑8601 string
     String deliveryDate,
     double totalPrice,
     String orderStatus,
     String supplyMethod
){
    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderID=" + orderID +
                ", supplierID=" + supplierID +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", physicalAddress='" + physicalAddress + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", contactName='" + contactName + '\'' +
                ", deliveryMethod='" + deliveryMethod + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", totalPrice=" + totalPrice +
                ", orderStatus='" + orderStatus + '\'' +
                ", supplyMethod='" + supplyMethod + '\'' +
                '}';
    }

}


