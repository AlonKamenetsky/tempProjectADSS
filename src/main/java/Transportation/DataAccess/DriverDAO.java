package Transportation.DataAccess;

import Transportation.DTO.DriverDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DriverDAO {
    DriverDTO insert(DriverDTO driver) throws SQLException;
    List<DriverDTO> findByLicenseType(String licenseType) throws SQLException;
    Optional<DriverDTO> findDriverById(String driverId) throws SQLException;
    void setAvailability(String driverId, boolean status) throws SQLException;
    void addLicenseType(String driverId, String licenseType) throws SQLException;
    List<DriverDTO> findAll() throws SQLException;
    void delete(String driverId) throws SQLException;
}