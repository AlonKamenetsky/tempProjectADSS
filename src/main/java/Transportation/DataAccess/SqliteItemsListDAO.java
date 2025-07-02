package Transportation.DataAccess;

import Transportation.DTO.ItemsListDTO;
import TransportationSuppliers.data.Util.Database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SqliteItemsListDAO implements ItemsListDAO {

    @Override
    public int createEmptyList() throws SQLException {
        String sql = "INSERT INTO items_lists DEFAULT VALUES";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.executeUpdate();
        }

        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve last inserted ID");
            }
        }
    }


    @Override
    public void addItemToList(int listId, int itemId, int quantity) throws SQLException {
        String sql = "INSERT OR REPLACE INTO items_in_list(list_id, item_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, listId);
            ps.setInt(2, itemId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        }
    }

    @Override
    public void removeItemFromList(int listId, int itemId) throws SQLException {
        String sql = "DELETE FROM items_in_list WHERE list_id = ? AND item_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, listId);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        }
    }

    @Override
    public float findWeight(int listId) throws SQLException {
        String sql = """
        SELECT SUM(iil.quantity * p.product_weight) AS total_weight
        FROM items_in_list iil
        JOIN products p ON iil.item_id = p.id
        WHERE iil.list_id = ?
    """;

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, listId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("total_weight");
                } else {
                    return 0f; // No items in list
                }
            }
        }
    }


    @Override
    public HashMap<Integer, Integer> getItemsInList(int listId) throws SQLException {
        String sql = "SELECT item_id, quantity FROM items_in_list WHERE list_id = ?";
        HashMap<Integer, Integer> items = new HashMap<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, listId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.put(rs.getInt("item_id"), rs.getInt("quantity"));
                }
            }
        }
        return items;
    }

    @Override
    public void delete(int listId) throws SQLException {
        String sql = "DELETE FROM items_lists WHERE list_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, listId);
            ps.executeUpdate();
        }
    }

    @Override
    public ItemsListDTO findItemListByID(int listId) throws SQLException {
        String sql = "SELECT item_id, quantity FROM items_in_list WHERE list_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, listId);
            try (ResultSet rs = ps.executeQuery()) {
                Map<Integer, Integer> itemsMap = new HashMap<>();
                while (rs.next()) {
                    int itemId = rs.getInt("item_id");
                    int quantity = rs.getInt("quantity");
                    itemsMap.put(itemId, quantity);
                }
                return new ItemsListDTO(listId, itemsMap);
            }
        }
    }
}