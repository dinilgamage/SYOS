package com.syos.command;

import com.syos.service.InventoryService;

/**
 * Command to restock an item in the store or online.
 */
public class RestockCommand implements Command {

  private InventoryService inventoryService;
  private String itemCode;
  private int quantity;
  private String shelfType;  // 'store' or 'online'

  // Constructor
  public RestockCommand(InventoryService inventoryService, String itemCode, int quantity, String shelfType) {
    this.inventoryService = inventoryService;
    this.itemCode = itemCode;
    this.quantity = quantity;
    this.shelfType = shelfType;
  }

  @Override
  public void execute() {
    // Delegate restocking to the InventoryService
    inventoryService.restockItem(itemCode, quantity, shelfType);
  }
}
