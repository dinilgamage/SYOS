package com.syos.report;

import com.syos.dao.InventoryDao;
import com.syos.enums.ReportFilterType;
import com.syos.model.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReshelveReportTest {

  private InventoryDao mockInventoryDao;
  private ReshelveReport reshelveReport;

  @BeforeEach
  public void setUp() {
    mockInventoryDao = mock(InventoryDao.class);
    reshelveReport = new ReshelveReport(mockInventoryDao);
  }

  /**
   * Helper method to create a sample Inventory.
   */
  private Inventory createInventory(String itemCode, String name, int storeStock, int onlineStock, int shelfCapacity) {
    return new Inventory(itemCode, name, new BigDecimal("100.00"), storeStock, onlineStock, shelfCapacity);
  }

  /**
   * Test generating a reshelve report for both in-store and online items (Happy Path).
   */
  @Test
  public void testGenerateReport_Both_Success() {
    // Arrange
    Inventory item1 = createInventory("ITEM001", "Item One", 50, 30, 100); // Needs reshelving in-store and online
    Inventory item2 = createInventory("ITEM002", "Item Two", 80, 90, 100); // No reshelving needed

    when(mockInventoryDao.getItemsToReshelveForBoth()).thenReturn(Arrays.asList(item1, item2));

    // Act
    reshelveReport.collectData(null, ReportFilterType.BOTH);
    reshelveReport.displayReport(ReportFilterType.BOTH);

    // Assert
    verify(mockInventoryDao, times(1)).getItemsToReshelveForBoth();
  }

  /**
   * Test generating a reshelve report for only in-store items (Happy Path).
   */
  @Test
  public void testGenerateReport_Store_Success() {
    // Arrange
    Inventory item1 = createInventory("ITEM001", "Item One", 50, 30, 100); // Needs reshelving in-store
    when(mockInventoryDao.getItemsToReshelveForInStore()).thenReturn(Collections.singletonList(item1));

    // Act
    reshelveReport.collectData(null, ReportFilterType.STORE);
    reshelveReport.displayReport(ReportFilterType.STORE);

    // Assert
    verify(mockInventoryDao, times(1)).getItemsToReshelveForInStore();
  }

  /**
   * Test generating a reshelve report for only online items (Happy Path).
   */
  @Test
  public void testGenerateReport_Online_Success() {
    // Arrange
    Inventory item1 = createInventory("ITEM001", "Item One", 100, 30, 100); // Needs reshelving online
    when(mockInventoryDao.getItemsToReshelveForOnline()).thenReturn(Collections.singletonList(item1));

    // Act
    reshelveReport.collectData(null, ReportFilterType.ONLINE);
    reshelveReport.displayReport(ReportFilterType.ONLINE);

    // Assert
    verify(mockInventoryDao, times(1)).getItemsToReshelveForOnline();
  }

  /**
   * Test generating a reshelve report when no items need reshelving (Edge Case).
   */
  @Test
  public void testGenerateReport_NoItemsToReshelve() {
    // Arrange
    when(mockInventoryDao.getItemsToReshelveForBoth()).thenReturn(Collections.emptyList());

    // Act
    reshelveReport.collectData(null, ReportFilterType.BOTH);
    reshelveReport.displayReport(ReportFilterType.BOTH);

    // Assert
    assertTrue(reshelveReport.getItemsToReshelve().isEmpty());
    verify(mockInventoryDao, times(1)).getItemsToReshelveForBoth();
  }

  /**
   * Test generating a reshelve report when all items have sufficient stock (Edge Case).
   */
  @Test
  public void testGenerateReport_AllItemsSufficientStock() {
    // Arrange
    // Sufficient stock, so getItemsToReshelveForBoth() should return an empty list
    when(mockInventoryDao.getItemsToReshelveForBoth()).thenReturn(Collections.emptyList());

    // Act
    reshelveReport.collectData(null, ReportFilterType.BOTH);
    reshelveReport.displayReport(ReportFilterType.BOTH);

    // Assert
    assertTrue(reshelveReport.getItemsToReshelve().isEmpty());  // No items should need reshelving
    verify(mockInventoryDao, times(1)).getItemsToReshelveForBoth();
  }

  /**
   * Test generating a reshelve report with mixed stock levels (some items need reshelving, others don’t).
   */
  @Test
  public void testGenerateReport_MixedStockLevels() {
    // Arrange
    Inventory item1 = createInventory("ITEM001", "Item One", 50, 100, 100); // Needs reshelving in-store
    Inventory item2 = createInventory("ITEM002", "Item Two", 100, 90, 100); // Needs reshelving online
    when(mockInventoryDao.getItemsToReshelveForBoth()).thenReturn(Arrays.asList(item1, item2));

    // Act
    reshelveReport.collectData(null, ReportFilterType.BOTH);
    reshelveReport.displayReport(ReportFilterType.BOTH);

    // Assert
    assertEquals(2, reshelveReport.getItemsToReshelve().size());
    verify(mockInventoryDao, times(1)).getItemsToReshelveForBoth();
  }
}
