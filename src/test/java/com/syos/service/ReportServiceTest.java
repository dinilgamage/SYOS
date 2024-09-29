package com.syos.service;

import com.syos.dao.InventoryDao;
import com.syos.dao.StockBatchDao;
import com.syos.dao.TransactionDao;
import com.syos.enums.ReportFilterType;
import com.syos.enums.ReportType;
import com.syos.exception.InvalidReportTypeException;
import com.syos.factory.ReportFactory;
import com.syos.report.Report;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ReportServiceTest {

  private InventoryDao mockInventoryDao;
  private TransactionDao mockTransactionDao;
  private StockBatchDao mockStockBatchDao;
  private ReportService reportService;

  @BeforeEach
  public void setUp() {
    mockInventoryDao = mock(InventoryDao.class);
    mockTransactionDao = mock(TransactionDao.class);
    mockStockBatchDao = mock(StockBatchDao.class);

    reportService = new ReportService(mockInventoryDao, mockTransactionDao, mockStockBatchDao);
  }

  @AfterEach
  public void tearDown() {
    Mockito.clearAllCaches();
  }

  /**
   * Test generating a TOTAL_SALES report with a date and filter (Happy Path).
   */
  @Test
  public void testGenerateTotalSalesReport_WithDateAndFilter() {
    // Arrange
    Report mockReport = mock(Report.class);
    mockStatic(ReportFactory.class);
    when(ReportFactory.createReport(ReportType.TOTAL_SALES, mockInventoryDao, mockTransactionDao, mockStockBatchDao))
      .thenReturn(mockReport);

    LocalDate date = LocalDate.now();
    ReportFilterType filterType = ReportFilterType.STORE;

    // Act
    reportService.generateReport(ReportType.TOTAL_SALES, date, filterType);

    // Assert
    verify(mockReport, times(1)).generate(date, filterType);
  }

  /**
   * Test generating a RESHELVE report without a date or filter (Happy Path).
   */
  @Test
  public void testGenerateReshelveReport_NoDateNoFilter() {
    // Arrange
    Report mockReport = mock(Report.class);
    mockStatic(ReportFactory.class);
    when(ReportFactory.createReport(ReportType.RESHELVE, mockInventoryDao, mockTransactionDao, mockStockBatchDao))
      .thenReturn(mockReport);

    // Act
    reportService.generateReport(ReportType.RESHELVE);

    // Assert
    verify(mockReport, times(1)).generate(null, null);
  }

  /**
   * Test generating a REORDER report with only a filter (Happy Path).
   */
  @Test
  public void testGenerateReorderReport_WithFilter() {
    // Arrange
    Report mockReport = mock(Report.class);
    mockStatic(ReportFactory.class);
    when(ReportFactory.createReport(ReportType.REORDER, mockInventoryDao, mockTransactionDao, mockStockBatchDao))
      .thenReturn(mockReport);

    ReportFilterType filterType = ReportFilterType.ONLINE;

    // Act
    reportService.generateReport(ReportType.REORDER, filterType);

    // Assert
    verify(mockReport, times(1)).generate(null, filterType);
  }

  /**
   * Test handling of invalid ReportType (Edge Case).
   */
  @Test
  public void testGenerateReport_InvalidReportType() {
    // Act & Assert
    assertThrows(InvalidReportTypeException.class, () -> {
      reportService.generateReport(null);  // Pass an invalid or null ReportType
    });
  }

  /**
   * Test generating a STOCK report (Happy Path).
   */
  @Test
  public void testGenerateStockReport_Success() {
    // Arrange
    Report mockReport = mock(Report.class);
    mockStatic(ReportFactory.class);
    when(ReportFactory.createReport(ReportType.STOCK, mockInventoryDao, mockTransactionDao, mockStockBatchDao))
      .thenReturn(mockReport);

    // Act
    reportService.generateReport(ReportType.STOCK);

    // Assert
    verify(mockReport, times(1)).generate(null, null);
  }

  /**
   * Test generating a BILL report (Happy Path).
   */
  @Test
  public void testGenerateBillReport_Success() {
    // Arrange
    Report mockReport = mock(Report.class);
    mockStatic(ReportFactory.class);
    when(ReportFactory.createReport(ReportType.BILL, mockInventoryDao, mockTransactionDao, mockStockBatchDao))
      .thenReturn(mockReport);

    // Act
    reportService.generateReport(ReportType.BILL);

    // Assert
    verify(mockReport, times(1)).generate(null, null);
  }
}
