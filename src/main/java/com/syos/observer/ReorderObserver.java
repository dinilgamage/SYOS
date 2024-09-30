package com.syos.observer;

import com.syos.model.Inventory;

public class ReorderObserver implements StockObserver {

  @Override
  public void update(Inventory inventory) {
    System.out.println("Reorder alert! Item " + inventory.getItemCode() + " has total stock below the reorder threshold.");
  }
}
