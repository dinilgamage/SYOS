package com.syos.report;

import java.time.LocalDate;
import java.util.List;

import com.syos.dao.InventoryDao;
import com.syos.enums.TransactionType;
import com.syos.model.Inventory;

public class ReorderReport extends Report {

  private InventoryDao inventoryDao;
  private List<Inventory> itemsToReorder;

  private static final int REORDER_THRESHOLD = 50;  // Reorder threshold of 50 items

  // Constructor to inject dependencies (InventoryDao)
  public ReorderReport(InventoryDao inventoryDao) {
    this.inventoryDao = inventoryDao;
  }

  @Override
  protected void prepareHeader() {
    System.out.println("=== Reorder Report ===");
  }

  @Override
  protected void collectData(LocalDate date, TransactionType type) {
    System.out.println("Collecting inventory data for reorder report...");

    // Fetch items where total stock (storeStock + onlineStock) is below the reorder threshold
    itemsToReorder = inventoryDao.getItemsBelowReorderLevel(REORDER_THRESHOLD);
  }

  @Override
  protected void formatReport() {
    System.out.println("Formatting Reorder Report...");
  }

  @Override
  protected void displayReport() {
    System.out.println("Items that need to be reordered (stock below " + REORDER_THRESHOLD + "):");
    for (Inventory item : itemsToReorder) {
      System.out.println("Item Code: " + item.getItemCode() +
        ", Name: " + item.getName() +
        ", Store Stock: " + item.getStoreStock() +
        ", Online Stock: " + item.getOnlineStock() +
        ", Total Stock: " + (item.getStoreStock() + item.getOnlineStock()));
    }
  }
}
