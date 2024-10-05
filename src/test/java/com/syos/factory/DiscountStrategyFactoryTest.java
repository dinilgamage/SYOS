package com.syos.factory;

import com.syos.enums.DiscountType;
import com.syos.model.Inventory;
import com.syos.strategy.DiscountStrategy;
import com.syos.strategy.FixedDiscountStrategy;
import com.syos.strategy.NoDiscountStrategy;
import com.syos.strategy.PercentageDiscountStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountStrategyFactoryTest {

  private Inventory inventory;

  /**
   * Helper method to create an Inventory object.
   */
  private Inventory createInventory(DiscountType discountType, BigDecimal discountValue) {
    Inventory inventory = new Inventory("ITEM001", "Test Item", new BigDecimal("100.00"), 50, 50, 100);
    inventory.setDiscountType(discountType);
    inventory.setDiscountValue(discountValue);
    return inventory;
  }

  @BeforeEach
  public void setUp() {
    // Initial setup if needed
  }

  /**
   * Test when the discount type is "fixed" (Happy Path).
   */
  @Test
  public void testGetDiscountStrategy_FixedDiscount() {
    // Arrange
    inventory = createInventory(DiscountType.FIXED, new BigDecimal("10.00"));

    // Act
    DiscountStrategy strategy = DiscountStrategyFactory.getDiscountStrategy(inventory);

    // Assert
    assertTrue(strategy instanceof FixedDiscountStrategy);
    assertEquals(new BigDecimal("90.00").setScale(2, RoundingMode.HALF_UP), strategy.applyDiscount(new BigDecimal("100.00")).setScale(2, RoundingMode.HALF_UP));
  }

  /**
   * Test when the discount type is "percentage" (Happy Path).
   */
  @Test
  public void testGetDiscountStrategy_PercentageDiscount() {
    // Arrange
    inventory = createInventory(DiscountType.PERCENTAGE, new BigDecimal("10.00")); // 10% discount

    // Act
    DiscountStrategy strategy = DiscountStrategyFactory.getDiscountStrategy(inventory);

    // Assert
    assertTrue(strategy instanceof PercentageDiscountStrategy);
    assertEquals(new BigDecimal("90.00").setScale(2, RoundingMode.HALF_UP), strategy.applyDiscount(new BigDecimal("100.00")).setScale(2, RoundingMode.HALF_UP));
  }

  /**
   * Test when the discount type is "none" (Edge Case).
   */
  @Test
  public void testGetDiscountStrategy_NoDiscount() {
    // Arrange
    inventory = createInventory(DiscountType.NONE, BigDecimal.ZERO);

    // Act
    DiscountStrategy strategy = DiscountStrategyFactory.getDiscountStrategy(inventory);

    // Assert
    assertTrue(strategy instanceof NoDiscountStrategy);
    assertEquals(new BigDecimal("100.00").setScale(2, RoundingMode.HALF_UP), strategy.applyDiscount(new BigDecimal("100.00")).setScale(2, RoundingMode.HALF_UP));
  }

  /**
   * Test when the fixed discount exceeds the total amount (Edge Case).
   */
  @Test
  public void testGetDiscountStrategy_FixedDiscountExceedsTotal() {
    // Arrange
    inventory = createInventory(DiscountType.FIXED, new BigDecimal("150.00"));

    // Act
    DiscountStrategy strategy = DiscountStrategyFactory.getDiscountStrategy(inventory);

    // Assert
    assertTrue(strategy instanceof FixedDiscountStrategy);
    assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), strategy.applyDiscount(new BigDecimal("100.00")).setScale(2, RoundingMode.HALF_UP));
  }

  /**
   * Test when the percentage discount is 100% (Edge Case).
   */
  @Test
  public void testGetDiscountStrategy_PercentageDiscount_100Percent() {
    // Arrange
    inventory = createInventory(DiscountType.PERCENTAGE, new BigDecimal("100.00")); // 100% discount

    // Act
    DiscountStrategy strategy = DiscountStrategyFactory.getDiscountStrategy(inventory);

    // Assert
    assertTrue(strategy instanceof PercentageDiscountStrategy);
    assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), strategy.applyDiscount(new BigDecimal("100.00")).setScale(2, RoundingMode.HALF_UP));
  }
  
}
