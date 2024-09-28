package com.syos.command;

import com.syos.enums.ShelfType;
import com.syos.exception.InsufficientStockException;
import com.syos.service.InventoryService;

/**
 * Command to restock an item in the store or online.
 */
public class RestockCommand implements Command {

  private InventoryService inventoryService;
  private String itemCode;
  private ShelfType shelfType;  // 'store' or 'online'

  // Constructor
  public RestockCommand(InventoryService inventoryService, String itemCode, ShelfType shelfType) {
    this.inventoryService = inventoryService;
    this.itemCode = itemCode;
    this.shelfType = shelfType;
  }

  @Override
  public void execute() {
    try {
      // Delegate restocking to the InventoryService
      inventoryService.restockItem(itemCode, shelfType);
      System.out.println("Restocking successful for item: " + itemCode);
    } catch (InsufficientStockException e) {
      System.out.println("Error during restocking: " + e.getMessage());
    }
  }
}
