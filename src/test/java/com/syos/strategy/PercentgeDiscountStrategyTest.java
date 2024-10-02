package com.syos.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class PercentageDiscountStrategyTest {

  @Test
  public void testApplyDiscount_ValidPercentage() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    BigDecimal percentage = new BigDecimal("10"); // 10% discount
    PercentageDiscountStrategy discountStrategy = new PercentageDiscountStrategy(percentage);

    // Act
    BigDecimal result = discountStrategy.applyDiscount(totalAmount);

    // Assert
    assertEquals(new BigDecimal("90.00"), result);
  }

  @Test
  public void testApplyDiscount_ZeroPercentage() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    BigDecimal percentage = BigDecimal.ZERO; // 0% discount
    PercentageDiscountStrategy discountStrategy = new PercentageDiscountStrategy(percentage);

    // Act
    BigDecimal result = discountStrategy.applyDiscount(totalAmount);

    // Assert
    assertEquals(new BigDecimal("100.00"), result);
  }

  @Test
  public void testApplyDiscount_FullPercentage() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    BigDecimal percentage = new BigDecimal("100"); // 100% discount
    PercentageDiscountStrategy discountStrategy = new PercentageDiscountStrategy(percentage);

    // Act
    BigDecimal result = discountStrategy.applyDiscount(totalAmount);

    // Assert
    assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testApplyDiscount_NegativePercentage_ThrowsException() {
    // Arrange
    BigDecimal percentage = new BigDecimal("-10");

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      new PercentageDiscountStrategy(percentage);
    });
  }

  @Test
  public void testApplyDiscount_PercentageGreaterThan100() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    BigDecimal percentage = new BigDecimal("150"); // 150% discount
    PercentageDiscountStrategy discountStrategy = new PercentageDiscountStrategy(percentage);

    // Act
    BigDecimal result = discountStrategy.applyDiscount(totalAmount);

    // Assert
    assertEquals(BigDecimal.ZERO, result); // Ensure total amount does not go negative
  }

  @Test
  public void testApplyDiscount_SmallAmountWithLargePercentage() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("1.00");
    BigDecimal percentage = new BigDecimal("90"); // 90% discount
    PercentageDiscountStrategy discountStrategy = new PercentageDiscountStrategy(percentage);

    // Act
    BigDecimal result = discountStrategy.applyDiscount(totalAmount);

    // Assert
    assertEquals(new BigDecimal("0.10"), result); // Expect remaining 10%
  }

  @Test
  public void testApplyDiscount_DecimalPercentage() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    BigDecimal percentage = new BigDecimal("12.5"); // 12.5% discount
    PercentageDiscountStrategy discountStrategy = new PercentageDiscountStrategy(percentage);

    // Act
    BigDecimal result = discountStrategy.applyDiscount(totalAmount);

    // Assert
    assertEquals(new BigDecimal("87.50").setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
  }
}
