package com.syos.processor;

import com.syos.enums.ShelfType;
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
    String shelfTypeString = InputUtils.getValidatedStringOption(scanner, "Enter Shelf Type (store/online): ", "store",
      "online");

    // Convert the string shelfType to the ShelfType enum
    ShelfType shelfType = convertToShelfType(shelfTypeString);

    // Restock the item using StoreFacade
    storeFacade.restockItem(inventoryItem.getItemCode(), shelfType);
  }

  // Helper method to convert string to enum
  private ShelfType convertToShelfType(String shelfTypeString) {
    switch (shelfTypeString.toLowerCase()) {
      case "store":
        return ShelfType.STORE_SHELF;
      case "online":
        return ShelfType.ONLINE_SHELF;
      default:
        throw new IllegalArgumentException("Invalid shelf type: " + shelfTypeString);
    }
  }
}
