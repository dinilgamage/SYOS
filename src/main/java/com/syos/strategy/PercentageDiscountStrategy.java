package com.syos.strategy;

import java.math.BigDecimal;

public class PercentageDiscountStrategy implements DiscountStrategy {

  private BigDecimal percentage; // The percentage to be deducted as a discount

  // Constructor to set the discount percentage
  public PercentageDiscountStrategy(BigDecimal percentage) {
    this.percentage = percentage;
  }

  @Override
  public BigDecimal applyDiscount(BigDecimal totalAmount) {
  // Calculate the discount based on the percentage and deduct it from the total amount
    BigDecimal discount = totalAmount.multiply(percentage).divide(BigDecimal.valueOf(100));
    return totalAmount.subtract(discount).max(BigDecimal.ZERO);
  }
}

