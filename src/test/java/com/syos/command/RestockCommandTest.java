package com.syos.command;

import com.syos.enums.ShelfType;
import com.syos.exception.InsufficientStockException;
import com.syos.service.InventoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RestockCommandTest {

  private RestockCommand restockCommandStore;
  private RestockCommand restockCommandOnline;
  private InventoryService mockInventoryService;

  private static final String ITEM_CODE = "ITEM001";

  @BeforeEach
  public void setUp() {
    // Mock the InventoryService
    mockInventoryService = mock(InventoryService.class);

    // Create RestockCommand instances for store and online
    restockCommandStore = new RestockCommand(mockInventoryService, ITEM_CODE, ShelfType.STORE_SHELF);
    restockCommandOnline = new RestockCommand(mockInventoryService, ITEM_CODE, ShelfType.ONLINE_SHELF);
  }

  @AfterEach
  public void tearDown() {
    Mockito.clearAllCaches();
  }

  /**
   * Test restocking an item in the store (Happy Path).
   */
  @Test
  public void testRestockStoreShelf_Success() {
    // Act
    restockCommandStore.execute();

    // Assert that the restocking method for the store shelf is called once
    verify(mockInventoryService, times(1)).restockItem(ITEM_CODE, ShelfType.STORE_SHELF);
    System.out.println("Restocking successful for store shelf.");
  }

  /**
   * Test restocking an item online (Happy Path).
   */
  @Test
  public void testRestockOnlineShelf_Success() {
    // Act
    restockCommandOnline.execute();

    // Assert that the restocking method for the online shelf is called once
    verify(mockInventoryService, times(1)).restockItem(ITEM_CODE, ShelfType.ONLINE_SHELF);
  }

  /**
   * Test restocking when there is insufficient stock (Edge Case).
   */
  @Test
  public void testRestockStoreShelf_InsufficientStock() {
    // Arrange: Simulate InsufficientStockException being thrown by the InventoryService
    doThrow(new InsufficientStockException("Insufficient stock for item: " + ITEM_CODE))
      .when(mockInventoryService).restockItem(ITEM_CODE, ShelfType.STORE_SHELF);

    // Act
    restockCommandStore.execute();

    // Verify that the restocking method was called and handled the exception
    verify(mockInventoryService, times(1)).restockItem(ITEM_CODE, ShelfType.STORE_SHELF);
  }

  /**
   * Test restocking online when there is insufficient stock (Edge Case).
   */
  @Test
  public void testRestockOnlineShelf_InsufficientStock() {
    // Arrange: Simulate InsufficientStockException being thrown by the InventoryService
    doThrow(new InsufficientStockException("Insufficient stock for item: " + ITEM_CODE))
      .when(mockInventoryService).restockItem(ITEM_CODE, ShelfType.ONLINE_SHELF);

    // Act
    restockCommandOnline.execute();

    // Verify that the restocking method was called and handled the exception
    verify(mockInventoryService, times(1)).restockItem(ITEM_CODE, ShelfType.ONLINE_SHELF);
  }

  /**
   * Test restocking with invalid shelf type (Edge Case).
   */
  @Test
  public void testInvalidShelfType() {
    // Arrange: Simulate an invalid shelf type
    ShelfType invalidShelfType = null;
    try {
      invalidShelfType = ShelfType.fromString("INVALID");
      fail("Expected IllegalArgumentException for invalid shelf type.");
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid shelf type: INVALID", e.getMessage());
    }

    // Verify that restockItem is not called with invalid shelf type
    verify(mockInventoryService, never()).restockItem(ITEM_CODE, invalidShelfType);
  }
}
