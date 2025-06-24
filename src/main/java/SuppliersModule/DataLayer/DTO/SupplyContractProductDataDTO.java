package SuppliersModule.DataLayer.DTO;

import SuppliersModule.DomainLayer.SupplyContractProductData;

public record SupplyContractProductDataDTO (
    Integer supplyContractID,
    Integer productID,
    Double productPrice,
    Integer quantityForDiscount,
    Double discountPercentage

){}