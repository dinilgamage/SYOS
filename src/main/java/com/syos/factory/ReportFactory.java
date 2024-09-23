package com.syos.factory;

import com.syos.enums.ReportType;
import com.syos.report.*;
import com.syos.dao.InventoryDao;
import com.syos.dao.TransactionDao;
import com.syos.dao.StockBatchDao;

public class ReportFactory {

  /**
   * Factory method to create a Report object.
   * @param reportType - The type of report (e.g., 'totalsales', 'reshelve', 'reorder', 'bill', 'stock').
   * @param inventoryDao - DAO for inventory-related data.
   * @param transactionDao - DAO for transaction-related data.
   * @param stockBatchDao - DAO for stock batch-related data.
   * @return - The specific Report object for the requested report type.
   */
  public static Report createReport(ReportType reportType, InventoryDao inventoryDao, TransactionDao transactionDao,
    StockBatchDao stockBatchDao) {
    switch (reportType) {
      case TOTAL_SALES:
        return new TotalSalesReport(transactionDao);
      case RESHELVE:
        return new ReshelveReport(inventoryDao);
      case REORDER:
        return new ReorderReport(inventoryDao);
      case BILL:
        return new BillReport(transactionDao);
      case STOCK:
        return new StockReport(stockBatchDao);
      default:
        throw new IllegalArgumentException("Invalid report type: " + reportType);
    }
  }
}
