package com.syos.service;

import com.syos.enums.DiscountType;
import com.syos.factory.DiscountStrategyFactory;
import com.syos.model.BillItem;
import com.syos.model.Inventory;
import com.syos.strategy.DiscountStrategy;
import com.syos.strategy.FixedDiscountStrategy;
import com.syos.strategy.NoDiscountStrategy;
import com.syos.strategy.PercentageDiscountStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DiscountServiceTest {

  private DiscountService discountService;

  @BeforeEach
  public void setUp() {
    discountService = new DiscountService();
  }

  @AfterEach
  public void tearDown() {
    // Clear Mockito's static mocks to prevent already registered issue with executing subsequent tests
    Mockito.clearAllCaches();
  }

  /**
   * Helper method to create a sample BillItem.
   */
  private BillItem createBillItem(String itemCode, int quantity, BigDecimal itemPrice) {
    return new BillItem(itemCode, quantity, itemPrice);
  }

  /**
   * Helper method to create a sample Inventory with a discount type.
   */
  private Inventory createInventory(DiscountType discountType, BigDecimal discountValue) {
    Inventory inventory = new Inventory("ITEM001", "Item One", new BigDecimal("100.00"), 50, 30, 100);
    inventory.setDiscountType(discountType);
    inventory.setDiscountValue(discountValue);
    return inventory;
  }

  /**
   * Test applying a fixed discount (Happy Path).
   */
  @Test
  public void testApplyFixedDiscount_Success() {
    // Arrange
    Inventory inventory = createInventory(DiscountType.FIXED, new BigDecimal("10.00"));
    BillItem billItem = createBillItem("ITEM001", 1, new BigDecimal("100.00"));

    // Mock the DiscountStrategyFactory to return a FixedDiscountStrategy
    DiscountStrategy discountStrategy = new FixedDiscountStrategy(new BigDecimal("10.00"));
    mockStatic(DiscountStrategyFactory.class);
    when(DiscountStrategyFactory.getDiscountStrategy(inventory)).thenReturn(discountStrategy);

    // Act
    BigDecimal result = discountService.applyDiscount(inventory, billItem);

    // Assert
    assertEquals(new BigDecimal("90.00").setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
  }

  /**
   * Test applying a percentage discount (Happy Path).
   */
  @Test
  public void testApplyPercentageDiscount_Success() {
    // Arrange
    Inventory inventory = createInventory(DiscountType.PERCENTAGE, new BigDecimal("10.00"));  // 10% discount
    BillItem billItem = createBillItem("ITEM001", 1, new BigDecimal("100.00"));

    // Mock the DiscountStrategyFactory to return a PercentageDiscountStrategy
    DiscountStrategy discountStrategy = new PercentageDiscountStrategy(new BigDecimal("10.00"));
    mockStatic(DiscountStrategyFactory.class);
    when(DiscountStrategyFactory.getDiscountStrategy(inventory)).thenReturn(discountStrategy);

    // Act
    BigDecimal result = discountService.applyDiscount(inventory, billItem);

    // Assert
    assertEquals(new BigDecimal("90.00").setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
  }

  /**
   * Test applying no discount (Edge Case).
   */
  @Test
  public void testApplyNoDiscount() {
    // Arrange
    Inventory inventory = createInventory(DiscountType.NONE, BigDecimal.ZERO);
    BillItem billItem = createBillItem("ITEM001", 1, new BigDecimal("100.00"));

    // Mock the DiscountStrategyFactory to return a NoDiscountStrategy
    DiscountStrategy discountStrategy = new NoDiscountStrategy();
    mockStatic(DiscountStrategyFactory.class);
    when(DiscountStrategyFactory.getDiscountStrategy(inventory)).thenReturn(discountStrategy);

    // Act
    BigDecimal result = discountService.applyDiscount(inventory, billItem);

    // Assert
    assertEquals(new BigDecimal("100.00").setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
  }

  /**
   * Test applying a fixed discount that results in zero price (Edge Case).
   */
  @Test
  public void testApplyFixedDiscount_ResultingInZero() {
    // Arrange
    Inventory inventory = createInventory(DiscountType.FIXED, new BigDecimal("200.00"));  // Discount exceeds item price
    BillItem billItem = createBillItem("ITEM001", 1, new BigDecimal("100.00"));

    // Mock the DiscountStrategyFactory to return a FixedDiscountStrategy
    DiscountStrategy discountStrategy = new FixedDiscountStrategy(new BigDecimal("200.00"));
    mockStatic(DiscountStrategyFactory.class);
    when(DiscountStrategyFactory.getDiscountStrategy(inventory)).thenReturn(discountStrategy);

    // Act
    BigDecimal result = discountService.applyDiscount(inventory, billItem);

    // Assert
    assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
  }

  /**
   * Test applying a percentage discount that results in zero price (Edge Case).
   */
  @Test
  public void testApplyPercentageDiscount_ResultingInZero() {
    // Arrange
    Inventory inventory = createInventory(DiscountType.PERCENTAGE, new BigDecimal("100.00"));  // 100% discount
    BillItem billItem = createBillItem("ITEM001", 1, new BigDecimal("100.00"));

    // Mock the DiscountStrategyFactory to return a PercentageDiscountStrategy
    DiscountStrategy discountStrategy = new PercentageDiscountStrategy(new BigDecimal("100.00"));
    mockStatic(DiscountStrategyFactory.class);
    when(DiscountStrategyFactory.getDiscountStrategy(inventory)).thenReturn(discountStrategy);

    // Act
    BigDecimal result = discountService.applyDiscount(inventory, billItem);

    // Assert
    assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
  }
}
