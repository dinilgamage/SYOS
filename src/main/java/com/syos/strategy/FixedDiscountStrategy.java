package com.syos.strategy;

import java.math.BigDecimal;

public class FixedDiscountStrategy implements DiscountStrategy {

  private BigDecimal discountAmount;

  public FixedDiscountStrategy(BigDecimal discountAmount) {
    if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Discount amount cannot be negative");
    }
    this.discountAmount = discountAmount;
  }

  @Override
  public BigDecimal applyDiscount(BigDecimal totalAmount) {

    return totalAmount.subtract(discountAmount).max(BigDecimal.ZERO);
  }
}
