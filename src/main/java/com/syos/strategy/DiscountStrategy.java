package com.syos.strategy;

import java.math.BigDecimal;

public interface DiscountStrategy {
  BigDecimal applyDiscount(BigDecimal totalAmount);
}

