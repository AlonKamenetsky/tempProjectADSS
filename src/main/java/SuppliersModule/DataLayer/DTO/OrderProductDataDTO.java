package SuppliersModule.DataLayer.DTO;

import SuppliersModule.DomainLayer.OrderProductData;

public record OrderProductDataDTO (
     Integer orderID,
     Integer productID,
     Integer productQuantity,
     Integer productPrice
){}