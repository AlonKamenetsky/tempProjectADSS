package TransportationSuppliers;

import SuppliersModule.PresentationLayer.SupplierMain;
import Transportation.Presentation.TransportationMain;
import TransportationSuppliers.data.Util.DatabaseInitializer;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Scanner;


public class CliMain {
    public static void main(String[] args) throws ParseException, SQLException {
        DatabaseInitializer db = new DatabaseInitializer();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Hello, would you like to load the system with data or not? (Yes/No)");
            String input = sc.nextLine();
            switch (input) {
                case "No":
                    //loadBasicDataSuppliers();
                    //loadBasicDataTransportation();
                    System.out.println("You have successfully loaded a basic data system!");
                    break;
                case "Yes":
                    //loadFullDataSuppliers();
                    db.loadFullDataTransportation();
                    System.out.println("You have successfully loaded a full data system!");
                    break;
                    default:
                        System.out.println("Invalid input!");
                        continue;
            }
            System.out.println("Select your role:");
            System.out.println("1 - Transportation Manager");
            System.out.println("2 - Supplier Manager");
            System.out.print("Enter 1 or 2: ");

            input = sc.nextLine().trim();
            switch (input) {
                case "1":
                    System.out.println("Redirecting to Transportation Manager...");
                    TransportationMain.main(args);
                    return;
                case "2":
                    System.out.println("Redirecting to Supplier Manager...");
                    SupplierMain.main(args);
                    return;
                default:
                    System.out.println("Invalid input. Please enter 1 or 2.\n");
            }
        }
    }
}