package com.syos.service;

import com.syos.factory.DiscountStrategyFactory;
import com.syos.model.BillItem;
import com.syos.model.Inventory;
import com.syos.strategy.DiscountStrategy;

import java.math.BigDecimal;

public class DiscountService {

  public BigDecimal applyDiscount(Inventory inventoryItem, BillItem billItem) {

    DiscountStrategy discountStrategy = DiscountStrategyFactory.getDiscountStrategy(inventoryItem);

    return discountStrategy.applyDiscount(billItem.getItemPrice());
  }
}

