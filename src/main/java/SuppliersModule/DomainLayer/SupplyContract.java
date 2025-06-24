package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.SupplyContractDTO;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.util.ArrayList;

public class SupplyContract {
   int contractID;
   int supplierID;
   ArrayList<SupplyContractProductData> supplyContractProductsDataArray;

   SupplyContractDTO supplyContractDTO;

   public SupplyContract(int supplierID, int contractID) {
      this.contractID = contractID;
      this.supplierID = supplierID;
      this.supplyContractProductsDataArray = new ArrayList<>();

      this.supplyContractDTO = new SupplyContractDTO(contractID, supplierID);
   }

   public SupplyContract(int contractID, int supplierID, SupplyMethod supplierSupplyMethod, ArrayList<SupplyContractProductData> supplyContractProductsDataArray) {
      this.contractID = contractID;
      this.supplierID = supplierID;
      this.supplyContractProductsDataArray = supplyContractProductsDataArray;

      this.supplyContractDTO = new SupplyContractDTO(contractID, supplierID);
   }

   public SupplyContract(SupplyContractDTO supplyContractDTO) {
      this.contractID = supplyContractDTO.supplyContractID;
      this.supplierID = supplyContractDTO.supplierID;
      this.supplyContractDTO = supplyContractDTO ;
   }

   public void addSupplyContractProductData(SupplyContractProductData data) {
      this.supplyContractProductsDataArray.add(data);
   }

   public int getContractID() {
      return contractID;
   }

   public int getSupplierID() {
      return supplierID;
   }

   public ArrayList<SupplyContractProductData> getSupplyContractProductData() {
      return this.supplyContractProductsDataArray;
   }

   public SupplyContractProductData getSupplyContractProductDataOfProduct(int productID) {
      for (SupplyContractProductData productData : this.supplyContractProductsDataArray)
         if (productData.getProductID() == productID)
            return productData;

      return null;
   }

   public boolean CheckIfProductInData(int productID) {
      for (SupplyContractProductData productData : this.supplyContractProductsDataArray)
         if (productData.getProductID() == productID)
            return true;

      return false;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("SupplyContract {\n");
      sb.append("  Supplier ID: ").append(supplierID).append(",\n");
      sb.append("  Contract ID: ").append(contractID).append(",\n");
      sb.append("  Product Data List:\n");
      for (SupplyContractProductData data : supplyContractProductsDataArray) {
         sb.append("    ").append(data).append("\n");
      }
      sb.append("}");
      return sb.toString();
   }
}
