package com.syos.dao;

import com.syos.model.Inventory;
import java.util.List;

public interface InventoryDao {
  Inventory getItemByCode(String itemCode);
  Inventory getItemById(int itemId);
  List<Inventory> getAllItems();
  void updateInventory(Inventory inventory);
  List<Inventory> getLowStockItems();
  List<Inventory> getItemsToReshelveForInStore();
  List<Inventory> getItemsToReshelveForOnline();
  List<Inventory> getItemsToReshelveForBoth();
  List<Inventory> getItemsBelowReorderLevel(int threshold);

}

