package SuppliersModule.util;


import java.util.List;

public class Initializers {
    public static void loadSuppliers() {
        List<String[]> rows = CSVReader.loadCSV("data/suppliers_data.csv");
        for (String[] row : rows) {
            String supplyMethod = row[0];
            String supplierName = row[1];
            String productCategory = row[2];
            String DeliveryMethod = row[3];
            String phoneNumber = row[4];
            String address = row[5];
            String emailAddress = row[6];
            String bankAcount = row[7];
            String paymentMethod = row[8];

            // Add to your repository or controller
        }
    }

    public static void loadProducts() {
        List<String[]> rows = CSVReader.loadCSV("data/products_data.csv");
        for (String[] row : rows) {
            int productId = Integer.parseInt(row[0]);
            String name = row[1];

        }
    }

    public static void loadContracts() {
        List<String[]> rows = CSVReader.loadCSV("data/contracts_data.csv");
        for (String[] row : rows) {
            int contractId = Integer.parseInt(row[0]);
            int supplierId = Integer.parseInt(row[1]);

        }
    }
    public static void dropAllTables() {
        Database.dropAllTables();
        Database.initializeSchema();
    }
    public static void initializeTables() {
        Database.initializeSchema();
    }
}
