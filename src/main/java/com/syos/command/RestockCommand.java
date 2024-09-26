package com.syos.command;

import com.syos.exception.InsufficientStockException;
import com.syos.service.InventoryService;

/**
 * Command to restock an item in the store or online.
 */
public class RestockCommand implements Command {

  private InventoryService inventoryService;
  private String itemCode;
  private String shelfType;  // 'store' or 'online'

  // Constructor
  public RestockCommand(InventoryService inventoryService, String itemCode, String shelfType) {
    this.inventoryService = inventoryService;
    this.itemCode = itemCode;
    this.shelfType = shelfType;
  }

  @Override
  public void execute() {
    try {
      // Delegate restocking to the InventoryService
      inventoryService.restockItem(itemCode, shelfType);
    } catch (InsufficientStockException e) {
      System.out.println("Error during restocking: " + e.getMessage());
    }
  }
}
