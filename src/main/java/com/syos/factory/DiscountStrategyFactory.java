package com.syos.factory;

import java.math.BigDecimal;

import com.syos.enums.DiscountType;
import com.syos.model.Inventory;
import com.syos.strategy.DiscountStrategy;
import com.syos.strategy.FixedDiscountStrategy;
import com.syos.strategy.NoDiscountStrategy;
import com.syos.strategy.PercentageDiscountStrategy;

public class DiscountStrategyFactory {

  public static DiscountStrategy getDiscountStrategy(Inventory inventory) {
    DiscountType discountType = inventory.getDiscountType();
    BigDecimal discountValue = inventory.getDiscountValue();

    if (DiscountType.FIXED.equals(discountType)) {
      return new FixedDiscountStrategy(discountValue);
    } else if (DiscountType.PERCENTAGE.equals(discountType)) {
      return new PercentageDiscountStrategy(discountValue);
    } else {
      return new NoDiscountStrategy();
    }
  }
}
