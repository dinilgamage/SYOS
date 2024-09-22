package com.syos.service;

import com.syos.enums.TransactionType;
import com.syos.report.Report;
import com.syos.factory.ReportFactory;
import com.syos.dao.InventoryDao;
import com.syos.dao.TransactionDao;
import com.syos.dao.StockBatchDao;

import java.time.LocalDate;

public class ReportService {

  private InventoryDao inventoryDao;
  private TransactionDao transactionDao;
  private StockBatchDao stockBatchDao;

  // Constructor to inject dependencies
  public ReportService(InventoryDao inventoryDao, TransactionDao transactionDao, StockBatchDao stockBatchDao) {
    this.inventoryDao = inventoryDao;
    this.transactionDao = transactionDao;
    this.stockBatchDao = stockBatchDao;
  }

  /**
   * Generates a report based on the report type.
   * @param reportType - The type of report to generate.
   * @param date - Optional: The date for the report (used for date-sensitive reports like TotalSales).
   * @param transactionType - Optional: The type of transaction (used for reports filtered by transaction type).
   */
  public void generateReport(String reportType, LocalDate date, TransactionType transactionType) {
    // Create the appropriate report using the factory
    Report report = ReportFactory.createReport(reportType, inventoryDao, transactionDao, stockBatchDao);

    // Generate the report with optional date and transactionType if applicable
    report.generate(date, transactionType);
  }
}
