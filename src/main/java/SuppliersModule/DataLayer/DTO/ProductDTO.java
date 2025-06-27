package SuppliersModule.DataLayer.DTO;

public record ProductDTO (
     Integer productId,
     String productName,
     String productCompanyName,
     String productCategory
){
    @Override
    public String toString() {
        return "Product ID: " + productId +
                ", Name: " + productName +
                ", Company: " + productCompanyName +
                ", Category: " + productCategory;
    }
}