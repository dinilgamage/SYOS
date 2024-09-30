package com.syos.report;

import com.syos.dao.InventoryDao;
import com.syos.dao.StockBatchDao;
import com.syos.enums.ReportFilterType;
import com.syos.model.Inventory;
import com.syos.model.StockBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StockReportTest {

  @Mock
  private StockBatchDao mockStockBatchDao;
  @Mock
  private InventoryDao mockInventoryDao;

  private StockReport stockReport;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    stockReport = new StockReport(mockStockBatchDao, mockInventoryDao);
  }

  // Helper method to create a StockBatch
  private StockBatch createStockBatch(int batchId, int itemId, int quantity, LocalDate dateReceived, LocalDate expiryDate) {
    return new StockBatch(batchId, itemId, quantity, dateReceived, expiryDate);
  }

  // Helper method to create an Inventory item
  private Inventory createInventory(int itemId, String itemCode, String name) {
    return new Inventory(itemCode, name, null, null, null, 0);
  }

  /**
   * Test generating stock report with valid stock batches and inventory items (Happy Path).
   */
  @Test
  public void testGenerateStockReport_Success() {
    // Arrange
    StockBatch batch1 = createStockBatch(1, 101, 50, LocalDate.now().minusDays(30), LocalDate.now().plusMonths(6));
    StockBatch batch2 = createStockBatch(2, 102, 100, LocalDate.now().minusDays(10), LocalDate.now().plusMonths(12));

    Inventory item1 = createInventory(101, "ITEM001", "Item One");
    Inventory item2 = createInventory(102, "ITEM002", "Item Two");

    when(mockStockBatchDao.getAllStockBatches()).thenReturn(Arrays.asList(batch1, batch2));
    when(mockInventoryDao.getItemById(101)).thenReturn(item1);
    when(mockInventoryDao.getItemById(102)).thenReturn(item2);

    // Act
    stockReport.generate(null, ReportFilterType.BOTH);  // Use generate()

    // Assert
    verify(mockStockBatchDao, times(1)).getAllStockBatches();
    verify(mockInventoryDao, times(1)).getItemById(101);
    verify(mockInventoryDao, times(1)).getItemById(102);
    assertEquals(2, stockReport.getStockBatchWithItemCodes().size());
  }

  /**
   * Test generating stock report when there are no stock batches (Edge Case).
   */
  @Test
  public void testGenerateStockReport_NoStockBatches() {
    // Arrange
    when(mockStockBatchDao.getAllStockBatches()).thenReturn(Collections.emptyList());

    // Act
    stockReport.generate(null, ReportFilterType.BOTH);  // Use generate()

    // Assert
    verify(mockStockBatchDao, times(1)).getAllStockBatches();
    verify(mockInventoryDao, never()).getItemById(anyInt());
    assertTrue(stockReport.getStockBatchWithItemCodes().isEmpty());  // Expect no stock batches in the report
  }

  /**
   * Test generating stock report when an inventory item is missing for a stock batch (Edge Case).
   */
  @Test
  public void testGenerateStockReport_ItemMissingForBatch() {
    // Arrange
    StockBatch batch1 = createStockBatch(1, 101, 50, LocalDate.now().minusDays(30), LocalDate.now().plusMonths(6));

    when(mockStockBatchDao.getAllStockBatches()).thenReturn(Collections.singletonList(batch1));
    when(mockInventoryDao.getItemById(101)).thenReturn(null);  // No corresponding inventory item

    // Act
    stockReport.generate(null, ReportFilterType.BOTH);  // Use generate()

    // Assert
    verify(mockStockBatchDao, times(1)).getAllStockBatches();
    verify(mockInventoryDao, times(1)).getItemById(101);
    assertEquals("Unknown Code", stockReport.getStockBatchWithItemCodes().get(batch1));
  }

  /**
   * Test generating stock report with multiple batches for a single inventory item (Edge Case).
   */
  @Test
  public void testGenerateStockReport_MultipleBatchesForSameItem() {
    // Arrange
    StockBatch batch1 = createStockBatch(1, 101, 50, LocalDate.now().minusDays(30), LocalDate.now().plusMonths(6));
    StockBatch batch2 = createStockBatch(2, 101, 100, LocalDate.now().minusDays(10), LocalDate.now().plusMonths(12));

    Inventory item1 = createInventory(101, "ITEM001", "Item One");

    when(mockStockBatchDao.getAllStockBatches()).thenReturn(Arrays.asList(batch1, batch2));
    when(mockInventoryDao.getItemById(101)).thenReturn(item1);

    // Act
    stockReport.generate(null, ReportFilterType.BOTH);  // Use generate()

    // Assert
    verify(mockStockBatchDao, times(1)).getAllStockBatches();
    verify(mockInventoryDao, times(2)).getItemById(101);  // Called twice, once for each batch
    assertEquals(2, stockReport.getStockBatchWithItemCodes().size());
  }

  /**
   * Test generating stock report when there are expired stock batches (Edge Case).
   */
  @Test
  public void testGenerateStockReport_ExpiredStockBatches() {
    // Arrange
    StockBatch batch1 = createStockBatch(1, 101, 50, LocalDate.now().minusDays(30), LocalDate.now().minusDays(1)); // Expired
    StockBatch batch2 = createStockBatch(2, 102, 100, LocalDate.now().minusDays(10), LocalDate.now().plusMonths(12)); // Valid

    Inventory item1 = createInventory(101, "ITEM001", "Item One");
    Inventory item2 = createInventory(102, "ITEM002", "Item Two");

    when(mockStockBatchDao.getAllStockBatches()).thenReturn(Arrays.asList(batch1, batch2));
    when(mockInventoryDao.getItemById(101)).thenReturn(item1);
    when(mockInventoryDao.getItemById(102)).thenReturn(item2);

    // Act
    stockReport.generate(null, ReportFilterType.BOTH);  // Use generate()

    // Assert
    verify(mockStockBatchDao, times(1)).getAllStockBatches();
    verify(mockInventoryDao, times(1)).getItemById(101);
    verify(mockInventoryDao, times(1)).getItemById(102);
    assertEquals(2, stockReport.getStockBatchWithItemCodes().size());  // Both batches should be in the report
  }
}
