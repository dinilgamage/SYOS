package com.syos.report;

import java.time.LocalDate;
import java.util.List;

import com.syos.dao.StockBatchDao;
import com.syos.enums.TransactionType;
import com.syos.model.StockBatch;

public class StockReport extends Report {

  private StockBatchDao stockBatchDao;
  private List<StockBatch> stockBatches;

  // Constructor to inject dependencies (StockBatchDao)
  public StockReport(StockBatchDao stockBatchDao) {
    this.stockBatchDao = stockBatchDao;
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
    stockBatches = stockBatchDao.getAllStockBatches();
  }

  @Override
  protected void formatReport() {
    System.out.println("Formatting Stock Report...");
  }

  @Override
  protected void displayReport() {
    for (StockBatch batch : stockBatches) {
      System.out.println("Item ID: " + batch.getItemId() +
        ", Batch ID: " + batch.getBatchId() +
        ", Quantity: " + batch.getQuantity() +
        ", Date Received: " + batch.getDateReceived() +
        ", Expiry Date: " + batch.getExpiryDate());
    }
  }
}
