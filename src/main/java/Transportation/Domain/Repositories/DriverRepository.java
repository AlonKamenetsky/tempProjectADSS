package Transportation.Domain.Repositories;

import Transportation.DTO.DriverDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DriverRepository {
    DriverDTO addDriver(String driverId, String driverName, String licenseType, boolean isFree) throws SQLException;
    Optional<DriverDTO> findDriverById(String driverId) throws SQLException;
    List<DriverDTO> findByLicenseType(String licenseType) throws SQLException;
    List<DriverDTO> getAllDrivers() throws SQLException;
    void addLicenseToDriver(String driverId, String licenseType) throws SQLException;
    void deleteDriver(String driverId) throws SQLException;
    void updateAvailability(String driverId, boolean status) throws SQLException;
}