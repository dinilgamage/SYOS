package com.syos.report;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.syos.dao.StockBatchDao;
import com.syos.dao.InventoryDao;
import com.syos.enums.ReportFilterType;
import com.syos.model.StockBatch;
import com.syos.model.Inventory;

public class StockReport extends Report {

  private StockBatchDao stockBatchDao;
  private InventoryDao inventoryDao;
  private Map<StockBatch, String> stockBatchWithItemCodes;

  public StockReport(StockBatchDao stockBatchDao, InventoryDao inventoryDao) {
    this.stockBatchDao = stockBatchDao;
    this.inventoryDao = inventoryDao;
    this.stockBatchWithItemCodes = new HashMap<>(); // Initialize the map
  }

  @Override
  protected void prepareHeader() {
    System.out.println("=== Stock Report ===");
    System.out.println("Item Code | Batch ID | Quantity | Date Received | Expiry Date");
  }

  @Override
  protected void collectData(LocalDate date, ReportFilterType type) {
    System.out.println("Collecting stock data batch-wise...");

    // Fetch all stock batches from the StockBatchDao
    List<StockBatch> stockBatches = stockBatchDao.getAllStockBatches();

    // Collect corresponding item codes from InventoryDao
    for (StockBatch batch : stockBatches) {
      Inventory inventoryItem = inventoryDao.getItemById(batch.getItemId());
      String itemCode = (inventoryItem != null) ? inventoryItem.getItemCode() : "Unknown Code";
      stockBatchWithItemCodes.put(batch, itemCode);
    }
  }

  @Override
  protected void formatReport() {
    System.out.println("Formatting Stock Report...");
  }

  @Override
  protected void displayReport(ReportFilterType reportFilterType) {
    // Convert the map entries to a stream and sort them by the item code (or any other field)
    stockBatchWithItemCodes.entrySet().stream()
      .sorted(Comparator.comparing(entry -> entry.getKey().getBatchId()))
      .forEach(entry -> {
        StockBatch batch = entry.getKey();
        String itemCode = entry.getValue();

        System.out.println("Batch ID: " + batch.getBatchId() +
          ", Item Code: " + itemCode +
          ", Quantity: " + batch.getQuantity() +
          ", Date Received: " + batch.getDateReceived() +
          ", Expiry Date: " + batch.getExpiryDate());
      });
  }

  // Getter for itemsToReorder for testing purposes
  public Map<StockBatch, String> getStockBatchWithItemCodes() {
    return stockBatchWithItemCodes;
  }
}
