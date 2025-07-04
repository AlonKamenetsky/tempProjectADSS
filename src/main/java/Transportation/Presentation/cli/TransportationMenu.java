package Transportation.Presentation.cli;

import Transportation.Service.*;
import java.util.Scanner;

public class TransportationMenu {
    private final TManagerRoleMenu TManagerMenuUI;
    private final Scanner input;

    public TransportationMenu( TruckService truckService,DriverService driverService, TaskService taskService,
                               ZoneService zoneService, SiteService siteService, SiteZoneService siteZoneService) {
        this.TManagerMenuUI = new TManagerRoleMenu(truckService,driverService, taskService, zoneService, siteService, siteZoneService);
        this.input = new Scanner(System.in);
    }

    public void show() {
        TManagerMenuUI.show();
    }
}
