package TransportationSuppliers.Interfaces;

import java.util.HashMap;

public interface TransportationInterface {

    //For Suppliers
    void addTransportationAssignment(String sourceSite, String destinationSite, String taskDate, HashMap<String, Integer> itemsNeeded);

    void addSupplierSite(String contactName, String phoneNumber);
}