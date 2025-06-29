package Transportation.Domain.Repositories;

import SuppliersModule.DataLayer.DTO.ProductDTO;

import Transportation.DTO.ItemsListDTO;

import java.sql.SQLException;
import java.util.HashMap;

public interface ItemListRepository {
    int makeList(HashMap<ProductDTO, Integer> itemsInList) throws SQLException;
    float findWeightList(int itemsListId) throws SQLException;
    ItemsListDTO getItemsList(int itemsListId) throws SQLException;
}