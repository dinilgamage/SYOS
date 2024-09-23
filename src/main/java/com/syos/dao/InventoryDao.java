package com.syos.dao;

import com.syos.model.Inventory;
import java.util.List;

public interface InventoryDao {
  Inventory getItemByCode(String itemCode); // Retrieves an inventory item by its unique item code
  Inventory getItemById(int itemId); // Retrieves an inventory item by its unique item id
  void updateInventory(Inventory inventory); // Updates the stock levels for an inventory item
  List<Inventory> getLowStockItems(); // Retrieves all items below the specified stock threshold
  List<Inventory> getItemsToReshelveForInStore(); // A list of Inventory objects for in-store items to be reshelved
  List<Inventory> getItemsToReshelveForOnline(); //A list of Inventory objects for online items to be reshelved
  List<Inventory> getItemsToReshelveForBoth(); // A list of Inventory objects for both in-store and online items to be reshelved
  List<Inventory> getItemsBelowReorderLevel(int threshold); // Fetches items where the total stock (storeStock + onlineStock) falls below the reorder threshold.

}

