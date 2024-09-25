package com.syos.service;

import com.syos.factory.DiscountStrategyFactory;
import com.syos.model.BillItem;
import com.syos.model.Inventory;
import com.syos.strategy.DiscountStrategy;

import java.math.BigDecimal;

public class DiscountService {

  public BigDecimal applyDiscount(Inventory inventoryItem, BillItem billItem) {
    // Use the factory to get the appropriate discount strategy
    DiscountStrategy discountStrategy = DiscountStrategyFactory.getDiscountStrategy(inventoryItem);

    // Apply the discount and return the discounted price
    return discountStrategy.applyDiscount(billItem.getItemPrice());
  }
}

