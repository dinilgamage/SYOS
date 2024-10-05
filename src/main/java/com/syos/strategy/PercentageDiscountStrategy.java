package com.syos.strategy;

import java.math.BigDecimal;

public class PercentageDiscountStrategy implements DiscountStrategy {

  private BigDecimal percentage;

  public PercentageDiscountStrategy(BigDecimal percentage) {
    if (percentage.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Discount percentage cannot be negative");
    }
    this.percentage = percentage;
  }

  @Override
  public BigDecimal applyDiscount(BigDecimal totalAmount) {

    BigDecimal discount = totalAmount.multiply(percentage).divide(BigDecimal.valueOf(100));
    return totalAmount.subtract(discount).max(BigDecimal.ZERO);
  }
}

