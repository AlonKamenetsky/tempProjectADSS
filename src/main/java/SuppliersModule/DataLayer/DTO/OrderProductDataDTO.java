package SuppliersModule.DataLayer.DTO;

public record OrderProductDataDTO (
     Integer orderID,
     Integer productID,
     Integer productQuantity,
     Double productPrice
){
    @Override
    public String toString() {
        return "OrderProductDataDTO{" +
                "orderID=" + orderID +
                ", productID=" + productID +
                ", productQuantity=" + productQuantity +
                ", productPrice=" + productPrice +
                '}';
    }


}