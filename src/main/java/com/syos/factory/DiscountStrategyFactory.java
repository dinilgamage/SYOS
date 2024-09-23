package com.syos.factory;

import java.math.BigDecimal;
import com.syos.model.Inventory;
import com.syos.strategy.DiscountStrategy;
import com.syos.strategy.FixedDiscountStrategy;
import com.syos.strategy.PercentageDiscountStrategy;

public class DiscountStrategyFactory {

  // Factory method to create the appropriate DiscountStrategy
  public static DiscountStrategy getDiscountStrategy(Inventory inventory) {
    String discountType = inventory.getDiscountType();
    BigDecimal discountValue = inventory.getDiscountValue();

    if ("fixed".equalsIgnoreCase(discountType)) {
      return new FixedDiscountStrategy(discountValue);
    } else if ("percentage".equalsIgnoreCase(discountType)) {
      return new PercentageDiscountStrategy(discountValue);
    } else {
      // Return a strategy that applies no discount if none is found
      return totalAmount -> totalAmount; // No discount applied
    }
  }
}
