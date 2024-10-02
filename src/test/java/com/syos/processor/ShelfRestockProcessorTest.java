package com.syos.processor;

import com.syos.enums.ShelfType;
import com.syos.facade.StoreFacade;
import com.syos.model.Inventory;
import com.syos.processor.ShelfRestockProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Scanner;

import static org.mockito.Mockito.*;

public class ShelfRestockProcessorTest {

  private final StoreFacade mockStoreFacade = mock(StoreFacade.class);
  private final Scanner mockScanner = mock(Scanner.class);
  private final ShelfRestockProcessor shelfRestockProcessor = new ShelfRestockProcessor(mockStoreFacade);

  @Test
  public void testRestockShelf_ValidItemAndShelfType() {
    // Arrange
    Inventory inventoryItem = new Inventory("ITEM001", "Item One", BigDecimal.valueOf(100.00), 50, 30, 100);
    when(mockStoreFacade.getItemByCode("ITEM001")).thenReturn(inventoryItem);
    when(mockScanner.next()).thenReturn("ITEM001", "store"); // Valid item code and shelf type

    // Act
    shelfRestockProcessor.restockShelf(mockScanner);

    // Assert
    verify(mockStoreFacade).restockItem("ITEM001", ShelfType.STORE_SHELF);
  }

  @Test
  public void testRestockShelf_InvalidItemCode() {
    // Arrange
    when(mockScanner.next()).thenReturn("ITEM002", "ITEM001", "store"); // Invalid then valid item code
    when(mockStoreFacade.getItemByCode("ITEM002")).thenReturn(null); // Simulate item not found
    Inventory inventoryItem = new Inventory("ITEM001", "Item One", BigDecimal.valueOf(100.00), 50, 30, 100);
    when(mockStoreFacade.getItemByCode("ITEM001")).thenReturn(inventoryItem);

    // Act
    shelfRestockProcessor.restockShelf(mockScanner);

    // Assert
    verify(mockStoreFacade).restockItem("ITEM001", ShelfType.STORE_SHELF);
  }

  @Test
  public void testRestockShelf_InvalidShelfType() {
    // Arrange
    Inventory inventoryItem = new Inventory("ITEM001", "Item One", BigDecimal.valueOf(100.00), 50, 30, 100);
    when(mockStoreFacade.getItemByCode("ITEM001")).thenReturn(inventoryItem);
    when(mockScanner.next()).thenReturn("ITEM001", "invalidShelfType", "store"); // Invalid then valid shelf type

    // Act
    shelfRestockProcessor.restockShelf(mockScanner);

    // Assert
    verify(mockStoreFacade).restockItem("ITEM001", ShelfType.STORE_SHELF);
  }

}
