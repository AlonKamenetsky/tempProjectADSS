package Transportation.Service;

import Transportation.DTO.TruckDTO;
import Transportation.Domain.TruckManager;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TruckService {
    private final TruckManager truckManager;

    public TruckService() {
        this.truckManager = new TruckManager();
    }

    //for mock tests
    public TruckService(TruckManager truckManager1) {
        this.truckManager = truckManager1;
    }

    public void AddTruck(String truckType, String licenseNumber, String model, float netWeight, float maxWeight) throws NullPointerException, InstanceAlreadyExistsException {
        if (truckType == null || licenseNumber == null || model == null || netWeight < 0 || maxWeight < 0) {
            throw new NullPointerException();
        }
        try {
            truckManager.addTruck(truckType.toLowerCase(), licenseNumber, model.toLowerCase(), netWeight, maxWeight);
        } catch (SQLException e) {
            throw new RuntimeException("Database access error - Adding truck failed");
        }
    }

    public void deleteTruck(String licenseNumber) throws NullPointerException, NoSuchElementException {
        if (licenseNumber == null) {
            throw new NullPointerException();
        }
        try {
            int truckId = truckManager.getTruckIdByLicense(licenseNumber);
            truckManager.removeTruck(truckId);
        } catch (SQLException e) {
            throw new RuntimeException("Database access error - Removing truck failed");
        }
    }

    public String viewAllTrucks() {
        try {
            return truckManager.getAllTrucksString();
        } catch (SQLException e) {
            throw new RuntimeException("Database access error - View all trucks failed");
        }
    }

    public List<TruckDTO> getAllTrucks() {
        try {
            return truckManager.getAllTrucks();
        } catch (SQLException e) {
            throw new RuntimeException("Database access error - Getting all trucks failed");
        }
    }


    public String getTruckByLicenseNumber(String licenseNumber) throws NullPointerException, NoSuchElementException {
        if (licenseNumber == null) {
            throw new NullPointerException();
        }
        try {
            int truckId = truckManager.getTruckIdByLicense(licenseNumber);
            Optional<TruckDTO> truck = truckManager.getTruckById(truckId);
            if (truck.isPresent()) {
                TruckDTO t = truck.get();
                return "Truck ID: " + t.truckId() + "\n" +
                        "License: " + t.licenseNumber() + "\n" +
                        "Type: " + t.truckType() + "\n" +
                        "Model: " + t.model() + "\n" +
                        "Net Weight: " + t.netWeight() + "\n" +
                        "Max Weight: " + t.maxWeight() + "\n" +
                        "Available: " + (t.isFree() ? "Yes" : "No");
            } else {
                return "Truck not found.";
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database access error - Getting truck failed");
        }
    }


    public void ChangeTruckAvailability(String licenseNumber, boolean status) throws NullPointerException, NoSuchElementException {
        if (licenseNumber == null) {
            throw new NullPointerException();
        }
        try {
            int truckId = truckManager.getTruckIdByLicense(licenseNumber);
            truckManager.setTruckAvailability(truckId, status);
        } catch (SQLException e) {
            throw new RuntimeException("Database access error - Changing truck availability failed");
        }
    }
}