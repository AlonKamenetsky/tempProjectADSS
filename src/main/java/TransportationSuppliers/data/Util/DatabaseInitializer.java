package TransportationSuppliers.data.Util;

import java.sql.SQLException;
import java.util.*;

import Transportation.DataAccess.*;
import Transportation.DTO.*;

public class DatabaseInitializer {
    public  void loadTransportationFakeData() throws SQLException {
        SqliteSiteDAO siteDAO = new SqliteSiteDAO();
        SqliteZoneDAO zoneDAO = new SqliteZoneDAO();
        SqliteTransportationTaskDAO taskDAO = new SqliteTransportationTaskDAO();
        SqliteTruckDAO truckDAO = new SqliteTruckDAO();

        // Adding Zones
        ZoneDTO zone1 = zoneDAO.insert(new ZoneDTO(null, "center", new ArrayList<>()));
        ZoneDTO zone2 = zoneDAO.insert(new ZoneDTO(null, "east", new ArrayList<>()));
        ZoneDTO zone3 = zoneDAO.insert(new ZoneDTO(null, "north", new ArrayList<>()));

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

    }

    public void loadItems() throws SQLException {
        SqliteItemDAO itemDAO = new SqliteItemDAO();
        //Adding Items
        ItemDTO item1 = itemDAO.insert(new ItemDTO(null,"bamba",0.5F));
        ItemDTO item2 = itemDAO.insert(new ItemDTO(null,"chicken",2F));
        ItemDTO item3 = itemDAO.insert(new ItemDTO(null,"sugar",1F));
    }
}