package Transportation.Domain;

import java.util.HashMap;

import SuppliersModule.DataLayer.DTO.ProductDTO;
import Transportation.DTO.ItemsListDTO;
import Transportation.Domain.Repositories.ItemListRepository;
import Transportation.Domain.Repositories.ItemListRepositoryImpli;

import java.sql.SQLException;

public class ItemListManager {
    private final ItemListRepository itemListRepository;

    public ItemListManager() {
        this.itemListRepository = new ItemListRepositoryImpli();
    }


//    public String getAllItemsString() throws SQLException {
//        List<ItemDTO> allItems = getAllItems();
//        if (allItems.isEmpty()) return "No items available.";
//
//        StringBuilder sb = new StringBuilder("All Items:\n");
//        for (ItemDTO i : allItems) {
//            sb.append(i.itemName()).append(" (Weight: ").append(i.itemWeight()).append(")\n----------------------\n");
//        }
//        return sb.toString();
//    }

    public int makeList(HashMap<ProductDTO, Integer> itemsInList) throws SQLException {
        return itemListRepository.makeList(itemsInList);
    }

    public float findWeightList(int itemsListId) throws SQLException {
        return itemListRepository.findWeightList(itemsListId);
    }

    public ItemsListDTO getItemsList(int itemsListId) throws SQLException {
        return itemListRepository.getItemsList(itemsListId);
    }
}