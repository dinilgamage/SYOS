package com.syos.strategy;

import java.math.BigDecimal;

public class FixedDiscountStrategy implements DiscountStrategy {

  private BigDecimal discountAmount; // The fixed amount to be deducted as a discount

  // Constructor to set the fixed discount amount
  public FixedDiscountStrategy(BigDecimal discountAmount) {
    if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Discount amount cannot be negative");
    }
    this.discountAmount = discountAmount;
  }

  @Override
  public BigDecimal applyDiscount(BigDecimal totalAmount) {
    // Deduct the fixed discount amount from the total, but ensure it doesn't result in a negative total
    return totalAmount.subtract(discountAmount).max(BigDecimal.ZERO);
  }
}
