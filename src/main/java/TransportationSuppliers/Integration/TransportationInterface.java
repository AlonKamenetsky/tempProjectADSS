package TransportationSuppliers.Integration;

import javax.management.InstanceNotFoundException;
import java.text.ParseException;
import java.util.HashMap;

public interface TransportationInterface {

    //For Suppliers
    void addTransportationAssignment(String sourceSite, String choiceDestinationSite, String contactName, String phoneNumber, String taskDate, HashMap<String, Integer> itemsNeeded) throws ParseException;

    void removesSupplierSite(String supplierAddress) throws InstanceNotFoundException;

    void updateSupplierSite(String supplierAddress, String contactName, String phoneNumber) throws InstanceNotFoundException;
}