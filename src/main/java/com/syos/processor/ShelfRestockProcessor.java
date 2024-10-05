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

    Inventory inventoryItem = InputUtils.getValidatedInventoryItem(storeFacade, scanner, "Enter Item Code: ");

    String shelfTypeString = InputUtils.getValidatedStringOption(scanner, "Enter Shelf Type (store/online): ", "store",
      "online");

    ShelfType shelfType = ShelfType.fromString(shelfTypeString);

    // Restock the item using StoreFacade
    storeFacade.restockItem(inventoryItem.getItemCode(), shelfType);
  }

}
