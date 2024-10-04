package com.syos.report;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.syos.dao.InventoryDao;
import com.syos.dao.StockBatchDao;
import com.syos.enums.ReportFilterType;
import com.syos.enums.StockThreshold;
import com.syos.model.Inventory;
import com.syos.model.StockBatch;

public class ReorderReport extends Report {

  private InventoryDao inventoryDao;
  private StockBatchDao stockBatchDao;
  private Map<Inventory, Integer> itemsToReorder;

  public ReorderReport(InventoryDao inventoryDao, StockBatchDao stockBatchDao) {
    this.inventoryDao = inventoryDao;
    this.stockBatchDao = stockBatchDao;
    this.itemsToReorder = new HashMap<>();
  }

  @Override
  protected void prepareHeader() {
    System.out.println("=== Reorder Report ===");
  }

  @Override
  protected void collectData(LocalDate date, ReportFilterType type) {
    System.out.println("Collecting inventory data for reorder report...");

    // Fetch all inventory items, regardless of their stock level
    List<Inventory> inventoryItems = inventoryDao.getAllItems(); // Fetch all items from inventory

    for (Inventory inventory : inventoryItems) {
      // Fetch all stock batches for the current item
      List<StockBatch> stockBatches = stockBatchDao.getBatchesForItem(inventory.getItemId());

      // Sum up total stock across all batches
      int totalBatchStock = stockBatches.stream()
        .mapToInt(StockBatch::getQuantity)
        .sum();

      // If the total stock is below the reorder threshold, add it to itemsToReorder
      if (totalBatchStock < StockThreshold.REORDER_LEVEL.getValue()) {
        itemsToReorder.put(inventory, totalBatchStock);
      }
    }
  }

  @Override
  protected void formatReport() {
    System.out.println("Formatting Reorder Report...");
  }

  @Override
  protected void displayReport(ReportFilterType reportFilterType) {
    System.out.println("Items that need to be reordered (total stock across batches below " + StockThreshold.REORDER_LEVEL.getValue() + "):");
    for (Map.Entry<Inventory, Integer> entry : itemsToReorder.entrySet()) {
      Inventory item = entry.getKey();
      int totalStock = entry.getValue();
      System.out.println("Item Code: " + item.getItemCode() +
        ", Name: " + item.getName() +
        ", Total Stock across batches: " + totalStock);
    }
  }

  // Getter for itemsToReorder for testing purposes
  public Map<Inventory, Integer> getItemsToReorder() {
    return itemsToReorder;
  }
}
