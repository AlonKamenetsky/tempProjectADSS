package TransportationSuppliers.data.Util;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import Transportation.DataAccess.*;
import Transportation.DTO.*;

public class DatabaseInitializer {
    public  void loadFullDataTransportation() throws SQLException {
        SqliteSiteDAO siteDAO = new SqliteSiteDAO();
        SqliteZoneDAO zoneDAO = new SqliteZoneDAO();
        SqliteTransportationTaskDAO taskDAO = new SqliteTransportationTaskDAO();
        SqliteTruckDAO truckDAO = new SqliteTruckDAO();
        SqliteDriverDAO driverDAO = new SqliteDriverDAO();

        boolean zonesExist = !zoneDAO.findAll().isEmpty();
        boolean sitesExist = !siteDAO.findAll().isEmpty();
        boolean trucksExist = !truckDAO.findAll().isEmpty();
        boolean driversExist = !driverDAO.findAll().isEmpty();
        boolean tasksExist = !taskDAO.findAll().isEmpty();

        if (zonesExist || sitesExist || trucksExist || driversExist || tasksExist) {
            System.out.println("Fake data already exists. Skipping data load.");
            return;
        }


        // Adding Zones
        ZoneDTO zone1 = zoneDAO.insert(new ZoneDTO(null, "center", new ArrayList<>()));
        ZoneDTO zone2 = zoneDAO.insert(new ZoneDTO(null, "east", new ArrayList<>()));
        ZoneDTO zone3 = zoneDAO.insert(new ZoneDTO(null, "south", new ArrayList<>()));

        //  Adding Sites for Zone 1 (Center)
        SiteDTO site1 = siteDAO.insert(new SiteDTO(null, "bareket 20 shoham", "liel", "0501111111", zone1.zoneId()));
        SiteDTO site2 = siteDAO.insert(new SiteDTO(null, "tel aviv", "alice", "0501234567", zone1.zoneId()));

        //  Adding Sites for Zone 2 (East)
        SiteDTO site3 = siteDAO.insert(new SiteDTO(null, "yafo 123, jerusalem", "avi", "0509999999", zone2.zoneId()));
        SiteDTO site4 = siteDAO.insert(new SiteDTO(null, "david King Hotel, the dead sea", "nadav", "0508888888", zone2.zoneId()));

        //  Adding Sites for Zone 3 (North)
        SiteDTO site5 = siteDAO.insert(new SiteDTO(null, "ben gurion university", "shlomi", "0502222222", zone3.zoneId()));
        SiteDTO site6 = siteDAO.insert(new SiteDTO(null, "mini market eilat", "dana", "0507777777", zone3.zoneId()));

        //Adding Trucks
        TruckDTO truck1 = truckDAO.insert(new TruckDTO(null,"small","123","BMW",100F,120F,true));
        TruckDTO truck2 = truckDAO.insert(new TruckDTO(null,"large","555","BMW",133F,140F,true));

        //Adding Driver
        DriverDTO driver1 = driverDAO.insert(new DriverDTO("207271966", "Liel", List.of("B"), true));
        DriverDTO driver2 = driverDAO.insert(new DriverDTO("2005666356","Lidor", new ArrayList<>(Arrays.asList("B","C")), true));



        //Adding WarehouseId


        //Adding Task
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse("01/06/2025", dateFormatter);

        LocalTime time = LocalTime.parse("10:00");
        TransportationTaskDTO task1 = taskDAO.insert(
                new TransportationTaskDTO(
                        null,                    // taskId
                        date,                    // taskDate
                        time,                    // departureTime
                        site1.siteAddress(),     // sourceSiteAddress
                        List.of(site2.siteAddress()), // destinationsAddresses
                        driver1.driverId(),      // driverId
                        "1",                    // whwId
                        truck1.licenseNumber(),  // truckLicenseNumber
                        120f                     // weightBeforeLeaving
                )
        );

    }
}