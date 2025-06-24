package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.OrderControllerDTO;
import SuppliersModule.DataLayer.OrderDTO;
import SuppliersModule.DataLayer.OrderProductDataDTO;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.OrderStatus;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class OrderController {
    int numOfOrders = 0;
    ArrayList<Order> ordersArrayList;

    OrderControllerDTO orderControllerDTO;

    public OrderController() {
        this.numOfOrders = 0;

        this.ordersArrayList = new ArrayList<>();

        this.orderControllerDTO = OrderControllerDTO.getInstance();
        for (OrderDTO dto : orderControllerDTO.getAllOrders()) {
            Order order = dto.convertDTOToEntity();
            for (OrderProductDataDTO pdDTO : orderControllerDTO.getOrderProductDataByOrderID(dto))
                order.addOrderProductData(pdDTO.convertDTOToEntity());

            ordersArrayList.add(order);
            this.numOfOrders++;
        }
    }

    private static boolean ValidateProductInContracts(SupplyContract supplyContract, int productID) {
        return supplyContract.CheckIfProductInData(productID);
    }

    public static ArrayList<OrderProductData> buildProductDataArray(ArrayList<int[]> dataList, ArrayList<SupplyContract> supplyContracts) {

        ArrayList<OrderProductData> productDataList = new ArrayList<>();

        for (int[] entry : dataList) {

            int productId = entry[0];

            SupplyContract supplyContract = null;
            for (SupplyContract contract : supplyContracts)
                if (ValidateProductInContracts(contract, productId)) {
                    supplyContract = contract;
                    break;
                }
            if (supplyContract == null)
                return null;

            SupplyContractProductData data = supplyContract.getSupplyContractProductDataOfProduct(productId);

            int quantity = entry[1];
            double productPrice = data.getProductPrice();

            if (quantity >= data.getQuantityForDiscount())
                productPrice = productPrice * ((100 - data.getDiscountPercentage()) / 100);


            productDataList.add(new OrderProductData(productId, quantity, productPrice));
        }

        return productDataList;
    }

    private double calculateTotalPrice(ArrayList<OrderProductData> dataList) {
        double totalPrice = 0;
        for (OrderProductData data : dataList)
            totalPrice += data.getTotalPrice();

        return totalPrice;
    }

    public boolean registerNewOrder(int supplierId, ArrayList<int[]> dataList, ArrayList<SupplyContract> supplyContracts, Date creationDate, Date deliveryDate, DeliveringMethod deliveringMethod, SupplyMethod supplyMethod, ContactInfo supplierContactInfo) {
        if (creationDate == null) {
            LocalDate today = LocalDate.now();
            creationDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
            if (deliveryDate == null)
                deliveryDate = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        }

        ArrayList<OrderProductData> orderProductDataList = buildProductDataArray(dataList, supplyContracts);
        if (orderProductDataList == null)
            return false;

        for (OrderProductData orderProductData : orderProductDataList) {
            orderProductData.setOrderID(numOfOrders);
            orderProductData.orderProductDataDTO.Insert();
        }

        double totalOrderValue = calculateTotalPrice(orderProductDataList);

        Order order = new Order(numOfOrders, supplierId, orderProductDataList, totalOrderValue, creationDate, deliveryDate, deliveringMethod, supplyMethod, supplierContactInfo);

        this.orderControllerDTO.insertOrder(order.orderDTO);

        ordersArrayList.add(order);

        this.numOfOrders++;
        return true;
    }

    private Order getOrderByID(int orderID) {
        for (Order order : ordersArrayList)
            if(order.orderID == orderID)
                return order;

        return null;
    }

    public boolean deleteOrder(int orderID){
        Order order = getOrderByID(orderID);

        order.orderDTO.Delete();

        return this.ordersArrayList.removeIf(o -> o.orderID == orderID);
    }

    public boolean removeAllSupplierOrders(int supplierID) {
        return this.ordersArrayList.removeIf(order -> order.supplierID == supplierID);
    }

    public boolean orderExists(int orderID) {
        Order order = getOrderByID(orderID);
        return order != null;
    }

    // ********** UPDATE FUNCTIONS **********

    public boolean updateOrderContactInfo(int orderID, String phoneNumber, String address, String email, String contactName){
        for (Order order : ordersArrayList) {
            if(order.orderID == orderID){
                ContactInfo newContactInfo = new ContactInfo(phoneNumber, address, email, contactName);

                order.setSupplierContactInfo(newContactInfo);
                order.orderDTO.Update();

                return true;
            }
        }
        return false;
    }

    public boolean updateOrderSupplyDate(int orderID, Date supplyDate){
        for (Order order : ordersArrayList) {
            if(order.orderID == orderID){
                order.setSupplyDate(supplyDate);
                order.orderDTO.Update();
                return true;
            }
        }
        return false;
    }

    public HashMap<Integer, Integer> updateOrderStatus(int orderID, OrderStatus orderStatus){
        Order order = getOrderByID(orderID);
        if (order != null) {
            order.setOrderStatus(orderStatus);
            order.orderDTO.Update();

            if (order.orderStatus == OrderStatus.ARRIVED) {
                HashMap<Integer, Integer> map = new HashMap<>();
                for (OrderProductData orderProductData : order.getProductArrayList())
                    map.put(orderProductData.getProductID(), orderProductData.getProductQuantity());
                return map;
            }

        }
        return null;
    }

    public Date getOrderSupplyDate(int orderID){
        for (Order order : ordersArrayList) {
            if(order.orderID == orderID){
                return order.getSupplyDate();
            }
        }
        return null;
    }

    public boolean addProductsToOrder(int orderID, ArrayList<SupplyContract> supplyContracts ,ArrayList<int[]> dataList) {
        ArrayList<OrderProductData> products = this.getOrderProducts(orderID);
        if(products == null)
            return false;

        ArrayList<OrderProductData> newProducts = buildProductDataArray(dataList, supplyContracts);
        for (OrderProductData orderProductData : newProducts)
            orderProductData.orderProductDataDTO.Insert();

        products.addAll(newProducts);

        double totalPrice = calculateTotalPrice(products);

        this.setOrderProducts(orderID, products);
        this.setOrderPrice(orderID, totalPrice);
        return true;
    }

    public boolean removeProductsFromOrder(int orderID, ArrayList<Integer> dataList) {
        ArrayList<OrderProductData> products = this.getOrderProducts(orderID);
        if(products == null)
            return false;

        for (int productID : dataList)
            products.removeIf(orderProductData -> orderProductData.getProductID() == productID);

        if (products.isEmpty()){
            this.deleteOrder(orderID);
            return true;
        }

        double totalPrice = calculateTotalPrice(products);

        this.setOrderProducts(orderID, products);
        this.setOrderPrice(orderID, totalPrice);

        return true;
    }

    // ********** GETTERS FUNCTIONS **********

    public int getOrderSupplierID(int orderID){
        Order order = getOrderByID(orderID);
        if (order != null)
            return order.getSupplierID();

        return -1;
    }

    public ArrayList<OrderProductData> getOrderProducts(int orderID){
        Order order = getOrderByID(orderID);
        if (order != null)
            return order.getProductArrayList();

        return null;
    }

    // ********** SETTERS FUNCTIONS **********

    private boolean setOrderProducts(int orderID, ArrayList<OrderProductData> productArrayList){
        Order order = getOrderByID(orderID);
        if (order == null)
            return false;

        order.setProductArrayList(productArrayList);
        order.orderDTO.Update();
        return true;
    }

    private boolean setOrderPrice(int orderID, double price){
        Order order = getOrderByID(orderID);
        if(order == null)
            return false;

        order.setTotalPrice(price);
        order.orderDTO.Update();
        return true;
    }

    // ********** OUTPUT FUNCTIONS **********

    public String getOrderAsString(int orderID){
        Order order = getOrderByID(orderID);
        if (order != null)
            return order.toString();

        return null;
    }

    public String[] getAllOrdersAsString() {
        String[] ordersAsString = new String[ordersArrayList.size()];
        for (int i = 0; i < ordersArrayList.size(); i++) {
            ordersAsString[i] = ordersArrayList.get(i).toString();
        }
        return ordersAsString;
    }
}
