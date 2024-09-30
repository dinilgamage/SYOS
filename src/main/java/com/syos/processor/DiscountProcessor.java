package com.syos.processor;

import com.syos.facade.StoreFacade;
import com.syos.model.Inventory;
import com.syos.util.InputUtils;

import java.math.BigDecimal;
import java.util.Scanner;

public class DiscountProcessor {

  private final StoreFacade storeFacade;

  public DiscountProcessor(StoreFacade storeFacade) {
    this.storeFacade = storeFacade;
  }

  public void addDiscounts(Scanner scanner) {
    System.out.println("=== Add Discounts ===");

    // Use the utility method to validate and get the item
    Inventory inventoryItem = InputUtils.getValidatedInventoryItem(storeFacade, scanner, "Enter Item Code: ");

    String strategyType = InputUtils.getValidatedStringOption(scanner,
      "Enter Discount Strategy (percentage/fixed/none): ",
      "percentage", "fixed", "none");

    BigDecimal discountValue;

    if ("none".equals(strategyType)) {
      discountValue = BigDecimal.ZERO;
    } else {
      discountValue = InputUtils.getValidatedPositiveBigDecimal(scanner, "Enter Discount Value: ");

      if (!InputUtils.validateDiscount(strategyType, discountValue, inventoryItem)) {
        return;  // Stop if validation fails
      }
    }

    // Apply the discount using StoreFacade
    storeFacade.addDiscount(inventoryItem.getItemCode(), discountValue, strategyType);
    System.out.println("Discount added successfully to item: " + inventoryItem.getItemCode());
  }
}
