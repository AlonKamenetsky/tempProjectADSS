package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.DTO.*;
import SuppliersModule.DomainLayer.Enums.*;
import SuppliersModule.DomainLayer.Repositories.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class SupplierController {
    OrderController orderController;
    SupplyContractController supplyContractController;
    private final IScheduledSupplierRepository scheduledRepo;
    private final IOnDemandSupplierRepository onDemandRepo;
    int numberOfSuppliers;
    ArrayList<SupplierDTO> suppliersArrayList;
    private final ISupplierDaysRepository supplierDaysRepo;
    private final ISupplyContractRepository supplyContractRepo;


    public SupplierController() throws SQLException, IOException {
        this.numberOfSuppliers = 0;
        this.suppliersArrayList = new ArrayList<>();
        this.orderController = new OrderController();
        this.supplyContractController = new SupplyContractController();
        this.scheduledRepo = new ScheduledSupplierRepositoryImpl();
        this.onDemandRepo = new OnDemandSupplierRepositoryImpl();
        this.suppliersArrayList = new ArrayList<>();
        this.supplierDaysRepo = new SupplierDaysRepositoryImpl();
        this.supplyContractRepo = new SupplyContractRepositoryImpl();
        loadSuppliersIntoMemory();
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public int registerNewSupplier(SupplyMethod supplyMethod, String supplierName, ProductCategory productCategory, DeliveringMethod deliveringMethod, String phoneNumber, String address, String email, String contactName, String bankAccount, PaymentMethod paymentMethod, EnumSet<WeekDay> supplyDays) throws SQLException {

        // 1. Build domain objects
        ContactInfo supplierContactInfo = new ContactInfo(phoneNumber, address, email, contactName);
        PaymentInfo supplierPaymentInfo = new PaymentInfo(bankAccount, paymentMethod);

        Supplier supplier;
        SupplierDTO savedDTO;

        // 2. Build correct Supplier and save using the right repository
        if (supplyMethod == SupplyMethod.ON_DEMAND) {
            supplier = new OnDemandSupplier(-1, supplierName, productCategory, deliveringMethod, supplierContactInfo, supplierPaymentInfo);

            savedDTO = onDemandRepo.addSupplier(supplier.getSupplierDTO());

        } else if (supplyMethod == SupplyMethod.SCHEDULED) {
            ScheduledSupplier scheduled = new ScheduledSupplier(-1, supplierName, productCategory, deliveringMethod, supplierContactInfo, supplierPaymentInfo, supplyDays);

            // Save supplier first
            savedDTO = scheduledRepo.addSupplier(scheduled.getSupplierDTO());

            // Save supply days
            for(WeekDay day: supplyDays){
                SupplierDaysDTO daysDTO = new SupplierDaysDTO(savedDTO.supplierID(), day.toString());
                supplierDaysRepo.insert(daysDTO);
            }

        } else {
            throw new IllegalArgumentException("Unknown supply method: " + supplyMethod);
        }

        // 3. Cache it in memory
        this.suppliersArrayList.add(savedDTO);
        this.numberOfSuppliers++;

        return savedDTO.supplierID();
    }


    private SupplierDTO getSupplierBySupplierID(int supplierID) {
        for (SupplierDTO supplier : this.suppliersArrayList)
            if (supplier.supplierID() == supplierID) return supplier;

        return null;
    }
    private ArrayList<OrderProductDataDTO> buildProductDataArray(ArrayList<int[]> dataList, ArrayList<SupplyContractDTO> supplyContracts) {
        ArrayList<OrderProductDataDTO> productDataList = new ArrayList<>();

        for (int[] entry : dataList) {
            int productId = entry[0];

            SupplyContractDTO matchedContract = null;
            for (SupplyContractDTO contract : supplyContracts) {
                List<SupplyContractProductDataDTO> productDataListFromRepo =
                        supplyContractRepo.getByContractId(contract.supplyContractID());

                for (SupplyContractProductDataDTO data : productDataListFromRepo) {
                    if (data.productID() == productId) {
                        matchedContract = contract;
                        break;
                    }
                }
                if (matchedContract != null) break;
            }

            if (matchedContract == null) return null;

            List<SupplyContractProductDataDTO> dataListFromRepo =
                    supplyContractRepo.getByContractId(matchedContract.supplyContractID());

            SupplyContractProductDataDTO data = dataListFromRepo.stream()
                    .filter(d -> d.productID() == productId)
                    .findFirst()
                    .orElse(null);

            if (data == null) return null;

            int quantity = entry[1];
            double price = data.productPrice();
            if (quantity >= data.quantityForDiscount()) {
                price *= (1 - data.discountPercentage() / 100);
            }

            productDataList.add(new OrderProductDataDTO(-1 ,productId, quantity, price));
        }

        return productDataList;
    }


    private SupplierDTO getSupplierByProductsPrice(ArrayList<int[]> dataList, SupplyMethod neededSupplyMethod) throws SQLException {
        SupplierDTO cheapestSupplier = null;
        double cheapestPrice = Integer.MAX_VALUE;

        for (SupplierDTO supplier : this.suppliersArrayList) {
            if (!supplier.supplyMethod().equalsIgnoreCase(neededSupplyMethod.name())) continue;

            // Replace this with a proper method to get contracts by supplier ID
            ArrayList<SupplyContractDTO> supplyContracts = supplyContractController.getSupplierContracts(supplier.supplierID());

            ArrayList<OrderProductDataDTO> orderProductData = buildProductDataArray(dataList, supplyContracts);
            if (supplyContracts == null || orderProductData == null) continue;

            double sum = 0;
            for (OrderProductDataDTO productData : orderProductData)
                sum += productData.productPrice();

            if (sum < cheapestPrice) {
                cheapestSupplier = supplier;
                cheapestPrice = sum;
            }
        }

        return cheapestSupplier;
    }


    public boolean deleteSupplier(int supplierID) {
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null) return false;
        if (supplier.supplyMethod().equals(SupplyMethod.SCHEDULED.toString())) {
            try {
                supplierDaysRepo.deleteAllDaysForSupplier(supplierID);
            } catch (SQLException e) {
            }
        }
        try {
            scheduledRepo.deleteSupplier(supplierID);
        } catch (Exception e) {
        }
        suppliersArrayList.remove(supplier);
        try {
            this.supplyContractController.removeAllSupplierContracts(supplierID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.orderController.removeAllSupplierOrders(supplierID);
        return true;
    }

    // ********** UPDATE FUNCTIONS **********

    public boolean updateSupplierName(int supplierID, String supplierName) {
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null) return false;

        // Update the name
        SupplierDTO updated = new SupplierDTO(supplierID, supplierName, supplier.productCategory(), supplier.deliveryMethod(), supplier.contactName(), supplier.phoneNumber(), supplier.address(), supplier.emailAddress(), supplier.bankAccount(), supplier.paymentMethod(), supplier.supplyMethod());

        try {
            if (supplier.supplyMethod().equalsIgnoreCase(SupplyMethod.SCHEDULED.toString())) {
                scheduledRepo.updateSupplier(updated);
            } else {
                onDemandRepo.updateSupplier(updated);
            }

            // Update cached list
            this.suppliersArrayList.removeIf(s -> s.supplierID() == supplierID);
            this.suppliersArrayList.add(updated);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateSupplierDeliveringMethod(int supplierID, DeliveringMethod deliveringMethod) {
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null) return false;

        SupplierDTO updated = new SupplierDTO(supplierID, supplier.supplierName(), supplier.productCategory(), deliveringMethod.toString(), supplier.contactName(), supplier.phoneNumber(), supplier.address(), supplier.emailAddress(), supplier.bankAccount(), supplier.paymentMethod(), supplier.supplyMethod());

        try {
            if (supplier.supplyMethod().equalsIgnoreCase(SupplyMethod.SCHEDULED.toString())) {
                scheduledRepo.updateSupplier(updated);
            } else {
                onDemandRepo.updateSupplier(updated);
            }

            // Update cached list
            this.suppliersArrayList.removeIf(s -> s.supplierID() == supplierID);
            this.suppliersArrayList.add(updated);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateSupplierContactInfo(int supplierID, String phoneNumber, String address, String email, String contactName) {
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null) return false;

        SupplierDTO updated = new SupplierDTO(supplierID, supplier.supplierName(), supplier.productCategory(), supplier.deliveryMethod().toString(), contactName, phoneNumber, address, email, supplier.bankAccount(), supplier.paymentMethod(), supplier.supplyMethod());

        try {
            if (supplier.supplyMethod().equalsIgnoreCase(SupplyMethod.SCHEDULED.toString())) {
                scheduledRepo.updateSupplier(updated);
            } else {
                onDemandRepo.updateSupplier(updated);
            }

            // Update cached list
            this.suppliersArrayList.removeIf(s -> s.supplierID() == supplierID);
            this.suppliersArrayList.add(updated);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateSupplierPaymentInfo(int supplierID, String bankAccount, PaymentMethod paymentMethod) {
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null) return false;

        SupplierDTO updated = new SupplierDTO(supplierID, supplier.supplierName(), supplier.productCategory(), supplier.deliveryMethod().toString(), supplier.contactName(), supplier.phoneNumber(), supplier.address(), supplier.emailAddress(), bankAccount, paymentMethod.toString(), supplier.supplyMethod());

        try {
            if (supplier.supplyMethod().equalsIgnoreCase(SupplyMethod.SCHEDULED.toString())) {
                scheduledRepo.updateSupplier(updated);
            } else {
                onDemandRepo.updateSupplier(updated);
            }

            // Update in-memory list
            this.suppliersArrayList.removeIf(s -> s.supplierID() == supplierID);
            this.suppliersArrayList.add(updated);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ********** GETTERS FUNCTIONS **********

    public ProductCategory getSupplierProductCategory(int supplierID) {
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null) return ProductCategory.valueOf(supplier.productCategory());
        return null;
    }


    public DeliveringMethod getSupplierDeliveringMethod(int supplierID) {
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null) return DeliveringMethod.valueOf(supplier.deliveryMethod());
        return null;
    }


    public ContactInfo getSupplierContactInfo(int supplierID) {
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null) {
            ContactInfo contactInfo = new ContactInfo(supplier.phoneNumber(), supplier.address(), supplier.emailAddress(), supplier.contactName());
            return contactInfo;
        }

        return null;
    }

    public SupplyMethod getSupplierSupplyMethod(int supplierID) {
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null) {
            if (supplier.supplyMethod().equalsIgnoreCase(SupplyMethod.SCHEDULED.toString()))
                return SupplyMethod.SCHEDULED;
            else return SupplyMethod.ON_DEMAND;
        }

        return null;
    }

    // ********** OUTPUT FUNCTIONS **********

    public String[] getAllSuppliersAsString() {
        String[] suppliersAsString = new String[this.suppliersArrayList.size()];
        for (int i = 0; i < suppliersAsString.length; i++)
            suppliersAsString[i] = this.suppliersArrayList.get(i).toString();

        return suppliersAsString;
    }

    public String getSupplierAsString(int supplierID) {
        SupplierDTO supplier = this.getSupplierBySupplierID(supplierID);
        if (supplier != null) return supplier.toString();
        return null;
    }

    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    public boolean registerNewContract(int supplierID, ArrayList<double[]> dataList) {
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null) return false;

        SupplyMethod supplyMethod = SupplyMethod.valueOf(supplier.supplyMethod());

        try {
            return  supplyContractController.registerNewContract(supplierID, dataList, supplyMethod);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // ********** GETTERS FUNCTIONS **********

    private ArrayList<SupplyContractDTO> getAvailableContractsForOrder(int orderID) {
        int supplierID = this.orderController.getOrderSupplierID(orderID);

        // Optional: validate supplier exists (not strictly necessary if controller handles it safely)
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null) return null;

        // Retrieve the contracts from the SupplyContractController
        try {
            return this.supplyContractController.getSupplierContracts(supplierID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ********** OUTPUT FUNCTIONS **********

    public String getContractToString(int contractID) {
        return this.supplyContractController.getContractToString(contractID);
    }

    public String[] getAvailableContractsForOrderAsString(int orderID) {
        ArrayList<SupplyContractDTO> supplyContractArrayList = this.getAvailableContractsForOrder(orderID);

        String[] result = new String[supplyContractArrayList.size()];
        for (int i = 0; i < supplyContractArrayList.size(); i++)
            result[i] = supplyContractArrayList.get(i).toString();

        return result;
    }

    public String[] GetSupplierContractsAsString(int supplierID) {
        SupplierDTO supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null) return null;

        ArrayList<SupplyContractDTO> supplyContractArrayList = null;
        try {
            supplyContractArrayList = this.supplyContractController.getSupplierContracts(supplierID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (supplyContractArrayList == null) return null;

        String[] result = new String[supplyContractArrayList.size()];
        for (int i = 0; i < supplyContractArrayList.size(); i++)
            result[i] = supplyContractArrayList.get(i).toString();

        return result;
    }


    public String[] getAllContractToStrings() {
        return this.supplyContractController.getAllContractToStrings();
    }

    public Set<Integer> getAllAvailableProductsInContracts() {
        Set<Integer> products = new HashSet<>();

        List<SupplyContractDTO> scheduledContracts = scheduledRepo.getAllContracts();
        List<SupplyContractDTO> onDemandContracts = onDemandRepo.getAllContracts();

        List<SupplyContractDTO> allContracts = new ArrayList<>();
        allContracts.addAll(scheduledContracts);
        allContracts.addAll(onDemandContracts);

        for (SupplyContractDTO contract : allContracts) {
            List<SupplyContractProductDataDTO> productDataList =
                    supplyContractRepo.getByContractId(contract.supplyContractID());

            for (SupplyContractProductDataDTO data : productDataList)
                products.add(data.productID());
        }

        return products;
    }



    // --------------------------- ORDER FUNCTIONS ---------------------------

    public int registerNewOrder(ArrayList<int[]> dataList, Date creationDate, Date deliveryDate, String type, String deliverySite) throws SQLException {
        SupplierDTO supplier = this.getSupplierByProductsPrice(dataList, SupplyMethod.valueOf(type));
        if (supplier == null) return -1;

        int supplierId = supplier.supplierID();

        DeliveringMethod deliveringMethod = DeliveringMethod.valueOf(supplier.deliveryMethod());
        ContactInfo contactInfo = new ContactInfo(supplier.phoneNumber(), supplier.address(), supplier.emailAddress(), supplier.contactName());
        SupplyMethod supplyMethod = SupplyMethod.valueOf(supplier.supplyMethod());

        List<SupplyContractDTO> supplyContracts = supplyContractController.getSupplierContracts(supplierId);
        return this.orderController.registerNewOrder(supplierId, dataList, supplyContracts, creationDate, deliveryDate, deliveringMethod, supplyMethod, contactInfo, deliverySite);
    }


    public boolean registerNewScheduledOrder(WeekDay day, ArrayList<int[]> dataList, String deliverySite) throws SQLException {
        SupplierDTO supplier = this.getSupplierByProductsPrice(dataList, SupplyMethod.SCHEDULED);
        if (supplier == null) return false;

        int supplierId = supplier.supplierID();
        List<SupplyContractDTO> supplyContracts = supplyContractController.getSupplierContracts(supplierId);

        ArrayList<OrderProductDataDTO> orderProductData = buildProductDataArray(dataList, new ArrayList<>(supplyContracts));
        if (orderProductData == null) return false;
        return this.orderController.registerNewScheduledOrder(supplierId, day, dataList, deliverySite);
    }



    public boolean deleteOrder(int orderID) {
        return this.orderController.deleteOrder(orderID);
    }

    public boolean orderExists(int orderID) {
        return this.orderController.orderExists(orderID);
    }

    // ********** UPDATE FUNCTIONS **********

    public boolean updateOrderContactInfo(int orderId, String phoneNumber, String address, String email, String contactName) {
        return this.orderController.updateOrderContactInfo(orderId, phoneNumber, address, email, contactName);
    }

    public boolean updateOrderSupplyDate(int orderID, Date supplyDate) {
        return this.orderController.updateOrderSupplyDate(orderID, supplyDate);
    }

    public HashMap<Integer, Integer> updateOrderStatus(int orderID, OrderStatus orderStatus) {
        return this.orderController.updateOrderStatus(orderID, orderStatus);
    }

    public boolean addProductsToOrder(int orderID, ArrayList<int[]> dataList) {
        ArrayList<SupplyContractDTO> supplyContracts = getAvailableContractsForOrder(orderID);
        return this.orderController.addProductsToOrder(orderID, supplyContracts, dataList);
    }

    public boolean removeProductsFromOrder(int orderID, ArrayList<Integer> dataList) {
        return this.orderController.removeProductsFromOrder(orderID, dataList);
    }

    // ********** GETTERS FUNCTIONS **********

    public Date getOrderSupplyDate(int orderID) {
        return this.orderController.getOrderSupplyDate(orderID);
    }

    // ********** OUTPUT FUNCTIONS **********

    public String getOrderAsString(int orderID) {
        return this.orderController.getOrderAsString(orderID);
    }

    public String[] getAllOrdersAsString() {
        return this.orderController.getAllOrdersAsString();
    }

    public String[] getAllScheduledOrdersAsString() throws SQLException {
        return orderController.getAllScheduledOrdersAsString();
    }


    private void loadSuppliersIntoMemory() throws SQLException {
        List<SupplierDTO> scheduled = scheduledRepo.getAllSuppliers();
        List<SupplierDTO> onDemand = onDemandRepo.getAllSuppliers();
        suppliersArrayList.addAll(scheduled);
        suppliersArrayList.addAll(onDemand);
        numberOfSuppliers = suppliersArrayList.size();
    }


    public void dropData() {
        this.suppliersArrayList.clear();
        supplyContractController.dropData();
        orderController.dropData();
    }
    public String[] getAllSupplyContractProductsToString(){
        try {
            return this.supplyContractController.getAllSupplyContractProductsAsString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> executeScheduledOrders() {
        return orderController.executeScheduledOrders();
    }

    public String[] getScheduledOrderDataForExecution(Integer orderID) {
        return orderController.getScheduledOrderDataForExecution(orderID);
    }

    public String getOrderDepartureAddress(int id) {
        return orderController.getOrderDepartureAddress(id);
    }

    public String getOrderContactName(int orderID) {
        return orderController.getOrderContactName(orderID);
    }

    public String getOrderPhoneNumber(int orderID) {
        return orderController.getOrderPhoneNumber(orderID);
    }
}
