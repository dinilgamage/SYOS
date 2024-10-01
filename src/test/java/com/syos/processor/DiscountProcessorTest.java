package com.syos.processor;

import com.syos.enums.DiscountType;
import com.syos.facade.StoreFacade;
import com.syos.model.Inventory;
import com.syos.util.InputUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class DiscountProcessorTest {

  private StoreFacade mockStoreFacade;
  private DiscountProcessor discountProcessor;
  private Scanner mockScanner;

  @BeforeEach
  public void setUp() {
    mockStoreFacade = mock(StoreFacade.class);
    mockScanner = mock(Scanner.class);
    discountProcessor = new DiscountProcessor(mockStoreFacade);
  }

  /**
   * Add fixed discount to item (Happy path)
   */
  @Test
  public void testAddDiscounts_FixedDiscount_Success() {
    // Arrange
    Inventory inventoryItem = new Inventory("ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    when(mockStoreFacade.getItemByCode("ITEM001")).thenReturn(inventoryItem);
    when(mockScanner.next()).thenReturn("ITEM001", "fixed", "20.00");
    when(mockScanner.nextBigDecimal()).thenReturn(new BigDecimal("20.00"));

    // Act
    discountProcessor.addDiscounts(mockScanner);

    // Assert
    verify(mockStoreFacade).addDiscount("ITEM001", new BigDecimal("20.00"), DiscountType.FIXED);
  }

  /**
   * Add % discount to item (Happy path)
   */
  @Test
  public void testAddDiscounts_PercentageDiscount_Success() {
    // Arrange
    Inventory inventoryItem = new Inventory("ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    when(mockStoreFacade.getItemByCode("ITEM001")).thenReturn(inventoryItem);
    when(mockScanner.next()).thenReturn("ITEM001", "percentage", "10.00");
    when(mockScanner.nextBigDecimal()).thenReturn(new BigDecimal("10.00"));

    // Act
    discountProcessor.addDiscounts(mockScanner);

    // Assert
    verify(mockStoreFacade).addDiscount("ITEM001", new BigDecimal("10.00"), DiscountType.PERCENTAGE);
  }

  /**
   * Add no discount to item (Happy path)
   */
  @Test
  public void testAddDiscounts_NoDiscount_Success() {
    // Arrange
    Inventory inventoryItem = new Inventory("ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    when(mockStoreFacade.getItemByCode("ITEM001")).thenReturn(inventoryItem);
    when(mockScanner.next()).thenReturn("ITEM001", "none");

    // Act
    discountProcessor.addDiscounts(mockScanner);

    // Assert
    verify(mockStoreFacade).addDiscount("ITEM001", BigDecimal.ZERO, DiscountType.NONE);
  }

}
