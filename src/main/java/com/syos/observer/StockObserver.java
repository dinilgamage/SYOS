package com.syos.observer;

import com.syos.model.Inventory;

public interface StockObserver {
  void update(Inventory inventory);
}
