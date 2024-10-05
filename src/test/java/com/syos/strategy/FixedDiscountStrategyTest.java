package com.syos.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FixedDiscountStrategyTest {

  @Test
  public void testApplyDiscount_ExactDiscountAmount() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    BigDecimal discountAmount = new BigDecimal("100.00");
    FixedDiscountStrategy discountStrategy = new FixedDiscountStrategy(discountAmount);

    // Act
    BigDecimal result = discountStrategy.applyDiscount(totalAmount);

    // Assert
    assertEquals(0, result.compareTo(BigDecimal.ZERO)); // Use compareTo to ignore scale differences
  }

  @Test
  public void testApplyDiscount_LowerDiscountAmount() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    BigDecimal discountAmount = new BigDecimal("50.00");
    FixedDiscountStrategy discountStrategy = new FixedDiscountStrategy(discountAmount);

    // Act
    BigDecimal result = discountStrategy.applyDiscount(totalAmount);

    // Assert
    assertEquals(new BigDecimal("50.00"), result);
  }

  @Test
  public void testApplyDiscount_HigherDiscountAmount() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    BigDecimal discountAmount = new BigDecimal("150.00");
    FixedDiscountStrategy discountStrategy = new FixedDiscountStrategy(discountAmount);

    // Act
    BigDecimal result = discountStrategy.applyDiscount(totalAmount);

    // Assert
    assertEquals(BigDecimal.ZERO, result);
  }

  @Test
  public void testApplyDiscount_ZeroDiscountAmount() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("100.00");
    BigDecimal discountAmount = BigDecimal.ZERO;
    FixedDiscountStrategy discountStrategy = new FixedDiscountStrategy(discountAmount);

    // Act
    BigDecimal result = discountStrategy.applyDiscount(totalAmount);

    // Assert
    assertEquals(new BigDecimal("100.00"), result);
  }

  @Test
  public void testNegativeDiscount_ThrowsException() {
    // Arrange
    BigDecimal negativeDiscountAmount = new BigDecimal("-10.00");

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      new FixedDiscountStrategy(negativeDiscountAmount);
    });
  }


  @Test
  public void testApplyDiscount_SmallTotalAmount() {
    // Arrange
    BigDecimal totalAmount = new BigDecimal("0.10");
    BigDecimal discountAmount = new BigDecimal("0.05");
    FixedDiscountStrategy discountStrategy = new FixedDiscountStrategy(discountAmount);

    // Act
    BigDecimal result = discountStrategy.applyDiscount(totalAmount);

    // Assert
    assertEquals(new BigDecimal("0.05"), result);
  }
}
