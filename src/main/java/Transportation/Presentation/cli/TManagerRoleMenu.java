package Transportation.Presentation.cli;

import Transportation.Service.*;
import TransportationSuppliers.CliMain;

import java.util.Scanner;

public class TManagerRoleMenu {
    private final TaskMenu TaskMenuUI;
    private final TruckMenu TruckMenuUI;
    private final SiteMenu SiteMenuUI;
    private final ZoneMenu ZoneMenuUI;
    private final DriverMenu DriverMenuUI;

    public TManagerRoleMenu( TruckService truckService,DriverService driverService, TaskService taskService, ZoneService zoneService, SiteService siteService, SiteZoneService siteZoneService) {
        TaskMenuUI = new TaskMenu(taskService, this);
        TruckMenuUI = new TruckMenu(this);
        SiteMenuUI = new SiteMenu(this);
        ZoneMenuUI = new ZoneMenu(this);
        DriverMenuUI = new DriverMenu(this);
    }

    public void show() {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("""
                    What would you like to use?
                   
                    1. Truck Menu
                    2. Driver Menu
                    3. Task Menu
                    4. Site Menu
                    5. Zone Menu
        
                    0. Logout""");
            String choiceManager = input.nextLine();
            switch (choiceManager) {
                case "1":
                    TruckMenuUI.show();
                    break;
                case "2":
                    DriverMenuUI.show();
                case "3":
                    TaskMenuUI.show();
                    break;
                case "4":
                    SiteMenuUI.show();
                    break;
                case "5":
                    ZoneMenuUI.show();
                    break;
                case "0":
                    System.out.println("""
                            Logging out of Transportation Menu
                            Returning to main menu..
                            """);
                    try {
                        CliMain.main(new String[0]);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                default:
                    System.out.println("Invalid input.");
            }
        }
    }
}