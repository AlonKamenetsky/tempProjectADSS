package SuppliersModule.PresentationLayer;

import SuppliersModule.util.Database;

public class Main {
    public static void main(String[] args) {
        Database.initializeSchema();
        SupplierCLI cli = new SupplierCLI();

        cli.mainCliMenu();
    }
}
