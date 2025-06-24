package SuppliersModule.DataLayer.DTO;

import SuppliersModule.DomainLayer.Product;

public record ProductDTO (
     Integer productId,
     String productName,
     String productCompanyName,
     String productCategory
){}