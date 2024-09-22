package com.syos.observer;

import com.syos.model.Inventory;

public interface StockSubject {
  void registerObserver(StockObserver observer);
  void removeObserver(StockObserver observer);
  void notifyObservers(Inventory inventory);
}
