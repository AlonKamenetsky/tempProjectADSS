package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.DAO.SqliteSupplierDaysDAO;
import SuppliersModule.DataLayer.DTO.SupplierDTO;
import SuppliersModule.DataLayer.DTO.SupplierDaysDTO;
import SuppliersModule.DomainLayer.Enums.*;
import SuppliersModule.DomainLayer.Repositories.IOnDemandSupplierRepository;
import SuppliersModule.DomainLayer.Repositories.IScheduledSupplierRepository;
import SuppliersModule.DomainLayer.Repositories.OnDemandSupplierRepositoryImpl;
import SuppliersModule.DomainLayer.Repositories.ScheduledSupplierRepositoryImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static SuppliersModule.DomainLayer.OrderController.buildProductDataArray;

public class SupplierController {
    OrderController orderController;
    SupplyContractController supplyContractController;
    private final IScheduledSupplierRepository scheduledRepo;
    private final IOnDemandSupplierRepository onDemandRepo;
    int numberOfSuppliers;
    ArrayList<SupplierDTO> suppliersArrayList;



    public SupplierController() throws SQLException, IOException {
        this.numberOfSuppliers = 0;
        this.suppliersArrayList = new ArrayList<>();
        this.orderController = new OrderController();
        this.supplyContractController = new SupplyContractController();
        this.scheduledRepo = new ScheduledSupplierRepositoryImpl();
        this.onDemandRepo = new OnDemandSupplierRepositoryImpl();
        this.suppliersArrayList = new ArrayList<>();
        loadSuppliersIntoMemory();
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public int registerNewSupplier(SupplyMethod supplyMethod,
                                   String supplierName,
                                   ProductCategory productCategory,
                                   DeliveringMethod deliveringMethod,
                                   String phoneNumber,
                                   String address,
                                   String email,
                                   String contactName,
                                   String bankAccount,
                                   PaymentMethod paymentMethod,
                                   EnumSet<WeekDay> supplyDays) throws SQLException {

        // 1. Build domain objects
        ContactInfo supplierContactInfo = new ContactInfo(phoneNumber, address, email, contactName);
        PaymentInfo supplierPaymentInfo = new PaymentInfo(bankAccount, paymentMethod);

        Supplier supplier;
        SupplierDTO savedDTO;

        // 2. Build correct Supplier and save using the right repository
        if (supplyMethod == SupplyMethod.ON_DEMAND) {
            supplier = new OnDemandSupplier(this.numberOfSuppliers, supplierName, productCategory,
                    deliveringMethod, supplierContactInfo, supplierPaymentInfo);

            savedDTO = onDemandRepo.addSupplier(supplier.getSupplierDTO());

        } else if (supplyMethod == SupplyMethod.SCHEDULED) {
            ScheduledSupplier scheduled = new ScheduledSupplier(this.numberOfSuppliers, supplierName, productCategory,
                    deliveringMethod, supplierContactInfo, supplierPaymentInfo, supplyDays);

            // Save supplier first
            savedDTO = scheduledRepo.addSupplier(scheduled.getSupplierDTO());

            // Save supply days
            for (SupplierDaysDTO dto : scheduled.getSupplierDaysDTOS()) {
                new SqliteSupplierDaysDAO().insert(dto); // or use a ScheduledSupplierDaysRepository if you have one
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
            if (supplier.supplierID() == supplierID)
                return supplier;

        return null;
    }

    private SupplierDTO getSupplierByProductsPrice(ArrayList<int[]> dataList, SupplyMethod neededSupplyMethod) throws SQLException {
        SupplierDTO cheapestSupplier = null;
        double cheapestPrice = Integer.MAX_VALUE;

        for (SupplierDTO supplier : this.suppliersArrayList) {
            if (!supplier.supplyMethod().equalsIgnoreCase(neededSupplyMethod.name()))
                continue;

            // Replace this with a proper method to get contracts by supplier ID
            ArrayList<SupplyContract> supplyContracts = supplyContractController.getSupplierContracts(supplier.id());

            ArrayList<OrderProductData> orderProductData = buildProductDataArray(dataList, supplyContracts);
            if (supplyContracts == null || orderProductData == null)
                continue;

            double sum = 0;
            for (OrderProductData productData : orderProductData)
                sum += productData.getTotalPrice();

            if (sum < cheapestPrice) {
                cheapestSupplier = supplier;
                cheapestPrice = sum;
            }
        }

        return cheapestSupplier;
    }


    public boolean deleteSupplier(int supplierID) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        supplier.supplierDTO.Delete();
        if (supplier.getSupplyMethod() == SupplyMethod.SCHEDULED)
            for (SupplierDaysDTO dto : ((ScheduledSupplier)supplier).supplierDaysDTOS)
                dto.Insert();

        this.supplyContractController.removeAllSupplierContracts(supplierID);
        this.orderController.removeAllSupplierOrders(supplierID);

        return this.suppliersArrayList.removeIf(s -> s.supplierId == supplierID);
    }

    // ********** UPDATE FUNCTIONS **********

    public boolean updateSupplierName(int supplierID, String supplierName) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        supplier.setSupplierName(supplierName);
        return true;
    }

    public boolean updateSupplierDeliveringMethod(int supplierID, DeliveringMethod deliveringMethod) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        supplier.setSupplierDeliveringMethod(deliveringMethod);
        return true;
    }

    public boolean updateSupplierContactInfo(int supplierID, String phoneNumber, String address, String email, String contactName) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        ContactInfo contactInfo = new ContactInfo(phoneNumber, address, email, contactName);
        supplier.setSupplierContactInfo(contactInfo);
        return true;
    }

    public boolean updateSupplierPaymentInfo(int supplierID, String bankAccount, PaymentMethod paymentMethod) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        PaymentInfo paymentInfo = new PaymentInfo(bankAccount, paymentMethod);
        supplier.setSupplierPaymentInfo(paymentInfo);
        return true;
    }

    // ********** GETTERS FUNCTIONS **********

    public ProductCategory getSupplierProductCategory(int supplierID) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null)
            return supplier.getSupplierProductCategory();
        return null;
    }

    public DeliveringMethod getSupplierDeliveringMethod(int supplierID) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null)
            return supplier.getSupplierDeliveringMethod();

        return null;
    }

    public ContactInfo getSupplierContactInfo(int supplierID){
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null)
            return supplier.getSupplierContactInfo();

        return null;
    }

    public SupplyMethod getSupplierSupplyMethod(int supplierID) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null)
            return supplier.getSupplyMethod();

        return null;
    }

    // ********** OUTPUT FUNCTIONS **********

    public String[] getAllSuppliersAsString() {
        String[] suppliersAsString = new String[this.suppliersArrayList.size()];
        for (int i = 0 ; i < suppliersAsString.length;i++)
            suppliersAsString[i] = this.suppliersArrayList.get(i).toString();

        return suppliersAsString;
    }

    public String getSupplierAsString(int supplierID) {
        Supplier supplier = this.getSupplierBySupplierID(supplierID);
        if (supplier != null)
            return supplier.toString();
        return null;
    }

    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    public boolean registerNewContract(int supplierID, ArrayList<int[]> dataList) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        SupplyMethod supplyMethod = supplier.getSupplyMethod();

        SupplyContract contract = supplyContractController.registerNewContract(supplierID, dataList, supplyMethod);

        supplier.addSupplierContract(contract);

        return true;
    }

    // ********** GETTERS FUNCTIONS **********

    private ArrayList<SupplyContract> getAvailableContractsForOrder(int orderID) {
        int supplierID = this.orderController.getOrderSupplierID(orderID);
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return null;

        return supplier.getSupplierContracts();
    }

    // ********** OUTPUT FUNCTIONS **********

    public String getContractToString(int contractID) {
        return this.supplyContractController.getContractToString(contractID);
    }

    public String[] getAvailableContractsForOrderAsString(int orderID) {
        ArrayList<SupplyContract> supplyContractArrayList = this.getAvailableContractsForOrder(orderID);

        String[] result = new String[supplyContractArrayList.size()];
        for (int i = 0; i < supplyContractArrayList.size(); i++)
            result[i] = supplyContractArrayList.get(i).toString();

        return result;
    }

    public String[] GetSupplierContractsAsString(int supplierID) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return null;

        ArrayList<SupplyContract> supplyContractArrayList = supplier.getSupplierContracts();

        String[] result = new String[supplyContractArrayList.size()];
        for (int i = 0; i < supplyContractArrayList.size(); i++)
            result[i] = supplyContractArrayList.get(i).toString();

        return result;
    }

    public String[] getAllContractToStrings(){
        return this.supplyContractController.getAllContractToStrings();
    }

    public Set<Integer> getAllAvailableProductsInContracts() {
        Set<Integer> products = new HashSet<>();
        for (SupplyContract supplyContract : this.supplyContractController.getAllAvailableContracts())
            for (SupplyContractProductData supplyContractProductData : supplyContract.getSupplyContractProductData())
                products.add(supplyContractProductData.getProductID());

        return products;
    }

    // --------------------------- ORDER FUNCTIONS ---------------------------

    public boolean registerNewOrder(ArrayList<int[]> dataList, Date creationDate, Date deliveryDate) {
        Supplier supplier = this.getSupplierByProductsPrice(dataList, SupplyMethod.ON_DEMAND);
        if (supplier == null)
            return false;

        int supplierId = supplier.getSupplierId();

        DeliveringMethod deliveringMethod = this.getSupplierDeliveringMethod(supplierId);
        ContactInfo contactInfo = this.getSupplierContactInfo(supplierId);
        SupplyMethod supplyMethod = this.getSupplierSupplyMethod(supplierId);
        ArrayList<SupplyContract> supplyContracts = supplier.getSupplierContracts();

        return this.orderController.registerNewOrder(supplierId, dataList, supplyContracts, creationDate, deliveryDate, deliveringMethod, supplyMethod, contactInfo);
    }

    public boolean registerNewScheduledOrder(WeekDay day, ArrayList<int[]> dataList) {
        Supplier supplier = this.getSupplierByProductsPrice(dataList, SupplyMethod.SCHEDULED);
        if (supplier == null)
            return false;

        ArrayList<SupplyContract> supplyContracts = supplier.getSupplierContracts();
        ArrayList<OrderProductData> orderProductData = buildProductDataArray(dataList, supplyContracts);

        ScheduledOrder scheduledOrder = new ScheduledOrder(supplier.supplierId, day, orderProductData);
        scheduledOrder.Insert();

        ScheduledSupplier scheduledSupplier = ((ScheduledSupplier)supplier);
        scheduledSupplier.addScheduledOrder(day, scheduledOrder);

        return true;
    }

    public boolean deleteOrder(int orderID) {
        return this.orderController.deleteOrder(orderID);
    }

    public boolean orderExists(int orderID) {
        return this.orderController.orderExists(orderID);
    }

    // ********** UPDATE FUNCTIONS **********

    public boolean updateOrderContactInfo(int orderId, String phoneNumber, String address, String email, String contactName){
        return this.orderController.updateOrderContactInfo(orderId, phoneNumber, address, email, contactName);
    }

    public boolean updateOrderSupplyDate(int orderID, Date supplyDate){
        return this.orderController.updateOrderSupplyDate(orderID, supplyDate);
    }

    public HashMap<Integer, Integer> updateOrderStatus(int orderID, OrderStatus orderStatus) {
        return this.orderController.updateOrderStatus(orderID, orderStatus);
    }

    public boolean addProductsToOrder(int orderID, ArrayList<int[]> dataList) {
        ArrayList<SupplyContract> supplyContracts = getAvailableContractsForOrder(orderID);
        return this.orderController.addProductsToOrder(orderID,supplyContracts, dataList);
    }

    public boolean removeProductsFromOrder(int orderID, ArrayList<Integer> dataList) {
        return this.orderController.removeProductsFromOrder(orderID, dataList);
    }

    // ********** GETTERS FUNCTIONS **********

    public Date getOrderSupplyDate(int orderID){
        return this.orderController.getOrderSupplyDate(orderID);
    }

    // ********** OUTPUT FUNCTIONS **********

    public String getOrderAsString(int orderID) {
        return this.orderController.getOrderAsString(orderID);
    }

    public String[] getAllOrdersAsString() {
        return this.orderController.getAllOrdersAsString();
    }

     public String[] getAllScheduledOrdersAsString() {
         ArrayList<String> results = new ArrayList<>();
         for (Supplier supplier : this.suppliersArrayList)
             if (supplier.getSupplyMethod() == SupplyMethod.SCHEDULED) {
                 ScheduledSupplier ScheduledSupplier = (ScheduledSupplier) supplier;
                 String res = ScheduledSupplier.getScheduledOrders().toString();
                 if (res.equals("{}"))
                     continue;
                 results.add(ScheduledSupplier.getScheduledOrders().toString());
             }

         return results.toArray(new String[results.size()]);
     }
    private void loadSuppliersIntoMemory() throws SQLException {
        List<SupplierDTO> scheduled = scheduledRepo.getAllSuppliers();
        List<SupplierDTO> onDemand = onDemandRepo.getAllSuppliers();
        suppliersArrayList.addAll(scheduled);
        suppliersArrayList.addAll(onDemand);
        numberOfSuppliers = suppliersArrayList.size();
    }


}
