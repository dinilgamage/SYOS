package com.syos.processor;

import com.syos.enums.DiscountType;
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

    Inventory inventoryItem = InputUtils.getValidatedInventoryItem(storeFacade, scanner, "Enter Item Code: ");

    String discountTypeStr = InputUtils.getValidatedStringOption(scanner,
      "Enter Discount Strategy (percentage/fixed/none): ",
      "percentage", "fixed", "none");

    DiscountType discountType = DiscountType.fromString(discountTypeStr);

    BigDecimal discountValue;

    if (DiscountType.NONE.equals(discountType)) {
      discountValue = BigDecimal.ZERO;
    } else {
      discountValue = InputUtils.getValidatedPositiveBigDecimal(scanner, "Enter Discount Value: ");

      if (!InputUtils.validateDiscount(discountType, discountValue, inventoryItem)) {
        return;
      }
    }

    // Apply the discount using StoreFacade
    storeFacade.addDiscount(inventoryItem.getItemCode(), discountValue, discountType);
    System.out.println("Discount added successfully to item: " + inventoryItem.getItemCode());
  }
}
