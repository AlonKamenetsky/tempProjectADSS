package Transportation.Domain.Repositories;

import Transportation.DTO.DriverDTO;
import Transportation.DataAccess.DriverDAO;
import Transportation.DataAccess.SqliteDriverDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DriverRepositoryImpli implements DriverRepository {
    private final DriverDAO driverDAO;

    public DriverRepositoryImpli() {
        this.driverDAO = new SqliteDriverDAO();
    }

    @Override
    public DriverDTO addDriver(String driverId, String driverName, String licenseType, boolean isFree) throws SQLException {
        List<String> licenses = new ArrayList<>();
        licenses.add(licenseType);
        DriverDTO driver = new DriverDTO(driverId, driverName, licenses, isFree);
        return driverDAO.insert(driver);
    }

    @Override
    public Optional<DriverDTO> findDriverById(String driverId) throws SQLException {
        return driverDAO.findDriverById(driverId);
    }

    @Override
    public List<DriverDTO> findByLicenseType(String licenseType) throws SQLException {
        return driverDAO.findByLicenseType(licenseType);
    }

    @Override
    public List<DriverDTO> getAllDrivers() throws SQLException {
        return driverDAO.findAll();
    }

    @Override
    public void addLicenseToDriver(String driverId, String licenseType) throws SQLException {
        driverDAO.addLicenseType(driverId, licenseType);
    }

    @Override
    public void deleteDriver(String driverId) throws SQLException {
        driverDAO.delete(driverId);
    }

    @Override
    public void updateAvailability(String driverId, boolean status) throws SQLException {
        driverDAO.setAvailability(driverId, status);
    }
}