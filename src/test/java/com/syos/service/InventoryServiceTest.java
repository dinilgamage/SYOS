package com.syos.service;

import com.syos.dao.InventoryDao;
import com.syos.dao.StockBatchDao;
import com.syos.enums.ShelfType;
import com.syos.enums.TransactionType;
import com.syos.exception.InsufficientStockException;
import com.syos.model.Inventory;
import com.syos.model.StockBatch;
import com.syos.observer.StockObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InventoryServiceTest {

  private InventoryDao mockInventoryDao;
  private StockBatchDao mockStockBatchDao;
  private StockObserver mockObserver;

  private InventoryService inventoryService;

  @BeforeEach
  public void setUp() {
    mockInventoryDao = mock(InventoryDao.class);
    mockStockBatchDao = mock(StockBatchDao.class);
    mockObserver = mock(StockObserver.class);

    inventoryService = new InventoryService(mockInventoryDao, mockStockBatchDao);
    inventoryService.registerObserver(mockObserver);
  }

  @AfterEach
  public void tearDown() {
    inventoryService = null;
  }

  /**
   * Helper method to create a sample Inventory.
   */
  private Inventory createInventory(int itemId, String itemCode, String name, BigDecimal price, int storeStock, int onlineStock, int shelfCapacity) {
    Inventory inventory = new Inventory(itemCode, name, price, storeStock, onlineStock, shelfCapacity);
    inventory.setItemId(itemId);
    return inventory;
  }

  /**
   * Helper method to create a sample StockBatch.
   */
  private StockBatch createStockBatch(int batchId, int itemId, int quantity) {
    return new StockBatch(batchId, itemId, quantity, null, null);
  }

  /**
   * Test retrieving all inventory items (Happy Path).
   */
  @Test
  public void testGetAllItems_Success() {
    // Arrange
    Inventory item1 = createInventory(1, "ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    Inventory item2 = createInventory(2, "ITEM002", "Item Two", new BigDecimal("200.00"), 40, 20, 80);
    when(mockInventoryDao.getAllItems()).thenReturn(Arrays.asList(item1, item2));

    // Act
    List<Inventory> items = inventoryService.getAllItems();

    // Assert
    assertNotNull(items);
    assertEquals(2, items.size());
    assertEquals("ITEM001", items.get(0).getItemCode());
    verify(mockInventoryDao, times(1)).getAllItems();
  }

  /**
   * Test retrieving an inventory item by code (Happy Path).
   */
  @Test
  public void testGetItemByCode_Success() {
    // Arrange
    Inventory inventory = createInventory(1, "ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    when(mockInventoryDao.getItemByCode("ITEM001")).thenReturn(inventory);

    // Act
    Inventory result = inventoryService.getItemByCode("ITEM001");

    // Assert
    assertNotNull(result);
    assertEquals("ITEM001", result.getItemCode());
    assertEquals("Item One", result.getName());
    verify(mockInventoryDao, times(1)).getItemByCode("ITEM001");
  }

  /**
   * Test retrieving an inventory item by code (Edge Case: Item not found).
   */
  @Test
  public void testGetItemByCode_ItemNotFound() {
    // Arrange
    when(mockInventoryDao.getItemByCode("ITEM001")).thenReturn(null);

    // Act
    Inventory result = inventoryService.getItemByCode("ITEM001");

    // Assert
    assertNull(result);
    verify(mockInventoryDao, times(1)).getItemByCode("ITEM001");
  }

  /**
   * Test calculating total stock from batches (Happy Path).
   */
  @Test
  public void testCalculateTotalStockFromBatches_Success() {
    // Arrange
    StockBatch batch1 = createStockBatch(1, 101, 50);
    StockBatch batch2 = createStockBatch(2, 101, 30);
    when(mockStockBatchDao.getBatchesForItem(101)).thenReturn(Arrays.asList(batch1, batch2));

    // Act
    int totalStock = inventoryService.calculateTotalStockFromBatches(101);

    // Assert
    assertEquals(80, totalStock);
    verify(mockStockBatchDao, times(1)).getBatchesForItem(101);
  }

  /**
   * Test checking available stock for store transactions (Happy Path).
   */
  @Test
  public void testCheckAvailableStock_StoreSuccess() {
    // Arrange
    Inventory inventory = createInventory(1, "ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);

    // Act
    boolean result = inventoryService.checkAvailableStock(inventory, 20, TransactionType.STORE);

    // Assert
    assertTrue(result);
  }

  /**
   * Test checking available stock for online transactions (Edge Case: Insufficient stock).
   */
  @Test
  public void testCheckAvailableStock_OnlineFailure() {
    // Arrange
    Inventory inventory = createInventory(1, "ITEM001", "Item One", new BigDecimal("100.00"), 50, 10, 100);

    // Act
    boolean result = inventoryService.checkAvailableStock(inventory, 20, TransactionType.ONLINE);

    // Assert
    assertFalse(result);
  }

  /**
   * Test checking available stock for store transactions (Edge Case: Insufficient stock).
   */
  @Test
  public void testCheckAvailableStock_StoreFailure() {
    // Arrange
    Inventory inventory = createInventory(1, "ITEM001", "Item One", new BigDecimal("100.00"), 10, 50, 100);

    // Act
    boolean result = inventoryService.checkAvailableStock(inventory, 20, TransactionType.STORE);

    // Assert
    assertFalse(result);
  }

  /**
   * Test updating inventory stock after purchase (Store, Happy Path).
   */
  @Test
  public void testUpdateInventoryStock_StoreSuccess() {
    // Arrange
    Inventory inventory = createInventory(1, "ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    when(mockInventoryDao.getItemByCode("ITEM001")).thenReturn(inventory);

    // Act
    inventoryService.updateInventoryStock("ITEM001", 10, TransactionType.STORE);

    // Assert
    assertEquals(40, inventory.getStoreStock());
    verify(mockInventoryDao, times(1)).updateInventory(inventory);
  }

  /**
   * Test updating inventory stock after purchase (Online, Happy Path).
   */
  @Test
  public void testUpdateInventoryStock_OnlineSuccess() {
    // Arrange
    Inventory inventory = createInventory(1, "ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    when(mockInventoryDao.getItemByCode("ITEM001")).thenReturn(inventory);

    // Act
    inventoryService.updateInventoryStock("ITEM001", 5, TransactionType.ONLINE);

    // Assert
    assertEquals(25, inventory.getOnlineStock());
    verify(mockInventoryDao, times(1)).updateInventory(inventory);
  }

  /**
   * Test updating inventory stock when item is not found (Edge Case).
   */
  @Test
  public void testUpdateInventoryStock_ItemNotFound() {
    // Arrange
    when(mockInventoryDao.getItemByCode("ITEM001")).thenReturn(null);

    // Act & Assert
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      inventoryService.updateInventoryStock("ITEM001", 10, TransactionType.STORE);
    });

    assertEquals("Item not found: ITEM001", thrown.getMessage());
    verify(mockInventoryDao, never()).updateInventory(any(Inventory.class));
  }

  /**
   * Test restocking item (Happy Path) with batch stock adjustment check.
   */
  @Test
  public void testRestockItem_Success_WithBatchStockAdjustment() {
    // Arrange
    Inventory inventory = createInventory(1, "ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);

    // Available stock in batches: 60 units. Restocking should use only 50.
    StockBatch batch1 = createStockBatch(1, 1, 60);  // Batch has 60 units initially

    when(mockInventoryDao.getItemByCode("ITEM001")).thenReturn(inventory);
    when(mockStockBatchDao.getBatchesForItem(1)).thenReturn(Collections.singletonList(batch1));

    // Act
    inventoryService.restockItem("ITEM001", ShelfType.STORE_SHELF);

    // Assert: Check store stock is updated correctly
    assertEquals(100, inventory.getStoreStock()); // Store stock should now be 100 (50 initially + 50 restocked)

    // Assert: Check if the batch stock was reduced by the restocked amount (50 units)
    assertEquals(10, batch1.getQuantity()); // 60 initial batch quantity - 50 restocked = 10 remaining in batch

    verify(mockInventoryDao, times(1)).updateInventory(inventory);
    verify(mockStockBatchDao, times(1)).updateBatch(batch1);
  }

  /**
   * Test restocking item when stock is insufficient (Edge Case).
   */
  @Test
  public void testRestockItem_InsufficientStock() {
    // Arrange
    Inventory inventory = createInventory(1, "ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    StockBatch batch1 = createStockBatch(1, 1, 5);
    when(mockInventoryDao.getItemByCode("ITEM001")).thenReturn(inventory);
    when(mockStockBatchDao.getBatchesForItem(1)).thenReturn(Collections.singletonList(batch1));

    // Act & Assert
    InsufficientStockException thrown = assertThrows(InsufficientStockException.class, () -> {
      inventoryService.restockItem("ITEM001", ShelfType.STORE_SHELF);
    });

    assertEquals("Insufficient stock across all batches for item: ITEM001", thrown.getMessage());
    verify(mockInventoryDao, never()).updateInventory(any(Inventory.class));
  }

  /**
   * Test notifying observers when stock falls below threshold (Happy Path).
   */
  @Test
  public void testNotifyObservers_Success() {
    // Arrange
    Inventory inventory = createInventory(1, "ITEM001", "Item One", new BigDecimal("100.00"), 5, 30, 100);
    when(mockInventoryDao.getItemByCode("ITEM001")).thenReturn(inventory);

    // Act
    inventoryService.notifyObservers(inventory);

    // Assert
    verify(mockObserver, times(1)).update(inventory);
  }

  /**
   * Test restocking an item and ensuring observers are notified when stock falls below the reorder threshold.
   */
  @Test
  public void testRestockItem_NotifyObserverOnReorderThreshold() {
    // Arrange
    Inventory inventory = createInventory(1, "ITEM001", "Item One", new BigDecimal("100.00"), 60, 30, 100); // Store
    // stock is 40 (below threshold)

    // Available stock in batches: 50 units. Restocking will leave total stock below the reorder threshold (50).
    StockBatch batch1 = createStockBatch(1, 1, 50);  // Batch has 50 units initially

    when(mockInventoryDao.getItemByCode("ITEM001")).thenReturn(inventory);
    when(mockStockBatchDao.getBatchesForItem(1)).thenReturn(Collections.singletonList(batch1));

    // Act
    inventoryService.restockItem("ITEM001", ShelfType.STORE_SHELF); // This should restock and bring total stock to 90 (still below 100 threshold)

    // Assert: Verify the observer is notified when total stock falls below the reorder threshold
    verify(mockObserver, times(1)).update(inventory);
  }

}
