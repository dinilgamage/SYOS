package com.syos.observer;

import com.syos.model.Inventory;

public class ReorderObserver implements StockObserver {

  // lets try to store this in inventory cus reorder thresholds can change with item
  private static final int REORDER_THRESHOLD = 50;  // Example reorder threshold

  @Override
  public void update(Inventory inventory) {
    // Assuming Inventory has the total stock already updated or accessible via the service
    System.out.println("Reorder alert! Item " + inventory.getItemCode() + " has total stock below the reorder threshold.");
    // Trigger any additional reorder actions, such as generating a reorder report or sending a notification
  }
}
