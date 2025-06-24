package Transportation.DataAccess;

import Transportation.DTO.DriverDTO;
import TransportationSuppliers.data.Util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteDriverDAO implements DriverDAO {
    @Override
    public DriverDTO insert(DriverDTO driver) throws SQLException {
        String insertDriverSQL = "INSERT INTO drivers (driver_id, driver_name, is_available) VALUES (?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(insertDriverSQL)) {
            ps.setString(1, driver.driverId());
            ps.setString(2, driver.driverName());
            ps.setBoolean(3, driver.isAvailable());
            ps.executeUpdate();
        }

        // Insert licenses
        String insertLicenseSQL = "INSERT INTO driver_licenses (driver_id, license_type) VALUES (?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(insertLicenseSQL)) {
            for (String license : driver.licenseTypes()) {
                ps.setString(1, driver.driverId());
                ps.setString(2, license);
                ps.addBatch();
            }
            ps.executeBatch();
        }

        return driver;
    }

    @Override
    public List<DriverDTO> findByLicenseType(String licenseType) throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();

        String sql = """
        SELECT d.driver_id, d.driver_name, d.is_available
        FROM drivers d
        JOIN driver_licenses l ON d.driver_id = l.driver_id
        WHERE l.license_type = ?
    """;

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, licenseType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    drivers.add(buildDriverFromResultSet(rs));
                }
            }
        }

        return drivers;
    }


    @Override
    public Optional<DriverDTO> findDriverById(String driverId) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE driver_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, driverId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(buildDriverFromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void setAvailability(String driverId, boolean status) throws SQLException {
        String sql = "UPDATE drivers SET is_available = ? WHERE driver_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setString(2, driverId);
            ps.executeUpdate();
        }
    }

    @Override
    public void addLicenseType(String driverId, String licenseType) throws SQLException {
        String sql = """
        INSERT OR IGNORE INTO driver_licenses (driver_id, license_type)
        VALUES (?, ?)
    """;

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, driverId);
            ps.setString(2, licenseType);
            ps.executeUpdate();
        }
    }


    @Override
    public List<DriverDTO> findAll() throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers";
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                drivers.add(buildDriverFromResultSet(rs));
            }
        }
        return drivers;
    }

    @Override
    public void delete(String driverId) throws SQLException {
        // Delete licenses first due to FK
        try (PreparedStatement ps = Database.getConnection().prepareStatement(
                "DELETE FROM driver_licenses WHERE driver_id = ?")) {
            ps.setString(1, driverId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = Database.getConnection().prepareStatement(
                "DELETE FROM drivers WHERE driver_id = ?")) {
            ps.setString(1, String.valueOf(driverId));
            ps.executeUpdate();
        }
    }

    private DriverDTO buildDriverFromResultSet(ResultSet rs) throws SQLException {
        String driverId = rs.getString("driver_id");
        String name = rs.getString("driver_name");
        boolean available = rs.getBoolean("is_available");

        // Load license types
        List<String> licenses = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(
                "SELECT license_type FROM driver_licenses WHERE driver_id = ?")) {
            ps.setString(1, driverId);
            try (ResultSet rsLic = ps.executeQuery()) {
                while (rsLic.next()) {
                    licenses.add(rsLic.getString("license_type"));
                }
            }
        }

        return new DriverDTO(driverId, name, licenses, available);
    }
}