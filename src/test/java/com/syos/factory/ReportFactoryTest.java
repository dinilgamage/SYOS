package com.syos.factory;

import com.syos.dao.InventoryDao;
import com.syos.dao.TransactionDao;
import com.syos.dao.StockBatchDao;
import com.syos.enums.ReportType;
import com.syos.report.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ReportFactoryTest {

  private InventoryDao mockInventoryDao;
  private TransactionDao mockTransactionDao;
  private StockBatchDao mockStockBatchDao;

  @BeforeEach
  public void setUp() {
    mockInventoryDao = mock(InventoryDao.class);
    mockTransactionDao = mock(TransactionDao.class);
    mockStockBatchDao = mock(StockBatchDao.class);
  }

  /**
   * Test creating a TotalSalesReport (Happy Path).
   */
  @Test
  public void testCreateTotalSalesReport_Success() {
    // Act
    Report report = ReportFactory.createReport(ReportType.TOTAL_SALES, mockInventoryDao, mockTransactionDao, mockStockBatchDao);

    // Assert
    assertNotNull(report);
    assertTrue(report instanceof TotalSalesReport);
  }

  /**
   * Test creating a ReshelveReport (Happy Path).
   */
  @Test
  public void testCreateReshelveReport_Success() {
    // Act
    Report report = ReportFactory.createReport(ReportType.RESHELVE, mockInventoryDao, mockTransactionDao, mockStockBatchDao);

    // Assert
    assertNotNull(report);
    assertTrue(report instanceof ReshelveReport);
  }

  /**
   * Test creating a ReorderReport (Happy Path).
   */
  @Test
  public void testCreateReorderReport_Success() {
    // Act
    Report report = ReportFactory.createReport(ReportType.REORDER, mockInventoryDao, mockTransactionDao, mockStockBatchDao);

    // Assert
    assertNotNull(report);
    assertTrue(report instanceof ReorderReport);
  }

  /**
   * Test creating a BillReport (Happy Path).
   */
  @Test
  public void testCreateBillReport_Success() {
    // Act
    Report report = ReportFactory.createReport(ReportType.BILL, mockInventoryDao, mockTransactionDao, mockStockBatchDao);

    // Assert
    assertNotNull(report);
    assertTrue(report instanceof BillReport);
  }

  /**
   * Test creating a StockReport (Happy Path).
   */
  @Test
  public void testCreateStockReport_Success() {
    // Act
    Report report = ReportFactory.createReport(ReportType.STOCK, mockInventoryDao, mockTransactionDao, mockStockBatchDao);

    // Assert
    assertNotNull(report);
    assertTrue(report instanceof StockReport);
  }

  /**
   * Test creating a report with an invalid report type (Edge Case).
   */
  @Test
  public void testCreateInvalidReportType_ThrowsException() {
    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      ReportFactory.createReport(null, mockInventoryDao, mockTransactionDao, mockStockBatchDao);
    });

    assertEquals("Report type cannot be null", exception.getMessage());
  }
}
