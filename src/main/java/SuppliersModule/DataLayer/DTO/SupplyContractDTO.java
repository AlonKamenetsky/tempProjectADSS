package SuppliersModule.DataLayer.DTO;

import SuppliersModule.DomainLayer.SupplyContract;

public record SupplyContractDTO (
    Integer supplyContractID,
    Integer supplierID
){}