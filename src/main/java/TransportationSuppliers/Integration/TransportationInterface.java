package TransportationSuppliers.Integration;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.text.ParseException;
import java.util.HashMap;

public interface TransportationInterface {

    //For Suppliers
    void addTransportationAssignment(String sourceSite, String destinationSite, String taskDate, HashMap<String, Integer> itemsNeeded) throws ParseException;

    void addSupplierSite(String supplierAddress, String contactName, String phoneNumber) throws InstanceAlreadyExistsException;
    public void updateSupplierSite(String supplierAddress, String contactName, String phoneNumber) throws InstanceNotFoundException;
    public void removesSupplierSite(String supplierAddress) throws InstanceNotFoundException;
}