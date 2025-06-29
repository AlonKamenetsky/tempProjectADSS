package SuppliersModule.DataLayer.DTO;

public record SupplyContractDTO (
    Integer supplyContractID,
    Integer supplierID
){
    @Override
    public String toString() {
        return "SupplyContractDTO{" +
                "supplyContractID=" + supplyContractID +
                ", supplierID=" + supplierID +
                '}';
    }

}