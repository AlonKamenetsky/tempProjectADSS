package TransportationSuppliers;

import SuppliersModule.PresentationLayer.SupplierCLI;
import Transportation.Presentation.TransportationMain;
import TransportationSuppliers.data.Util.Database;
import TransportationSuppliers.data.Util.DatabaseInitializer;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Scanner;

public class CliMain {
    public static void main(String[] args) throws ParseException {
        Scanner sc = new Scanner(System.in);
        DatabaseInitializer dbInitializer = new DatabaseInitializer();
        while (true) {
            System.out.println("Hello, would you like to load the system with data?\n1.yes\n2.no - boot up with last run's information\n3.reset the Database - completely new system");
            String input;
            boolean t = true;
            while (t) {
                input = sc.nextLine();
                switch (input) {
                    case "1":

                        try {
                            Database.dropTables();
                            Database.initializeSchema();
                            dbInitializer.loadFullSuppliersData();
                            dbInitializer.loadFullDataTransportation();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("You have successfully loaded a full data system!");
                        t = false;
                        break;
                    case "2":
                        t = false;
                        break;
                    case "3":
                        try {
                            Database.dropTables();
                            Database.initializeSchema();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        t = false;
                        break;
                    default:
                        System.out.println("Invalid input. Please enter 1, 2 or 3.");
                        break;
                }
            }
            SupplierCLI suppliersCli = new SupplierCLI();
            while (true) {
                System.out.println("Select your role:");
                System.out.println("1. Transportation Manager");
                System.out.println("2. Supplier Manager");
                System.out.println("3. Shut down");

                input = sc.nextLine().trim();

                switch (input) {
                    case "1":
                        System.out.println("Redirecting to Transportation Manager...");
                        TransportationMain.main(args);
                        break;
                    case "2":
                        System.out.println("Redirecting to Supplier Manager...");
                        suppliersCli.mainCliMenu();
                        break;
                    case "3":
                        System.out.println("Bye bye!");
                        return;
                    default:
                        System.out.println("Invalid input. Please enter 1 or 2.\n");
                }
            }
        }
    }
}