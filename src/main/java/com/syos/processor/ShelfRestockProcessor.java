package com.syos.processor;

import com.syos.facade.StoreFacade;
import com.syos.model.Inventory;
import com.syos.util.InputUtils;

import java.util.Scanner;

public class ShelfRestockProcessor {

  private final StoreFacade storeFacade;

  public ShelfRestockProcessor(StoreFacade storeFacade) {
    this.storeFacade = storeFacade;
  }

  public void restockShelf(Scanner scanner) {
    System.out.println("=== Restock Shelf ===");

    // Use the utility method to validate and get the item
    Inventory inventoryItem = InputUtils.getValidatedInventoryItem(storeFacade, scanner, "Enter Item Code: ");

    // Use the utility method to validate shelf type (store or online)
    String shelfType = InputUtils.getValidatedStringOption(scanner, "Enter Shelf Type (store/online): ", "store", "online");

    // Restock the item using StoreFacade
    storeFacade.restockItem(inventoryItem.getItemCode(), shelfType);
    System.out.println("Item " + inventoryItem.getItemCode() + " restocked to " + shelfType + ".");
  }
}
