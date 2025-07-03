package SuppliersModule.ServiceLayer;


import SuppliersModule.DataLayer.DTO.ProductDTO;
import SuppliersModule.DomainLayer.Enums.*;
import SuppliersModule.util.CSVReader;
import TransportationSuppliers.Integration.SupplierInterface;
import TransportationSuppliers.Integration.TransportationInterface;
import TransportationSuppliers.Integration.TransportationProvider;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ServiceController implements SupplierInterface {
    public HashMap<Integer, Integer> updateOrderStatus(int orderID, int status) {
        return null;
    }
    private final SupplierService supplierService;
    private final ProductService productService;
    private final TransportationInterface transportation;
    private String superAddress;

    private ServiceController() {
        this.supplierService = new SupplierService();
        this.productService = new ProductService();
        this.transportation = new TransportationProvider();
        this.superAddress = "Ben Gurion";
    //    executeScheduledOrders();
    }
    private static class ServiceControllerHelper {
        private static final ServiceController INSTANCE = new ServiceController();
    }
    public static ServiceController getInstance() {
        return ServiceControllerHelper.INSTANCE;
    }

    public void loadData() {
        loadProducts();
        loadSuppliers();
        loadSupplyContract();
    }
    private void executeScheduledOrders(){
        List<Integer> ordersToExecute = supplierService.executeScheduledOrders();
        if(ordersToExecute.size() == 0)
            return;
        for (Integer orderID : ordersToExecute) {
            String [] orderData = supplierService.getScheduledOrderDataForExecution(orderID);
            ArrayList<int[]> dataList = new ArrayList<>();
            int[] data = {Integer.parseInt(orderData[1]), Integer.parseInt(orderData[2]) };
            dataList.add(data);
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date tomorrow = cal.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String formattedTomorrow = formatter.format(tomorrow);
            try {
                int order = this.registerNewOrder(dataList, today, formattedTomorrow,SupplyMethod.SCHEDULED.toString());
                if(order == -1)
                    return;
                String departureAddress = supplierService.getOrderDepartureAddress(order);
                String destinationAddress = this.superAddress;
                HashMap<String, Integer> map = new HashMap<>();
                String productName = productService.getProductName(Integer.parseInt(orderData[1]));
                map.put(productName ,Integer.parseInt(orderData[2]));
                try {
                    transportation.addTransportationAssignment(departureAddress, destinationAddress, formattedTomorrow, map);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadSupplyContract() {
        List<String[]> rows = List.of();
        try {
            rows = CSVReader.loadCSV("data/contracts_data.csv");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        for (String[] row : rows) {
            ArrayList<double[]> dataList =   new ArrayList<>();
            int SupplierID = Integer.parseInt(row[0]);
            int ProductID = Integer.parseInt(row[1]);
            double productPrice = Double.parseDouble(row[2]);
            int quantity = Integer.parseInt(row[3]);
            int discount = Integer.parseInt(row[4]);
            double[] data = {ProductID, productPrice, quantity, discount};
            dataList.add(data);
            supplierService.registerNewContract(SupplierID, dataList);
        }
    }

    private  void loadSuppliers() {
        List<String[]> rows = List.of();
        try {
            rows = CSVReader.loadCSV("data/suppliers_data.csv");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        for (String[] row : rows) {
            String supplyMethod = row[0];
            String supplierName = row[1];
            String productCategory = row[2];
            String deliveryMethod = row[3];
            String phoneNumber = row[4];
            String address = row[5];
            String contactName = row[6];
            String emailAddress = row[7];
            String bankAcount = row[8];
            String paymentMethod = row[9];
            SupplyMethod sm = SupplyMethod.valueOf(supplyMethod);
            ProductCategory pc =  ProductCategory.valueOf(productCategory);
            DeliveringMethod dm = DeliveringMethod.valueOf(deliveryMethod);
            PaymentMethod pm = PaymentMethod.valueOf(paymentMethod);
            ArrayList<Integer> supplyDays = null;
            if(SupplyMethod.SCHEDULED == sm) {
                continue;
            }
            supplierService.registerNewSupplier(sm, supplierName, pc, dm, phoneNumber, address, emailAddress, contactName, bankAcount, pm, supplyDays);
            try {
                this.transportation.addSupplierSite(address, contactName, phoneNumber);
            } catch (InstanceAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
            // Add to your repository or controller
        }
    }
    private void loadProducts() {
        List<String[]> rows = List.of();
        try {
            rows = CSVReader.loadCSV("data/products_data2.csv");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        for (String[] row : rows) {
            String productName = row[0];
            String productCompanyName = row[1];
            String productCategory = row[2];
            float productWeight = Float.parseFloat(row[3]);
            ProductCategory pc =  ProductCategory.valueOf(productCategory);
            productService.registerNewProduct(productName, productCompanyName, pc, productWeight);
        }
    }











    // --------------------------- SHARED FUNCTIONS ---------------------------


    @Override
    public List<ProductDTO> getAllProducts() {
        return this.productService.getAllProducts();
    }

    @Override
    public ProductDTO getProductById(int productId) {
        return productService.getProduct(productId);
    }

    @Override
    public Float getWeightByProductId(int productId) {
        return getProductById(productId).productWeight();
    }

    @Override
    public Optional<ProductDTO> getProductByName(String productName) {
        return productService.getProductByName(productName);
    }

//    public void placeUrgentOrderSingleProduct(int ItemID, int quantity) {
//        ArrayList<int[]> dataList = new ArrayList<>();
//        dataList.add(new int[]{ItemID, quantity});
//
//        // null, null will send it as urgent for tommorow.
//        this.supplierService.registerNewOrder(dataList, null, null);
//    }

    // --------------------------- VALIDATION FUNCTIONS ---------------------------

    private boolean validateProductCategory(int productCategory) {
        return (productCategory >= 0 && productCategory < ProductCategory.values().length);
    }

    private boolean validateSupplyMethod(int supplyMethod) {
        return (supplyMethod >= 0 && supplyMethod < SupplyMethod.values().length);
    }

    private boolean validateDeliveringMethod(int deliveringMethod) {
        return (deliveringMethod >= 0 && deliveringMethod < DeliveringMethod.values().length);
    }

    private boolean validatePaymentMethod(int paymentMethod) {
        return (paymentMethod >= 0 && paymentMethod < PaymentMethod.values().length);
    }

    private boolean validateSupplierAndProduct(int supplierID, int productID) {
        return this.supplierService.getSupplierProductCategory(supplierID) == this.productService.getProductCategory(productID);
    }

    private boolean validateContractProductData(double price, double quantityForDiscount, double discountPercentage) {
        return price > 0 || quantityForDiscount > 0 || (discountPercentage > 0 && discountPercentage < 100);
    }

    private Date validateOrderDated(String supplyDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date parsedDate = sdf.parse(supplyDate);
            System.out.println("Parsed Date: " + parsedDate);
            return parsedDate;
        } catch (ParseException e) {
            System.out.println("Invalid Date Format");
        }
        return null;
    }

    private Date validateDate(String strDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // Makes sure it strictly checks the format (e.g., no 31/02/2024)
        try {
            return sdf.parse(strDate); // Returns a java.util.Date object
        } catch (ParseException e) {
            return null; // If invalid, return null
        }
    }

    private boolean validateDay(int day) {
        return day >= 1 && day <= 7;
    }

    private boolean validateDays(ArrayList<Integer> days) {
        if (days == null)
            return true;

        for (int day : days)
            if (!validateDay(day))
                return false;
        return true;
    }

    private boolean validateOrderStatus(int orderStatus) {
        return (orderStatus >= 0 && orderStatus < OrderStatus.values().length);
    }


    // --------------------------- PRODUCT FUNCTIONS ---------------------------

    public int registerNewProduct(String productName, String productCompanyName, int productCategory, float productWeight) {
        if (validateProductCategory(productCategory))
            return this.productService.registerNewProduct(productName, productCompanyName, ProductCategory.values()[productCategory],  productWeight);
        return -1;
    }

    public boolean updateProduct(int productID, String productName, String productCompanyName, float productWeight) {
        return this.productService.updateProduct(productID, productName, productCompanyName,  productWeight);
    }

    public boolean deleteProduct(int productID) {
        return this.productService.deleteProduct(productID);
    }

    public String[] getAllProductsAsStrings() {
        return this.productService.getProductsAsString();
    }

    public String getProductAsString(int productID) {
        return this.productService.getProductAsString(productID);
    }


    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public int registerNewSupplier(int supplyMethod, String supplierName, int productCategory, int deliveringMethod,
                                   String phoneNumber, String address, String email, String contactName,
                                   String bankAccount, int paymentMethod, ArrayList<Integer> supplyDays) {
        int id = -1;
        if (this.validateProductCategory(productCategory) && this.validateSupplyMethod(supplyMethod) && this.validateDeliveringMethod(deliveringMethod) &&
                this.validatePaymentMethod(paymentMethod) && validateDays(supplyDays)) {
            id = this.supplierService.registerNewSupplier(supplyMethod, supplierName, productCategory, deliveringMethod, phoneNumber, address, email, contactName, bankAccount, paymentMethod, supplyDays);
        }
        if(id != -1){
            try {
                this.transportation.addSupplierSite(address, contactName, phoneNumber);
            } catch (InstanceAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        }
        return id;
    }

    public boolean deleteSupplier(int supplierID) {
        return supplierService.deleteSupplier(supplierID);
    }

    public boolean updateSupplierName(int supplierID, String supplierName) {
        return this.supplierService.updateSupplierName(supplierID, supplierName);
    }

    public boolean updateSupplierDeliveringMethod(int supplierID, int deliveringMethod) {
        if (this.validateDeliveringMethod(deliveringMethod))
            return this.supplierService.updateSupplierDeliveringMethod(supplierID, deliveringMethod);
        return false;
    }

    public boolean updateSupplierContactInfo(int supplierID, String phoneNumber, String address, String email, String contactName) {
        return this.supplierService.updateSupplierContactInfo(supplierID, phoneNumber, address, email, contactName);
    }

    public boolean updateSupplierPaymentInfo(int supplierId, String bankAccount, int paymentMethod) {
        if (this.validatePaymentMethod(paymentMethod))
            return this.supplierService.updateSupplierPaymentInfo(supplierId, bankAccount, paymentMethod);
        return false;
    }

    public String[] getAllSuppliersAsString() {
        return this.supplierService.getAllSuppliersAsString();
    }

    public String getSupplierAsString(int supplierID) {
        return this.supplierService.getSupplierAsString(supplierID);
    }


    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    public boolean registerNewContract(int supplierID, ArrayList<double[]> dataList) {
        for (double[] data : dataList)
            if (!validateSupplierAndProduct(supplierID, (int) data[0]) || !validateContractProductData(data[1], data[2], data[3])) {
                System.out.println(validateSupplierAndProduct(supplierID, (int) data[0]));
                System.out.println(validateContractProductData(data[1], data[2], data[3]));
                return false;
            }


        return this.supplierService.registerNewContract(supplierID, dataList);
    }

    public String[] getSupplierContractsAsString(int supplierID) {
        return this.supplierService.getSupplierContractsAsString(supplierID);
    }
    public String getContractsAsString(int contractID) {
        return supplierService.getContractToString(contractID);
    }

    public String[] getAvailableContractsForOrderAsString(int orderID) {
        return this.supplierService.getAvailableContractsForOrderAsString(orderID);
    }
    public String[] getAllContractToStrings(){
        return this.supplierService.getAllContractToStrings();
    }
    public String[] getAllSupplyContractProductsAsString(){
        return supplierService.getAllSupplyContractProductsAsString();
    }
    public void printSupplyContractDetails() {
        String[] supplyContractDTOs = getAllContractToStrings();
        String[] productDTOs = getAllProductsAsStrings();
        String[] supplyContractProductDataDTOs = getAllSupplyContractProductsAsString();

        for (String contractStr : supplyContractDTOs) {
            int contractId = extractIntValue(contractStr, "supplyContractID");
            int supplierId = extractIntValue(contractStr, "supplierID");
            String supplier = getSupplierAsString(supplierId);
            String supplyMethod = extractStringValue(supplier, "supplyMethod");
            String result = contractStr.substring(0, contractStr.length() - 1);
            result = result + ", " + supplyMethod + "}";
            System.out.println(result);
            for (String productDataStr : supplyContractProductDataDTOs) {
                if (extractIntValue(productDataStr, "supplyContractID") == contractId) {
                    int productId = extractIntValue(productDataStr, "productID");

                    // Match the product
                    for (String productStr : productDTOs) {
                        //    System.out.println("Looking for productId: " + productId);
                        //  System.out.println("Matching against: " + productStr);

                        if (extractIntValue(productStr, "id") == productId) {
                            String name = extractStringValue(productStr, "name");
                            String company = extractStringValue(productStr, "company_name");
                            String category = extractStringValue(productStr, "product_category");
                            double weight = extractDoubleValue(productStr, "product_weight");

                            System.out.println("  Product ID: " + productId +
                                    ", Name: " + name +
                                    ", Company: " + company +
                                    ", Category: " + category +
                                    ", Weight: " + weight);
                            break;
                        }
                    }

                    // Print discount info
                    double price = extractDoubleValue(productDataStr, "productPrice");
                    int quantity = extractIntValue(productDataStr, "quantityForDiscount");
                    double discount = extractDoubleValue(productDataStr, "discountPercentage");

                    System.out.println("    Price: " + price +
                            ", Quantity for Discount: " + quantity +
                            ", Discount: " + discount + "%");
                }
            }
            System.out.println(); // space between contracts
        }
    }


    private static String extractStringValue(String line, String label) {
        try {
            int startIndex = line.indexOf(label + "=");
            if (startIndex == -1) return "";
            startIndex += label.length() + 1;
            int endIndex = line.indexOf(",", startIndex);
            if(label.equals("supplierID") || label.equals("supplyMethod"))
                endIndex = line.indexOf("}", startIndex);
            if(label.equals("product_weight") || label.equals("discountPercentage")) {
                endIndex = line.indexOf("}", startIndex);
            }

            if (endIndex == -1) endIndex = line.length();
            return line.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            return "";
        }
    }



    private static int extractIntValue(String line, String key) {
        try {
            return Integer.parseInt(extractStringValue(line, key));
        } catch (Exception e) {
            return -1;
        }
    }

    private static double extractDoubleValue(String line, String key) {
        try {
            return Double.parseDouble(extractStringValue(line, key));
        } catch (Exception e) {
            return -1;
        }
    }


    // --------------------------- ORDER FUNCTIONS ---------------------------

    public int registerNewOrder(ArrayList<int[]> dataList, Date creationDate, String deliveryDate, String type) throws SQLException {
        Date deliveryDateAsDate;
        if (deliveryDate.equalsIgnoreCase("t"))
            deliveryDateAsDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        else
            deliveryDateAsDate = this.validateOrderDated(deliveryDate);

        if (deliveryDateAsDate == null)
            return -1;
        if (deliveryDateAsDate.before(creationDate))
            return -1;


       int id =  this.supplierService.registerNewOrder(dataList, creationDate, deliveryDateAsDate, type);
       //transportation.addSupplierSite();
       return id;

    }

    public boolean registerNewScheduledOrder(int day, ArrayList<int[]> dataList) {
        if (!validateDay(day))
            return false;

        return this.supplierService.registerNewScheduledOrder(day, dataList);
    }

    public boolean deleteOrder(int orderID) {
        return this.supplierService.deleteOrder(orderID);
    }

    public boolean orderExists(int orderID) {
        return supplierService.orderExists(orderID);
    }

    public boolean updateOrderContactInfo(int orderId,  String phoneNumber, String address, String email, String contactName){
        return this.supplierService.updateOrderContactInfo(orderId, phoneNumber, address, email, contactName);
    }

    public boolean updateOrderSupplyDate(int orderID, String supplyDate){
        Date supplyDateAsDate = this.validateDate(supplyDate);
        if(supplyDateAsDate == null)
            return false;
        Date oldSupplyDate = supplierService.getOrderSupplyDate(orderID);
        if(supplyDateAsDate.before(oldSupplyDate))
            return false;
        return this.supplierService.updateOrderSupplyDate(orderID, supplyDateAsDate);
    }



    public boolean addProductsToOrder(int orderID, ArrayList<int[]> dataList) {
        return this.supplierService.addProductsToOrder(orderID, dataList);
    }

    public boolean removeProductsFromOrder(int orderID, ArrayList<Integer> dataList) {
        return this.supplierService.removeProductsFromOrder(orderID, dataList);
    }

    public String getOrderAsString(int orderID) {
        return this.supplierService.getOrderAsString(orderID);
    }

    public String[] getAllOrdersAsString() {
        return this.supplierService.getAllOrdersAsString();
    }

    public String[] getAllScheduledOrdersAsString() {
        return this.supplierService.getAllScheduledOrdersAsString();
    }
    // supplier interface




}
