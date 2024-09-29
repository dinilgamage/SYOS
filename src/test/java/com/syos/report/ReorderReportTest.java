package com.syos.report;

import com.syos.dao.InventoryDao;
import com.syos.dao.StockBatchDao;
import com.syos.enums.ReportFilterType;
import com.syos.model.Inventory;
import com.syos.model.StockBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReorderReportTest {

  private InventoryDao mockInventoryDao;
  private StockBatchDao mockStockBatchDao;
  private ReorderReport reorderReport;

  @BeforeEach
  public void setUp() {
    mockInventoryDao = mock(InventoryDao.class);
    mockStockBatchDao = mock(StockBatchDao.class);
    reorderReport = new ReorderReport(mockInventoryDao, mockStockBatchDao);
  }

  /**
   * Helper method to create a sample Inventory.
   */
  private Inventory createInventory(String itemCode, String name, int storeStock, int onlineStock, int shelfCapacity) {
    return new Inventory(itemCode, name, BigDecimal.valueOf(100.00), storeStock, onlineStock, shelfCapacity);
  }

  /**
   * Helper method to create a sample StockBatch.
   */
  private StockBatch createStockBatch(int batchId, int itemId, int quantity) {
    return new StockBatch(batchId, itemId, quantity, null, null);
  }

  /**
   * Test generating a reorder report with items below the reorder threshold (Happy Path).
   */
  @Test
  public void testGenerateReorderReport_ItemsBelowThreshold() {
    // Arrange
    Inventory item1 = createInventory("ITEM001", "Item One", 50, 20, 100);
    item1.setItemId(1); // Set unique itemId for item1

    Inventory item2 = createInventory("ITEM002", "Item Two", 30, 15, 100);
    item2.setItemId(2); // Set unique itemId for item2

    StockBatch batch1 = createStockBatch(1, item1.getItemId(), 20);
    StockBatch batch2 = createStockBatch(2, item2.getItemId(), 10);

    when(mockInventoryDao.getAllItems()).thenReturn(Arrays.asList(item1, item2));
    when(mockStockBatchDao.getBatchesForItem(item1.getItemId())).thenReturn(Collections.singletonList(batch1));
    when(mockStockBatchDao.getBatchesForItem(item2.getItemId())).thenReturn(Collections.singletonList(batch2));

    // Act
    reorderReport.collectData(null, ReportFilterType.BOTH);
    reorderReport.displayReport(ReportFilterType.BOTH);

    // Assert
    assertFalse(reorderReport.getItemsToReorder().isEmpty()); // Items should need reordering
    verify(mockInventoryDao, times(1)).getAllItems();
    verify(mockStockBatchDao, times(1)).getBatchesForItem(item1.getItemId());
    verify(mockStockBatchDao, times(1)).getBatchesForItem(item2.getItemId());
  }

  /**
   * Test generating a reorder report when all items are above the reorder threshold (Edge Case).
   */
  @Test
  public void testGenerateReorderReport_AllItemsAboveThreshold() {
    // Arrange
    Inventory item1 = createInventory("ITEM001", "Item One", 100, 100, 100); // Enough stock
    StockBatch batch1 = createStockBatch(1, item1.getItemId(), 80); // Stock above the threshold

    when(mockInventoryDao.getAllItems()).thenReturn(Collections.singletonList(item1));
    when(mockStockBatchDao.getBatchesForItem(item1.getItemId())).thenReturn(Collections.singletonList(batch1));

    // Act
    reorderReport.collectData(null, ReportFilterType.BOTH);
    reorderReport.displayReport(ReportFilterType.BOTH);

    // Assert
    assertTrue(reorderReport.getItemsToReorder().isEmpty()); // No items should need reordering
    verify(mockInventoryDao, times(1)).getAllItems();
    verify(mockStockBatchDao, times(1)).getBatchesForItem(item1.getItemId());
  }

  /**
   * Test generating a reorder report with no stock batches (Edge Case).
   */
  @Test
  public void testGenerateReorderReport_NoStockBatches() {
    // Arrange
    Inventory item1 = createInventory("ITEM001", "Item One", 50, 20, 100);

    when(mockInventoryDao.getAllItems()).thenReturn(Collections.singletonList(item1));
    when(mockStockBatchDao.getBatchesForItem(item1.getItemId())).thenReturn(Collections.emptyList()); // No stock batches

    // Act
    reorderReport.collectData(null, ReportFilterType.BOTH);
    reorderReport.displayReport(ReportFilterType.BOTH);

    // Assert
    assertFalse(reorderReport.getItemsToReorder().isEmpty()); // Item should need reordering because it has no stock
    verify(mockInventoryDao, times(1)).getAllItems();
    verify(mockStockBatchDao, times(1)).getBatchesForItem(item1.getItemId());
  }


  /**
   * Test generating a reorder report with mixed stock levels (Happy Path).
   */
  @Test
  public void testGenerateReorderReport_MixedStockLevels() {
    // Arrange
    Inventory item1 = createInventory("ITEM001", "Item One", 50, 20, 100);
    item1.setItemId(1); // Set unique itemId for item1

    Inventory item2 = createInventory("ITEM002", "Item Two", 100, 100, 100); // No reorder needed
    item2.setItemId(2); // Set unique itemId for item1

    StockBatch batch1 = createStockBatch(1, item1.getItemId(), 20);
    StockBatch batch2 = createStockBatch(2, item2.getItemId(), 100);

    when(mockInventoryDao.getAllItems()).thenReturn(Arrays.asList(item1, item2));
    when(mockStockBatchDao.getBatchesForItem(item1.getItemId())).thenReturn(Collections.singletonList(batch1));
    when(mockStockBatchDao.getBatchesForItem(item2.getItemId())).thenReturn(Collections.singletonList(batch2));

    // Act
    reorderReport.collectData(null, ReportFilterType.BOTH);
    reorderReport.displayReport(ReportFilterType.BOTH);

    // Assert
    assertEquals(1, reorderReport.getItemsToReorder().size()); // Only item1 should need reordering
    verify(mockInventoryDao, times(1)).getAllItems();
    verify(mockStockBatchDao, times(1)).getBatchesForItem(item1.getItemId());
    verify(mockStockBatchDao, times(1)).getBatchesForItem(item2.getItemId());
  }

  /**
   * Test generating a reorder report for an empty inventory (Edge Case).
   */
  @Test
  public void testGenerateReorderReport_EmptyInventory() {
    // Arrange
    when(mockInventoryDao.getAllItems()).thenReturn(Collections.emptyList());

    // Act
    reorderReport.collectData(null, ReportFilterType.BOTH);
    reorderReport.displayReport(ReportFilterType.BOTH);

    // Assert
    assertTrue(reorderReport.getItemsToReorder().isEmpty()); // No items should be in the report
    verify(mockInventoryDao, times(1)).getAllItems();
  }
}
