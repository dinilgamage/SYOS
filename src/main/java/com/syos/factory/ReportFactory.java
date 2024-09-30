package com.syos.factory;

import com.syos.enums.ReportType;
import com.syos.report.*;
import com.syos.dao.InventoryDao;
import com.syos.dao.TransactionDao;
import com.syos.dao.StockBatchDao;

public class ReportFactory {

  public static Report createReport(ReportType reportType, InventoryDao inventoryDao, TransactionDao transactionDao,
    StockBatchDao stockBatchDao) {
    switch (reportType) {
      case TOTAL_SALES:
        return new TotalSalesReport(transactionDao);
      case RESHELVE:
        return new ReshelveReport(inventoryDao);
      case REORDER:
        return new ReorderReport(inventoryDao, stockBatchDao);
      case BILL:
        return new BillReport(transactionDao);
      case STOCK:
        return new StockReport(stockBatchDao, inventoryDao);
      default:
        throw new IllegalArgumentException("Invalid report type: " + reportType);
    }
  }
}
