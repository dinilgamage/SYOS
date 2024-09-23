package com.syos.factory;

import com.syos.strategy.DiscountStrategy;
import com.syos.strategy.FixedDiscountStrategy;
import com.syos.strategy.PercentageDiscountStrategy;

import java.math.BigDecimal;

public class DiscountStrategyFactory {

  public static DiscountStrategy getDiscountStrategy(String strategyType, BigDecimal value) {
    switch (strategyType.toLowerCase()) {
      case "percentage":
        return new PercentageDiscountStrategy(value);
      case "fixed":
        return new FixedDiscountStrategy(value);
      default:
        throw new IllegalArgumentException("Invalid discount strategy: " + strategyType);
    }
  }
}
