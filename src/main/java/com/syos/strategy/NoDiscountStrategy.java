package com.syos.strategy;

import java.math.BigDecimal;

public class NoDiscountStrategy implements DiscountStrategy {
  @Override
  public BigDecimal applyDiscount(BigDecimal totalAmount) {
    return totalAmount;
  }
}
