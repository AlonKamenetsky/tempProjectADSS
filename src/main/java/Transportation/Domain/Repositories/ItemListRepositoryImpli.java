package Transportation.Domain.Repositories;

import SuppliersModule.DataLayer.DTO.ProductDTO;
import Transportation.DTO.ItemsListDTO;

import Transportation.DataAccess.ItemsListDAO;
import Transportation.DataAccess.SqliteItemsListDAO;

import java.sql.SQLException;
import java.util.*;

public class ItemListRepositoryImpli implements ItemListRepository {

    private final ItemsListDAO listDAO;

    public ItemListRepositoryImpli() {
        this.listDAO = new SqliteItemsListDAO();
    }

    @Override
    public int makeList(HashMap<ProductDTO, Integer> itemsInList) throws SQLException {
        int listId = listDAO.createEmptyList();
        for (HashMap.Entry<ProductDTO, Integer> entry : itemsInList.entrySet()) {
            ProductDTO currProduct = entry.getKey();
            int quantity = entry.getValue();
            int currItemId = currProduct.productId();
            listDAO.addItemToList(listId, currItemId, quantity);
        }
        return listId;
    }

    public float findWeightList(int itemsListId) throws SQLException {
        return listDAO.findWeight(itemsListId);
    }

    @Override
    public ItemsListDTO getItemsList(int itemsListId) throws SQLException {
        return listDAO.findItemListByID(itemsListId);
    }
}