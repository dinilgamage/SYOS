package com.syos.strategy;

import java.math.BigDecimal;

public interface DiscountStrategy {

  /**
   * Applies a discount to the total amount.
   *
   * @param totalAmount - The original total amount before applying the discount.
   * @return - The total amount after the discount is applied.
   */
  BigDecimal applyDiscount(BigDecimal totalAmount);
}

