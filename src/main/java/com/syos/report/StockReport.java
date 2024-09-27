package com.syos.report;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.syos.dao.StockBatchDao;
import com.syos.dao.InventoryDao;
import com.syos.enums.TransactionType;
import com.syos.model.StockBatch;
import com.syos.model.Inventory;

public class StockReport extends Report {

  private StockBatchDao stockBatchDao;
  private InventoryDao inventoryDao;
  private Map<StockBatch, String> stockBatchWithItemCodes; // Map to store StockBatch with corresponding Item Code

  // Constructor to inject both StockBatchDao and InventoryDao
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
  protected void collectData(LocalDate date, TransactionType type) {
    System.out.println("Collecting stock data batch-wise...");

    // Fetch all stock batches from the StockBatchDao
    List<StockBatch> stockBatches = stockBatchDao.getAllStockBatches();

    // Collect corresponding item codes from InventoryDao
    for (StockBatch batch : stockBatches) {
      Inventory inventoryItem = inventoryDao.getItemById(batch.getItemId());
      String itemCode = (inventoryItem != null) ? inventoryItem.getItemCode() : "Unknown Code";
      stockBatchWithItemCodes.put(batch, itemCode); // Store the StockBatch with its Item Code
    }
  }

  @Override
  protected void formatReport() {
    System.out.println("Formatting Stock Report...");
  }

  @Override
  protected void displayReport(TransactionType transactionType) {
    // Display report using the data collected in collectData
    for (Map.Entry<StockBatch, String> entry : stockBatchWithItemCodes.entrySet()) {
      StockBatch batch = entry.getKey();
      String itemCode = entry.getValue();

      System.out.println("Item Code: " + itemCode +
        ", Batch ID: " + batch.getBatchId() +
        ", Quantity: " + batch.getQuantity() +
        ", Date Received: " + batch.getDateReceived() +
        ", Expiry Date: " + batch.getExpiryDate());
    }
  }
}
