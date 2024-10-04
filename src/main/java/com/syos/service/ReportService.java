package com.syos.service;

import com.syos.enums.ReportType;
import com.syos.enums.ReportFilterType;
import com.syos.exception.InvalidReportTypeException;
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

  public ReportService(InventoryDao inventoryDao, TransactionDao transactionDao, StockBatchDao stockBatchDao) {
    this.inventoryDao = inventoryDao;
    this.transactionDao = transactionDao;
    this.stockBatchDao = stockBatchDao;
  }

// method (date and transactionType both required)
  public void generateReport(ReportType reportType, LocalDate date, ReportFilterType reportFilterType) {
    validateReportType(reportType);
    Report report = ReportFactory.createReport(reportType, inventoryDao, transactionDao, stockBatchDao);
    report.generate(date,
      reportFilterType);
  }

  // Overloaded method for reports that don't need date or transactionType
  public void generateReport(ReportType reportType) {
    validateReportType(reportType);
    Report report = ReportFactory.createReport(reportType, inventoryDao, transactionDao, stockBatchDao);
    report.generate(null, null);  // No date and transactionType, so passing null is acceptable
  }

  // Overloaded method for reports that only need transactionType
  public void generateReport(ReportType reportType, ReportFilterType reportFilterType) {
    validateReportType(reportType);
    Report report = ReportFactory.createReport(reportType, inventoryDao, transactionDao, stockBatchDao);
    report.generate(null,
      reportFilterType);  // No date, only transactionType, so passing null is acceptable
  }

  private void validateReportType(ReportType reportType) {
    if (reportType == null) {
      throw new InvalidReportTypeException("Report type cannot be null");
    }
  }

}
