package SuppliersModule.DataLayer.DTO;

public record SupplyContractProductDataDTO (
    Integer supplyContractID,
    Integer productID,
    Double productPrice,
    Integer quantityForDiscount,
    Double discountPercentage

){
    @Override
    public String toString() {
        return "SupplyContractProductDataDTO{" +
                "supplyContractID=" + supplyContractID +
                ", productID=" + productID +
                ", productPrice=" + productPrice +
                ", quantityForDiscount=" + quantityForDiscount +
                ", discountPercentage=" + discountPercentage +
                '}';
    }

}