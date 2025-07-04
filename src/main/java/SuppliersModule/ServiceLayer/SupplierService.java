package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.Enums.*;
import SuppliersModule.DomainLayer.SupplierController;

import java.sql.SQLException;
import java.util.*;

public class SupplierService {
    SupplierController supplierController;

    public SupplierService() {
        try {
            this.supplierController = new SupplierController();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int registerNewSupplier(int supplyMethod, String supplierName, int productCategory, int deliveringMethod,
                                   String phoneNumber, String address, String email, String contactName,
                                   String bankAccount, int paymentMethod, ArrayList<Integer> supplyDays) {
        SupplyMethod sm = SupplyMethod.values()[supplyMethod];
        ProductCategory pc = ProductCategory.values()[productCategory];
        DeliveringMethod dm = DeliveringMethod.values()[deliveringMethod];
        PaymentMethod pm = PaymentMethod.values()[paymentMethod];
        EnumSet<WeekDay> sd = null;
        if (supplyDays != null) {
            sd = EnumSet.noneOf(WeekDay.class);
            for (int day : supplyDays)
                sd.add(WeekDay.values()[day - 1]);
        }

        try {
            return this.supplierController.registerNewSupplier(sm, supplierName, pc, dm, phoneNumber, address, email, contactName, bankAccount, pm, sd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void registerNewSupplier(SupplyMethod supplyMethod, String supplierName, ProductCategory productCategory, DeliveringMethod deliveringMethod,
                                    String phoneNumber, String address, String email, String contactName,
                                    String bankAccount, PaymentMethod paymentMethod, ArrayList<Integer> supplyDays) {
        EnumSet<WeekDay> sd = null;
        if (supplyDays != null) {
            sd = EnumSet.noneOf(WeekDay.class);
            for (int day : supplyDays)
                sd.add(WeekDay.values()[day - 1]);
        }

        try {
             this.supplierController.registerNewSupplier(supplyMethod, supplierName, productCategory, deliveringMethod, phoneNumber, address, email, contactName, bankAccount, paymentMethod, sd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean updateSupplierName(int supplierID, String supplierName) {
        return this.supplierController.updateSupplierName(supplierID, supplierName);
    }

    public boolean updateSupplierDeliveringMethod(int supplierID, int deliveringMethod) {
        DeliveringMethod dm = DeliveringMethod.values()[deliveringMethod];
        return this.supplierController.updateSupplierDeliveringMethod(supplierID, dm);
    }

    public boolean updateSupplierContactInfo(int supplierID, String phoneNumber, String address, String email, String contactName) {
        return this.supplierController.updateSupplierContactInfo(supplierID, phoneNumber, address, email, contactName);
    }

    public boolean updateSupplierPaymentInfo(int supplierId, String bankAccount, int paymentMethod) {
        PaymentMethod pm = PaymentMethod.values()[paymentMethod];
        return this.supplierController.updateSupplierPaymentInfo(supplierId, bankAccount, pm);
    }

    public boolean deleteSupplier(int supplierID) {
        return this.supplierController.deleteSupplier(supplierID);
    }

    public String[] getAllSuppliersAsString() {
        return this.supplierController.getAllSuppliersAsString();
    }

    public String getSupplierAsString(int supplierID) {
        return this.supplierController.getSupplierAsString(supplierID);
    }

    public DeliveringMethod getSupplierDeliveringMethod(int supplierID) {
        return supplierController.getSupplierDeliveringMethod(supplierID);
    }

    public ProductCategory getSupplierProductCategory(int supplierID) {
        return this.supplierController.getSupplierProductCategory(supplierID);
    }

    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    public boolean registerNewContract(int supplierID, ArrayList<double[]> dataList) {
        return this.supplierController.registerNewContract(supplierID, dataList);
    }

    public String[] getSupplierContractsAsString(int supplierID) {
        return this.supplierController.GetSupplierContractsAsString(supplierID);
    }

    public String[] getAvailableContractsForOrderAsString(int orderID) {
        return this.supplierController.getAvailableContractsForOrderAsString(orderID);
    }

    public String getContractToString(int contractID) {
        return this.supplierController.getContractToString(contractID);
    }

    public String[] getAllContractToStrings(){
        return this.supplierController.getAllContractToStrings();
    }

    public Set<Integer> getAllAvailableProductsInContracts() {
        return this.supplierController.getAllAvailableProductsInContracts();
    }

    // --------------------------- ORDER FUNCTIONS ---------------------------

    public int registerNewOrder(ArrayList<int[]> dataList, Date creationDate, Date deliveryDate) {
        try {
            return this.supplierController.registerNewOrder(dataList, creationDate, deliveryDate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean registerNewScheduledOrder(int day, ArrayList<int[]> dataList) {
        WeekDay d = WeekDay.values()[day - 1];
        try {
            return this.supplierController.registerNewScheduledOrder(d, dataList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateOrderContactInfo(int orderId, String phoneNumber, String address, String email, String contactName){
        return this.supplierController.updateOrderContactInfo(orderId, phoneNumber, address, email, contactName);
    }

    public boolean updateOrderSupplyDate(int orderID, Date supplyDate){
        return this.supplierController.updateOrderSupplyDate(orderID, supplyDate);
    }

    public HashMap<Integer, Integer> updateOrderStatus(int orderID, int orderStatus) {
        OrderStatus os = OrderStatus.values()[orderStatus];
        return this.supplierController.updateOrderStatus(orderID, os);
    }

    public boolean addProductsToOrder(int orderID, ArrayList<int[]> dataList) {
        return this.supplierController.addProductsToOrder(orderID, dataList);
    }

    public boolean removeProductsFromOrder(int orderID, ArrayList<Integer> dataList) {
        return this.supplierController.removeProductsFromOrder(orderID, dataList);
    }
    public String[] getAllSupplyContractProductsAsString(){
        return supplierController.getAllSupplyContractProductsToString();
    }

    public boolean deleteOrder(int orderID) {
        return this.supplierController.deleteOrder(orderID);
    }

    public Date getOrderSupplyDate(int orderID){
        return this.supplierController.getOrderSupplyDate(orderID);
    }

    public String getOrderAsString(int orderID) {
        return this.supplierController.getOrderAsString(orderID);
    }

    public String[] getAllOrdersAsString() {
        return this.supplierController.getAllOrdersAsString();
    }

    public String[] getAllScheduledOrdersAsString() {
        try {
            return this.supplierController.getAllScheduledOrdersAsString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean orderExists(int orderID) {
        return this.supplierController.orderExists(orderID);
    }
    public void dropSuppliersData(){
        supplierController.dropData();

    }


    public String getOrderDepartureAdress(int id) {
        return "";
    }
}
