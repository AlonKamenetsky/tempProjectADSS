package SuppliersModule.DataLayer.DTO;

public record ProductDTO (
     Integer productId,
     String productName,
     String productCompanyName,
     String productCategory,
     Double productWeight
){
    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + productId +
                ", name=" + productName +
                ", company_name=" + productCompanyName +
                ", product_category=" + productCategory +
                ", product_weight=" + productWeight +
                '}';
    }

}