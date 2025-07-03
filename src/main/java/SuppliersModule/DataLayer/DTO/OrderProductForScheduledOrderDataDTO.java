package SuppliersModule.DataLayer.DTO;

public record OrderProductForScheduledOrderDataDTO(
        Integer orderID,
        Integer productID,
        Integer productQuantity,
        String day,
        String deliverySite
) {
}
