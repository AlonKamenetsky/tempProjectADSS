package SuppliersModule.DataLayer.DTO;

import SuppliersModule.DomainLayer.Order;

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
){}


