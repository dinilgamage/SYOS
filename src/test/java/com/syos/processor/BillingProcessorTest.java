package com.syos.processor;

import com.syos.enums.TransactionType;
import com.syos.facade.StoreFacade;
import com.syos.model.BillItem;
import com.syos.model.Inventory;
import com.syos.processor.BillingProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BillingProcessorTest {

  private BillingProcessor billingProcessor;
  private StoreFacade mockStoreFacade;
  private Scanner mockScanner;

  @BeforeEach
  public void setUp() {
    mockStoreFacade = mock(StoreFacade.class);
    mockScanner = mock(Scanner.class);
    billingProcessor = new BillingProcessor(mockStoreFacade);
  }

  /**
   * Add single item to bill success (Happy path)
   */
  @Test
  public void testProcessBilling_Success_SingleItem() {
    // Arrange
    Inventory inventoryItem = new Inventory("ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    when(mockStoreFacade.getItemByCode("ITEM001")).thenReturn(inventoryItem);
    when(mockStoreFacade.checkAvailableStock(inventoryItem, 1, TransactionType.STORE)).thenReturn(true);
    when(mockStoreFacade.applyDiscount(any(), any())).thenReturn(new BigDecimal("90.00"));

    // Mock user inputs
    when(mockScanner.next()).thenReturn("ITEM001", "no", "100.00");
    when(mockScanner.nextInt()).thenReturn(1);
    when(mockScanner.nextBigDecimal()).thenReturn(new BigDecimal("100.00"));

    // Act
    billingProcessor.processBilling(mockScanner, TransactionType.STORE, null);

    // Assert
    verify(mockStoreFacade).generateBill(anyList(), eq(TransactionType.STORE), eq(new BigDecimal("100.00")), eq(null));
  }

  /**
   * Add multiple items to bill success (Happy path)
   */
  @Test
  public void testProcessBilling_Success_MultipleItems() {
    // Arrange
    Inventory inventoryItem1 = new Inventory("ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    Inventory inventoryItem2 = new Inventory("ITEM002", "Item Two", new BigDecimal("150.00"), 50, 30, 100);
    when(mockStoreFacade.getItemByCode("ITEM001")).thenReturn(inventoryItem1);
    when(mockStoreFacade.getItemByCode("ITEM002")).thenReturn(inventoryItem2);
    when(mockStoreFacade.checkAvailableStock(inventoryItem1, 1, TransactionType.STORE)).thenReturn(true);
    when(mockStoreFacade.checkAvailableStock(inventoryItem2, 1, TransactionType.STORE)).thenReturn(true);
    when(mockStoreFacade.applyDiscount(any(), any())).thenReturn(new BigDecimal("90.00"), new BigDecimal("140.00"));

    // Mock user inputs
    when(mockScanner.next()).thenReturn("ITEM001", "yes", "ITEM002", "no", "300.00");
    when(mockScanner.nextInt()).thenReturn(1);
    when(mockScanner.nextBigDecimal()).thenReturn(new BigDecimal("300.00"));

    // Act
    billingProcessor.processBilling(mockScanner, TransactionType.STORE, null);

    // Assert
    verify(mockStoreFacade).generateBill(anyList(), eq(TransactionType.STORE), eq(new BigDecimal("300.00")), eq(null));
  }

  /**
   * User enters multiple items with percentage discounts and ensures the total is calculated properly
   */
  @Test
  public void testProcessBilling_Success_WithPercentageDiscounts_MultipleItems() {
    // Arrange
    Inventory inventoryItem1 = new Inventory("ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    Inventory inventoryItem2 = new Inventory("ITEM002", "Item Two", new BigDecimal("200.00"), 50, 30, 100);
    when(mockStoreFacade.getItemByCode("ITEM001")).thenReturn(inventoryItem1);
    when(mockStoreFacade.getItemByCode("ITEM002")).thenReturn(inventoryItem2);
    when(mockStoreFacade.checkAvailableStock(inventoryItem1, 1, TransactionType.STORE)).thenReturn(true);
    when(mockStoreFacade.checkAvailableStock(inventoryItem2, 1, TransactionType.STORE)).thenReturn(true);
    when(mockStoreFacade.applyDiscount(any(), any())).thenReturn(new BigDecimal("90.00"), new BigDecimal("180.00")); // 10% discount on each

    // Mock user inputs
    when(mockScanner.next()).thenReturn("ITEM001", "yes", "ITEM002", "no", "270.00");
    when(mockScanner.nextInt()).thenReturn(1);
    when(mockScanner.nextBigDecimal()).thenReturn(new BigDecimal("270.00"));

    // Act
    billingProcessor.processBilling(mockScanner, TransactionType.STORE, null);

    // Assert
    verify(mockStoreFacade).generateBill(anyList(), eq(TransactionType.STORE), eq(new BigDecimal("270.00")), eq(null));
  }

}
