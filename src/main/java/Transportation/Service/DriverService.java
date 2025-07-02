package Transportation.Service;

import Transportation.DTO.DriverDTO;
import Transportation.Domain.DriverManager;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class DriverService {
    private final DriverManager driverManager;

    public DriverService() {
        this.driverManager = new DriverManager();
    }

    public void AddDriver(String driverId, String driverName, String licenseType) throws NullPointerException, InstanceAlreadyExistsException {
        if (driverId == null || driverName == null || licenseType == null) {
            throw new NullPointerException();
        }
        try {
            driverManager.addDriver(driverId, driverName.toLowerCase(), licenseType.toLowerCase());
        } catch (SQLException e) {
            throw new RuntimeException("Database access error - Adding driver failed");
        }    }

    public String getDriverById(String driverId) throws NullPointerException, NoSuchElementException {
        if (driverId == null) {
            throw new NullPointerException();
        }
        try {
            Optional<DriverDTO> driver = driverManager.getDriverById(driverId);
            if (driver.isPresent()) {
                DriverDTO d = driver.get();
                return "Driver ID: " + d.driverId() + "\n" +
                       "Name: " + d.driverName() + "\n" +
                       "License Types: " + d.licenseTypes() + "\n" +
                       "Available: " + (d.isAvailable() ? "Yes" : "No");
            } else {
                return "Driver not found.";
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database access error - Getting driver failed");
        }
    }

    public void deleteDriver(String driverId) throws NullPointerException, NoSuchElementException {
        if (driverId == null) {
            throw new NullPointerException();
        }
        try {
            driverManager.removeDriver(driverId);
        } catch (SQLException e) {
            throw new RuntimeException("Database access error - Removing driver failed");
        }
    }

    public boolean hasLicense(String driverId, String licenseType) throws SQLException {
        if (driverId == null || licenseType == null) {
            return false;
        }
        return driverManager.hasLicenseType(driverId, licenseType);
    }


    public void AddLicense(String driverId, String licenseType) throws NullPointerException {
        if (driverId == null || licenseType == null) {
            throw new NullPointerException();
        }
        try {
            driverManager.addLicense(driverId, licenseType);
        }
        catch (SQLException e) {
            throw new RuntimeException("Database access error - Adding license failed");
        }
    }
    public  List<DriverDTO> getAllDrivers() throws SQLException {
        try {
            return driverManager.getAllDrivers();
        }
        catch(SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }


    public String viewAllDrivers() {
       try {
           return driverManager.getAllDriversString();
       }
         catch (SQLException e) {
           throw new RuntimeException("Database access error - viewing all drivers failed");
       }
    }

    public void ChangeDriverAvailability(String driverId, boolean status) throws NullPointerException, NoSuchElementException {
        if (driverId == null) {
            throw new NullPointerException();
        }
        try {
            driverManager.setDriverAvailability(driverId, status);
        } catch (SQLException e) {
            throw new RuntimeException("Database access error - Changing driver availability failed");
        }
    }
}