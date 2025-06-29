package TransportationSuppliers.data.Util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public final class Database {
    private static final String DB_URL = "jdbc:sqlite:SuperLee.db";
    private final static Connection conn;

    static {
        try {

            Path dbFile = Paths.get("SuperLee.db");
            if (Files.exists(dbFile)) {
                Files.delete(dbFile);
            }

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);

            try (Statement st = conn.createStatement()) {
                // Transportation Tables
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS trucks(
                        truck_id        INTEGER PRIMARY KEY AUTOINCREMENT,
                        truck_type      TEXT      NOT NULL,
                        license_number  TEXT      NOT NULL,
                        model           TEXT      NOT NULL,
                        net_weight      REAL      NOT NULL,
                        max_weight      REAL      NOT NULL,
                        is_free         BOOLEAN   NOT NULL
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS drivers (
                        driver_id    TEXT PRIMARY KEY,
                        driver_name  TEXT NOT NULL,
                        is_available BOOLEAN NOT NULL
                    );
                """);
                st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS driver_licenses (
                        driver_id     TEXT NOT NULL,
                        license_type  TEXT NOT NULL,
                        PRIMARY KEY (driver_id, license_type),
                        FOREIGN KEY (driver_id) REFERENCES drivers(driver_id)
                );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS zones (
                        zone_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                        zone_name  TEXT NOT NULL
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS sites (
                        site_id       INTEGER PRIMARY KEY AUTOINCREMENT,
                        address       TEXT    NOT NULL,
                        contact_name  TEXT    NOT NULL,
                        phone_number  TEXT    NOT NULL,
                        zone_id       INTEGER NOT NULL,
                        FOREIGN KEY (zone_id) REFERENCES zones(zone_id)
                    );
                """);
//                st.executeUpdate("""
//                    CREATE TABLE IF NOT EXISTS items (
//                        item_id   INTEGER PRIMARY KEY AUTOINCREMENT,
//                        item_name TEXT    NOT NULL,
//                        weight    REAL    NOT NULL
//                    );
//                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS items_lists (
                        list_id INTEGER PRIMARY KEY AUTOINCREMENT
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS items_in_list (
                        list_id   INTEGER NOT NULL,
                        item_id   INTEGER NOT NULL,
                        quantity  INTEGER NOT NULL,
                        PRIMARY KEY (list_id, item_id),
                        FOREIGN KEY (list_id) REFERENCES items_lists(list_id),
                        FOREIGN KEY (item_id) REFERENCES products(id)
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transportation_tasks (
                        task_id               INTEGER PRIMARY KEY AUTOINCREMENT,
                        truck_id              INTEGER,
                        truck_license_number TEXT    NOT NULL,
                        task_date             TEXT    NOT NULL,
                        departure_time        TEXT    NOT NULL,
                        source_site_address        TEXT NOT NULL,
                        weight_before_leaving REAL    NOT NULL,
                        driver_id             TEXT, NOT NULL,
                        FOREIGN KEY (source_site_address) REFERENCES sites(address),
                        FOREIGN KEY (truck_id) REFERENCES trucks(truck_id)
                        FOREIGN KEY (driver_id) REFERENCES drivers(driver_id)
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transportation_docs (
                        doc_id           INTEGER PRIMARY KEY AUTOINCREMENT,
                        task_id          INTEGER NOT NULL,
                        destination_site INTEGER NOT NULL,
                        item_list_id     INTEGER NOT NULL,
                        FOREIGN KEY (task_id) REFERENCES transportation_tasks(task_id),
                        FOREIGN KEY (destination_site) REFERENCES sites(site_id),
                        FOREIGN KEY (item_list_id) REFERENCES items_lists(list_id)
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transportation_task_destinations (
                        task_id      INTEGER NOT NULL,
                        site_id      INTEGER NOT NULL,
                        PRIMARY KEY (task_id, site_id),
                        FOREIGN KEY (task_id) REFERENCES transportation_tasks(task_id),
                        FOREIGN KEY (site_id) REFERENCES sites(site_id)
                    );
                """);
//                st.executeUpdate("""
//                    CREATE TABLE IF NOT EXISTS drivers_in_tasks (
//                        task_id       TEXT NOT NULL,
//                        driver_id      TEXT NOT NULL,
//                        PRIMARY KEY (shift_id, driver_id),
//                        FOREIGN KEY (shift_id) REFERENCES shifts(id),
//                        FOREIGN KEY (driver_id) REFERENCES employees(id)
//                    );
//                """);

                //Suppliers Tables:
                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS products (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    company_name TEXT,
                    product_category TEXT,
                    product_weight REAL NOT NULL
                );
            """);

                st.executeUpdate("""
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
                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS order_product_for_scheduled_order_data (
                    order_id INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    product_quantity INTEGER NOT NULL,
                    day TEXT NOT NULL
                );
            """);

                st.executeUpdate("""
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

                st.executeUpdate("""
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

                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS suppliers_days (
                    supplier_id INTEGER NOT NULL,
                    day TEXT NOT NULL,
                    PRIMARY KEY (supplier_id, day)
                );
            """);

                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS supply_contracts (
                    id INTEGER PRIMARY KEY,
                    supplier_id INTEGER NOT NULL,
                    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
                );
            """);

                st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS supply_contract_product_data (
                    contract_id INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    product_price REAL NOT NULL,
                    quantity_for_discount INTEGER NOT NULL,
                    discount_percentage REAL NOT NULL,
                    FOREIGN KEY (contract_id) REFERENCES supply_contracts(id)
                );
            """);


            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    private Database() {
    }

    public static Connection getConnection() {
        return conn;
    }
}
