package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.DTO.OrderDTO;
import SuppliersModule.DataLayer.DTO.OrderProductDataDTO;
import SuppliersModule.DataLayer.DTO.SupplyContractDTO;
import SuppliersModule.DataLayer.DTO.SupplyContractProductDataDTO;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.OrderStatus;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;
import SuppliersModule.DomainLayer.Repositories.IOrderProductDataRepository;
import SuppliersModule.DomainLayer.Repositories.IOrderRepository;
import SuppliersModule.DomainLayer.Repositories.OrderProductDataRepositoryImpl;
import SuppliersModule.DomainLayer.Repositories.OrderRepositoryImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class OrderController {
    private int numOfOrders;
    private final List<OrderDTO> ordersArrayList;

    private final IOrderRepository orderRepository;
    private final IOrderProductDataRepository orderProductDataRepository;


    public OrderController() {
        this.numOfOrders = 0;
        this.ordersArrayList = new ArrayList<>();
        this.orderRepository = new OrderRepositoryImpl();
        this.orderProductDataRepository = new OrderProductDataRepositoryImpl();
    }


    private boolean validateProductInContracts(SupplyContractDTO supplyContract, int productID) {
        List<SupplyContractProductDataDTO> productDataList =
                orderProductDataRepository.findByContractId(supplyContract.supplyContractID());

        for (SupplyContractProductDataDTO data : productDataList) {
            if (data.productID().equals(productID)) {
                return true;
            }
        }

        return false;
    }



    public ArrayList<OrderProductDataDTO> buildProductDataArray(
            ArrayList<int[]> dataList,
            ArrayList<SupplyContractDTO> supplyContracts) {

        ArrayList<OrderProductDataDTO> productDataList = new ArrayList<>();

        for (int[] entry : dataList) {
            int productId = entry[0];
            int quantity = entry[1];

            boolean found = false;

            for (SupplyContractDTO contract : supplyContracts) {
                List<SupplyContractProductDataDTO> productDataForContract =
                        orderProductDataRepository.findContractProductsByContractId(contract.supplyContractID());

                for (SupplyContractProductDataDTO productData : productDataForContract) {
                    if (productData.productID().equals(productId)) {
                        double price = productData.productPrice();
                        if (quantity >= productData.quantityForDiscount()) {
                            price *= ((100 - productData.discountPercentage()) / 100.0);
                        }

                        productDataList.add(new OrderProductDataDTO(
                                contract.supplyContractID(),
                                productId,
                                quantity,
                                 price // or `price` if your DTO takes `Double`
                        ));
                        found = true;
                        break;
                    }
                }

                if (found) break;

            }

            if (!found) return null; // No matching product found in any contract
        }

        return productDataList;
    }



    private double calculateTotalPriceDTOs(List<OrderProductDataDTO> dataList) {
        double total = 0;
        for (OrderProductDataDTO dto : dataList) {
            total += dto.productPrice() * dto.productQuantity();
        }
        return total;
    }


    public boolean registerNewOrder(int supplierId,
                                    ArrayList<int[]> dataList,
                                    List<SupplyContractDTO> supplyContracts,
                                    Date creationDate,
                                    Date deliveryDate,
                                    DeliveringMethod deliveringMethod,
                                    SupplyMethod supplyMethod,
                                    ContactInfo supplierContactInfo) {
        if (creationDate == null) {
            LocalDate today = LocalDate.now();
            creationDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        if (deliveryDate == null) {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            deliveryDate = Date.from(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        ArrayList<OrderProductDataDTO> orderProductDataList = buildProductDataArray(dataList, (ArrayList<SupplyContractDTO>) supplyContracts);
        if (orderProductDataList == null) return false;

        double totalOrderValue = calculateTotalPriceDTOs(orderProductDataList);

        OrderDTO orderDTO = new OrderDTO(
                numOfOrders,
                supplierId,
                supplierContactInfo.phoneNumber,
                supplierContactInfo.address,
                supplierContactInfo.email,
                supplierContactInfo.name,
                deliveringMethod.toString(),
                creationDate.toString(),
                deliveryDate.toString(),
                totalOrderValue,
                OrderStatus.PENDING.toString(),
                supplyMethod.toString()
        );

        orderRepository.insertOrder(orderDTO);

        for (OrderProductDataDTO data : orderProductDataList) {
            OrderProductDataDTO dto = new OrderProductDataDTO(
                    numOfOrders,
                    data.productID(),
                    data.productQuantity(),
                    data.productPrice() // if needed
            );
            orderProductDataRepository.insert(dto);
        }

        ordersArrayList.add(orderDTO);
        numOfOrders++;
        return true;

    }



    private OrderDTO getOrderByID(int orderID) {
        for (OrderDTO order : ordersArrayList)
            if (order.orderID() == orderID) return order;

        return null;
    }

    public boolean deleteOrder(int orderID) {
        OrderDTO order = getOrderByID(orderID);
        try {
            orderRepository.deleteOrder(orderID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this.ordersArrayList.removeIf(o -> o.orderID() == orderID);
    }

    public boolean removeAllSupplierOrders(int supplierID) {
        try {
            List<OrderDTO> allOrders = orderRepository.getAllOrders();
            List<OrderDTO> supplierOrders = new ArrayList<>();

            for (OrderDTO order : allOrders) {
                if (order.supplierID().equals(supplierID)) {
                    supplierOrders.add(order);
                }
            }

            // Delete them from the DB
            for (OrderDTO order : supplierOrders) {
                orderRepository.deleteOrder(order.orderID());
                orderProductDataRepository.deleteAllByOrderId(order.orderID());
            }

            // Remove them from in-memory list
            ordersArrayList.removeIf(order -> order.supplierID() == supplierID);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean orderExists(int orderID) {
        try {
            Optional<OrderDTO> maybeOrder = orderRepository.getOrderById(orderID);
            return maybeOrder.isPresent();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ********** UPDATE FUNCTIONS **********

    public boolean updateOrderContactInfo(int orderID, String phoneNumber, String address, String email, String contactName) {
        Optional<OrderDTO> optionalOrder = orderRepository.findById(orderID);
        if (optionalOrder.isEmpty()) return false;

        OrderDTO oldDTO = optionalOrder.get();

        // Create new DTO with updated contact info fields
        OrderDTO updatedDTO = new OrderDTO(oldDTO.orderID(), oldDTO.supplierID(), phoneNumber, address, email, contactName, oldDTO.deliveryMethod(), oldDTO.orderDate(), oldDTO.deliveryDate(), oldDTO.totalPrice(), oldDTO.orderStatus(), oldDTO.supplyMethod());

        orderRepository.update(updatedDTO);
        return true;

    }


    public boolean updateOrderSupplyDate(int orderID, Date newSupplyDate) {
        Optional<OrderDTO> optionalOrderDTO = orderRepository.findById(orderID);
        if (optionalOrderDTO.isEmpty()) return false;

        OrderDTO oldDTO = optionalOrderDTO.get();

        // Create new DTO with updated supply date
        OrderDTO updatedDTO = new OrderDTO(oldDTO.orderID(), oldDTO.supplierID(), oldDTO.phoneNumber(), oldDTO.physicalAddress(), oldDTO.emailAddress(), oldDTO.contactName(), oldDTO.deliveryMethod(), oldDTO.orderDate(), newSupplyDate.toString(), oldDTO.totalPrice(), oldDTO.orderStatus(), oldDTO.supplyMethod());

        orderRepository.update(updatedDTO);
        return true;

    }


    public HashMap<Integer, Integer> updateOrderStatus(int orderID, OrderStatus newStatus) {
        Optional<OrderDTO> optionalOrderDTO = orderRepository.findById(orderID);
        if (optionalOrderDTO.isEmpty()) return null;

        OrderDTO orderDTO = optionalOrderDTO.get();

        // Create a new DTO with updated status
        OrderDTO updatedOrder = new OrderDTO(orderDTO.orderID(), orderDTO.supplierID(), orderDTO.phoneNumber(), orderDTO.physicalAddress(), orderDTO.emailAddress(), orderDTO.contactName(), orderDTO.deliveryMethod(), orderDTO.orderDate(), orderDTO.deliveryDate(), orderDTO.totalPrice(), newStatus.toString(), orderDTO.supplyMethod());

        // Persist the change via repository
        orderRepository.update(updatedOrder);

        if (newStatus == OrderStatus.ARRIVED) {
            List<OrderProductDataDTO> productDataList = orderProductDataRepository.findByOrderId(orderID);
            HashMap<Integer, Integer> productQuantities = new HashMap<>();

            for (OrderProductDataDTO dto : productDataList) {
                productQuantities.put(dto.productID(), dto.productQuantity());
            }

            return productQuantities;
        }

        return null;
    }


    public Date getOrderSupplyDate(int orderID) {
        for (OrderDTO order : ordersArrayList) {
            if (order.orderID() == orderID) {
                LocalDate localDate = LocalDate.parse(order.deliveryDate()); // parse ISO-8601 string
                return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
        }
        return null;
    }

    public boolean addProductsToOrder(int orderID, ArrayList<SupplyContractDTO> supplyContracts, ArrayList<int[]> dataList) {
        // Get current products for the order from repository
        List<OrderProductDataDTO> existingProducts = orderProductDataRepository.findByOrderId(orderID);
        if (existingProducts == null) return false;

        // Build new product data from input
        ArrayList<OrderProductDataDTO> newProductEntities = buildProductDataArray(dataList, supplyContracts);
        if (newProductEntities == null) return false;

        // Convert and insert new products
        for (OrderProductDataDTO newProduct : newProductEntities) {
            OrderProductDataDTO dto = new OrderProductDataDTO(orderID, newProduct.productID(), newProduct.productQuantity(), newProduct.productPrice());
            orderProductDataRepository.insert(dto);
        }

        // Merge and recalculate price
        List<OrderProductDataDTO> allProducts = new ArrayList<>(existingProducts);
        for (OrderProductDataDTO opd : newProductEntities) {
            allProducts.add(new OrderProductDataDTO(orderID, opd.productID(), opd.productQuantity(), opd.productPrice()));
        }

        double totalPrice = calculateTotalPriceDTOs(allProducts);

        // Update order total price
        setOrderPrice(orderID, totalPrice);

        return true;
    }


    public boolean removeProductsFromOrder(int orderID, ArrayList<Integer> dataList) {
        ArrayList<OrderProductDataDTO> products = this.getOrderProducts(orderID);
        if (products == null) return false;

        for (int productID : dataList)
            products.removeIf(orderProductData -> orderProductData.productID() == productID);

        if (products.isEmpty()) {
            this.deleteOrder(orderID);
            return true;
        }

        double totalPrice = calculateTotalPriceDTOs(products);

        this.setOrderProducts(orderID, products);
        this.setOrderPrice(orderID, totalPrice);

        return true;
    }

    // ********** GETTERS FUNCTIONS **********

    public int getOrderSupplierID(int orderID) {
        OrderDTO order = getOrderByID(orderID);
        if (order != null) return order.supplierID();

        return -1;
    }

    public ArrayList<OrderProductDataDTO> getOrderProducts(int orderID) {
        List<OrderProductDataDTO> productList = orderProductDataRepository.findByOrderId(orderID);
        return new ArrayList<>(productList);
    }


    // ********** SETTERS FUNCTIONS **********

    private boolean setOrderProducts(int orderID, ArrayList<OrderProductDataDTO> productArrayList) {
        // Delete all existing product data for the order
        orderProductDataRepository.deleteAllByOrderId(orderID);

        // Insert updated product list
        for (OrderProductDataDTO product : productArrayList) {
            OrderProductDataDTO dto = new OrderProductDataDTO(orderID, product.productID(), product.productQuantity(), product.productPrice());
            orderProductDataRepository.insert(dto);
        }

        return true;

    }


    private boolean setOrderPrice(int orderID, double price) {
        Optional<OrderDTO> maybeOrder = orderRepository.findById(orderID);
        if (maybeOrder.isEmpty()) return false;

        OrderDTO oldOrder = maybeOrder.get();
        OrderDTO updatedOrder = new OrderDTO(oldOrder.orderID(), oldOrder.supplierID(), oldOrder.phoneNumber(), oldOrder.physicalAddress(), oldOrder.emailAddress(), oldOrder.contactName(), oldOrder.deliveryMethod(), oldOrder.orderDate(), oldOrder.deliveryDate(), price, // updated price
                oldOrder.orderStatus(), oldOrder.supplyMethod());

        orderRepository.update(updatedOrder);
        return true;
    }


    // ********** OUTPUT FUNCTIONS **********

    public String getOrderAsString(int orderID) {
        OrderDTO order = getOrderByID(orderID);
        if (order != null) return order.toString();

        return null;
    }

    public String[] getAllOrdersAsString() {
        String[] ordersAsString = new String[ordersArrayList.size()];
        for (int i = 0; i < ordersArrayList.size(); i++) {
            ordersAsString[i] = ordersArrayList.get(i).toString();
        }
        return ordersAsString;
    }

    public List<OrderDTO> getOrdersBySupplierId(Integer supplierId) {
        return orderRepository.findAll().stream()
                .filter(order -> order.supplierID().equals(supplierId))
                .toList();
    }


}
