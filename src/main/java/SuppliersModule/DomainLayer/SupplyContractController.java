package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.DTO.SupplyContractDTO;
import SuppliersModule.DataLayer.DTO.SupplyContractProductDataDTO;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;
import SuppliersModule.DomainLayer.Repositories.ISupplyContractRepository;
import SuppliersModule.DomainLayer.Repositories.SupplyContractRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplyContractController {

    private final ISupplyContractRepository contractRepository;
    private final List<SupplyContractDTO> supplyContractsList;

    public SupplyContractController() throws SQLException {
        this.contractRepository = new SupplyContractRepositoryImpl();
        this.supplyContractsList = contractRepository.getAllSupplyContracts();
    }

    public boolean registerNewContract(int supplierID, ArrayList<int[]> dataList, SupplyMethod method) throws SQLException {
        // Step 1: Create and insert a new contract
        SupplyContractDTO newContract = new SupplyContractDTO(null, supplierID);
        int contractId = contractRepository.insertSupplyContract(newContract);

        // Step 2: For each product data, create DTO and persist it
        for (int[] data : dataList) {
            int productID = data[0];
            double price = data[1];
            int quantityForDiscount = data[2];
            double discountPercentage = data[3];

            SupplyContractProductDataDTO productDataDTO = new SupplyContractProductDataDTO(
                    contractId,
                    productID,
                    price,
                    quantityForDiscount,
                    discountPercentage
            );

            contractRepository.addProductToContract(contractId, productDataDTO);
        }

        // Optional: add to in-memory list if needed
        supplyContractsList.add(new SupplyContractDTO(contractId, supplierID));

        return true;
    }


    private SupplyContractDTO getContractByContactID(int contractID) {
        for (SupplyContractDTO supplyContract : supplyContractsList)
            if(supplyContract.supplyContractID() == contractID)
                return supplyContract;

        return null;
    }

    public boolean removeContractByID(int contractID) {
        try {
            contractRepository.deleteContract(contractID);
        }
        catch (Exception e) {

        }
        return supplyContractsList.removeIf(supplyContract -> supplyContract.supplyContractID() == contractID);
    }

    public boolean removeAllSupplierContracts(int supplierID) throws SQLException {
        // Step 1: Remove from DB via repository
        contractRepository.deleteAllBySupplierId(supplierID);

        // Step 2: Remove from memory
        return supplyContractsList.removeIf(contract -> contract.supplierID() == supplierID);
    }


    // ********** GETTERS FUNCTIONS **********

    public ArrayList<SupplyContractDTO> getAllSupplierContracts(int supplierID) {
        ArrayList<SupplyContractDTO> supplyContractArrayList = new ArrayList<>();
        for (SupplyContractDTO supplyContract : supplyContractsList)
            if(supplyContract.supplyContractID() == supplierID)
                supplyContractArrayList.add(supplyContract);

        return supplyContractArrayList;
    }

    public ArrayList<SupplyContractProductDataDTO> getSupplyContractProductDataArrayList(int contractID) throws SQLException {
        return new ArrayList<>(contractRepository.getSupplyContractProductDataByContractId(contractID));
    }


    public ArrayList<SupplyContractDTO> getAllAvailableContracts() {
        return new ArrayList<>(this.supplyContractsList); // creates a shallow copy
    }


    // ********** OUTPUT FUNCTIONS **********

    public String getContractToString(int contractID) {
        SupplyContractDTO supplyContract = getContractByContactID(contractID);
        if(supplyContract != null) {
            return supplyContract.toString();
        }
        return null;
    }

    public String[] getAllContractToStrings() {
        String[] contractToStrings = new String[supplyContractsList.size()];
        for(int i = 0; i < supplyContractsList.size(); i++)
            contractToStrings[i] = supplyContractsList.get(i).toString();

        return contractToStrings;
    }
    public ArrayList<SupplyContractDTO> getSupplierContracts(int supplierId) throws SQLException {
        List<SupplyContractDTO> dtos = contractRepository.getContractsBySupplierId(supplierId);
        ArrayList<SupplyContractDTO> contracts = new ArrayList<>();

        for (SupplyContractDTO dto : dtos) {
            contracts.add(dto);
        }

        return contracts;
    }

}
