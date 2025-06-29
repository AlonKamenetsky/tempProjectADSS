package SuppliersModule.util;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Database {
    private static final Logger log = Logger.getLogger(Database.class.getName());
    private static final String DB_URL = "jdbc:sqlite:data/suppliers.db";
    private static Connection conn;

    static {
        try {
            new File("data").mkdirs();
            conn = DriverManager.getConnection(DB_URL);
            log.info("Connected to SQLite at " + DB_URL);
            log.info("Connected to SQLite at " + new File("data/suppliers.db").getAbsolutePath());
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Failed to connect to the database", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void dropAllTables() {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = OFF");

            // Collect all table names first
            List<String> tableNames = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'")) {
                while (rs.next()) {
                    String tableName = rs.getString("name");
                    if (!tableName.startsWith("sqlite_")) {
                        tableNames.add(tableName);
                    }
                }
            }

            // Drop each table after result set is closed
            for (String tableName : tableNames) {
                System.out.println("Dropping table: " + tableName);
                stmt.executeUpdate("DROP TABLE IF EXISTS \"" + tableName + "\"");
            }

            stmt.execute("PRAGMA foreign_keys = ON");
            System.out.println("All tables dropped.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void initializeSchema() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS products (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    company_name TEXT,
                    product_category TEXT,
                    product_weight REAL NOT NULL
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS orders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    supplier_id INTEGER NOT NULL,
                    delivering_method TEXT NOT NULL,
                    order_date TEXT NOT NULL,
                    supply_date TEXT NOT NULL,
                    total_price REAL NOT NULL,
                    supply_method TEXT NOT NULL,
                    order_status TEXT NOT NULL,
                    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
                );
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS order_product_for_scheduled_order_data (
                    order_id INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    product_quantity INTEGER NOT NULL,
                    day TEXT NOT NULL
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS order_product_data (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_id INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    price REAL NOT NULL,
                    FOREIGN KEY (order_id) REFERENCES orders(id),
                    FOREIGN KEY (product_id) REFERENCES products(id)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS suppliers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    product_category TEXT NOT NULL,
                    delivering_method TEXT NOT NULL,
                    contact_name TEXT NOT NULL,
                    phone_number TEXT NOT NULL,
                    address TEXT NOT NULL,
                    email TEXT NOT NULL,
                    bank_account TEXT NOT NULL,
                    payment_method TEXT NOT NULL,
                    supply_method TEXT NOT NULL
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS suppliers_days (
                    supplier_id INTEGER NOT NULL,
                    day TEXT NOT NULL,
                    PRIMARY KEY (supplier_id, day)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS supply_contracts (
                    id INTEGER PRIMARY KEY,
                    supplier_id INTEGER NOT NULL,
                    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS supply_contract_product_data (
                    contract_id INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    product_price REAL NOT NULL,
                    quantity_for_discount INTEGER NOT NULL,
                    discount_percentage REAL NOT NULL,
                    FOREIGN KEY (contract_id) REFERENCES supply_contracts(id)
                );
            """);

            System.out.println("Schema initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return conn;
    }
}
