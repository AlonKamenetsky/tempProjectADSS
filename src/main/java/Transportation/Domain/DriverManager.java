package Transportation.Domain;

import Transportation.DTO.DriverDTO;

import Transportation.Domain.Repositories.DriverRepository;
import Transportation.Domain.Repositories.DriverRepositoryImpli;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.*;

public class DriverManager {
    private final DriverRepository driverRepository;

    public DriverManager() {
        this.driverRepository = new DriverRepositoryImpli();
    }

    //for mock tests
    public DriverManager(DriverRepository driverRepository1) {
        this.driverRepository = driverRepository1;
    }

    public void addDriver(String driverId, String driverName, String licenseType) throws SQLException, InstanceAlreadyExistsException {
        if (driverRepository.findDriverById(driverId).isEmpty()) {
            driverRepository.addDriver(driverId, driverName, licenseType, true);
        } else {
            throw new InstanceAlreadyExistsException();
        }
    }

    public void removeDriver(String driverId) throws SQLException, NoSuchElementException {
        if (driverRepository.findDriverById(driverId).isEmpty()) {
            throw new NoSuchElementException();
        } else {
            driverRepository.deleteDriver(driverId);
        }
    }

    public List<DriverDTO> getAllDrivers() throws SQLException {
        return driverRepository.getAllDrivers();
    }

    public Optional<DriverDTO> getDriverById(String driverId) throws SQLException {
        return driverRepository.findDriverById(driverId);
    }

    public boolean hasLicenseType(String driverId, String licenseType) throws SQLException {
        List<DriverDTO> driverWithLicense = driverRepository.findByLicenseType(licenseType);
        for (DriverDTO d : driverWithLicense) {
            if (d.driverId().equals(driverId)) {
                return true;
            }
        }
        return false;
    }

    public void addLicense(String driverId, String licenseType) throws SQLException {
        driverRepository.addLicenseToDriver(driverId, licenseType);
    }

    public void setDriverAvailability(String driverId, boolean status) throws SQLException, NoSuchElementException {
        Optional<DriverDTO> driverToChange = driverRepository.findDriverById(driverId);
        if (driverToChange.isPresent()) {
            driverRepository.updateAvailability(driverId, status);
        } else {
            throw new NoSuchElementException();
        }
    }

    public String getAllDriversString() throws SQLException {
        List<DriverDTO> drivers = driverRepository.getAllDrivers();
        if (drivers.isEmpty()) return "No drivers available.";

        StringBuilder sb = new StringBuilder("All Drivers:\n");
        for (DriverDTO d : drivers) {
            sb.append(d.driverId())
                    .append(" - ").append(d.driverName())
                    .append(" Licenses: ").append(d.licenseTypes())
                    .append(" Available: ").append(d.isAvailable())
                    .append("\n----------------------\n");
        }
        return sb.toString();
    }

    public Optional<DriverDTO> getAvailableDriverByLicense(String licenseType) throws SQLException {
        List<DriverDTO> drivers = driverRepository.getAllDrivers();
        for (DriverDTO d : drivers) {
            if (d.isAvailable() && d.licenseTypes().contains(licenseType)) {
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }
}