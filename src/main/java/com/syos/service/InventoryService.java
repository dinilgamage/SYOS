package com.syos.service;

import com.syos.dao.InventoryDao;
import com.syos.dao.StockBatchDao;
import com.syos.enums.ShelfType;
import com.syos.enums.StockThreshold;
import com.syos.enums.TransactionType;
import com.syos.exception.InsufficientStockException;
import com.syos.model.Inventory;
import com.syos.model.StockBatch;
import com.syos.observer.StockObserver;
import com.syos.observer.StockSubject;

import java.util.ArrayList;
import java.util.List;

public class InventoryService implements StockSubject {

  private InventoryDao inventoryDao;
  private StockBatchDao stockBatchDao;
  private List<StockObserver> observers;

  public InventoryService(InventoryDao inventoryDao, StockBatchDao stockBatchDao) {
    this.inventoryDao = inventoryDao;
    this.stockBatchDao = stockBatchDao;
    this.observers = new ArrayList<>();

  }

  @Override
  public void registerObserver(StockObserver observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(StockObserver observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers(Inventory inventory) {
    for (StockObserver observer : observers) {
      observer.update(inventory);
    }
  }

  public List<Inventory> getAllItems() {
    return inventoryDao.getAllItems();
  }

  public int calculateTotalStockFromBatches(int itemId) {
    List<StockBatch> stockBatches = stockBatchDao.getBatchesForItem(itemId);
    return stockBatches.stream()
      .mapToInt(StockBatch::getQuantity)
      .sum(); // Calculate total stock by summing batch quantities
  }

  public Inventory getItemByCode(String itemCode) {
    return inventoryDao.getItemByCode(itemCode);
  }

  public Inventory getItemById(int itemId) {
    return inventoryDao.getItemById(itemId);
  }

  public boolean checkAvailableStock(Inventory inventoryItem, int quantity, TransactionType transactionType) {
    if (TransactionType.STORE.equals(transactionType)) {

      return inventoryItem.getStoreStock() >= quantity;
    } else if (TransactionType.ONLINE.equals(transactionType)) {

      return inventoryItem.getOnlineStock() >= quantity;
    } else {
      throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
    }
  }

  public void updateInventoryStock(String itemCode, int quantity, TransactionType shelfType) {
    Inventory item = inventoryDao.getItemByCode(itemCode);

    if (item != null) {

      if (TransactionType.STORE.equals(shelfType)) {
        int updatedStoreStock = item.getStoreStock() - quantity;
        item.setStoreStock(updatedStoreStock);
      } else if (TransactionType.ONLINE.equals(shelfType)) {
        int updatedOnlineStock = item.getOnlineStock() - quantity;
        item.setOnlineStock(updatedOnlineStock);
      } else {
        throw new IllegalArgumentException("Invalid shelf type: " + shelfType);
      }


      inventoryDao.updateInventory(item);

    } else {
      throw new IllegalArgumentException("Item not found: " + itemCode);
    }
  }

  // Restock an item based on the shelf type (store or online), replenishing until shelf capacity is reached
  public void restockItem(String itemCode, ShelfType shelfType) {
    Inventory item = inventoryDao.getItemByCode(itemCode);

    if (item != null) {
      // Get all batches sorted by expiry date for the item
      List<StockBatch> batches = stockBatchDao.getBatchesForItem(item.getItemId());
      int totalAvailableQuantity = batches.stream().mapToInt(StockBatch::getQuantity).sum();

      int maxRestockQuantity = getMaxRestockQuantity(item, shelfType);

      if (totalAvailableQuantity < maxRestockQuantity) {
        throw new InsufficientStockException("Insufficient stock across all batches for item: " + itemCode);
      }

      adjustBatchStock(batches, maxRestockQuantity);
      adjustShelfStock(item, shelfType, maxRestockQuantity);

      inventoryDao.updateInventory(item);

      int totalStockAcrossBatches = calculateTotalStockFromBatches(item.getItemId());

      // If the total stock falls below a certain threshold, notify observers
      if (totalStockAcrossBatches < StockThreshold.REORDER_LEVEL.getValue()) {
        notifyObservers(item);
      }

    } else {
      throw new IllegalArgumentException("Item not found: " + itemCode);
    }
  }

  // Adjusts batch stock quantities based on the required amount
  private void adjustBatchStock(List<StockBatch> batches, int requiredQuantity) {
    int remainingQuantity = requiredQuantity;
    for (StockBatch batch : batches) {
      if (remainingQuantity == 0) break;

      int batchQuantity = batch.getQuantity();
      if (batchQuantity >= remainingQuantity) {
        batch.setQuantity(batchQuantity - remainingQuantity);
        remainingQuantity = 0;
      } else {
        batch.setQuantity(0);
        remainingQuantity -= batchQuantity;
      }
      stockBatchDao.updateBatch(batch);
    }
  }

  // Adjusts the store or online stock based on shelf type
  private void adjustShelfStock(Inventory item, ShelfType shelfType, int quantity) {
    if (ShelfType.STORE_SHELF.equals(shelfType)) {
      int currentStoreStock = item.getStoreStock();
      int maxStoreRestock = item.getShelfCapacity() - currentStoreStock;

      // Ensure restock quantity does not exceed shelf capacity
      if (quantity > maxStoreRestock) {
        quantity = maxStoreRestock;
      }

      item.setStoreStock(currentStoreStock + quantity);
    } else if (ShelfType.ONLINE_SHELF.equals(shelfType)) {
      int currentOnlineStock = item.getOnlineStock();
      int maxOnlineRestock = item.getShelfCapacity() - currentOnlineStock;

      // Ensure restock quantity does not exceed shelf capacity
      if (quantity > maxOnlineRestock) {
        quantity = maxOnlineRestock;
      }

      item.setOnlineStock(currentOnlineStock + quantity);
    } else {
      throw new IllegalArgumentException("Invalid shelf type: " + shelfType);
    }
  }

  // Determines the maximum restock quantity based on shelf capacity
  private int getMaxRestockQuantity(Inventory item, ShelfType shelfType) {
    if (ShelfType.STORE_SHELF.equals(shelfType)) {
      return item.getShelfCapacity() - item.getStoreStock();
    } else if (ShelfType.ONLINE_SHELF.equals(shelfType)) {
      return item.getShelfCapacity() - item.getOnlineStock();
    } else {
      throw new IllegalArgumentException("Invalid shelf type: " + shelfType);
    }
  }
}

