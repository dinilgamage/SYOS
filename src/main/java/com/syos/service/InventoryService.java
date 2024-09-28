package com.syos.service;

import com.syos.dao.InventoryDao;
import com.syos.dao.StockBatchDao;
import com.syos.enums.StockThreshold;
import com.syos.exception.InsufficientStockException;
import com.syos.model.Inventory;
import com.syos.model.StockBatch;
import com.syos.observer.StockObserver;
import com.syos.observer.StockSubject;
import com.syos.strategy.DiscountStrategy;

import java.util.ArrayList;
import java.util.List;

public class InventoryService implements StockSubject {

  private InventoryDao inventoryDao; // DAO for managing Inventory items
  private StockBatchDao stockBatchDao; // DAO for managing stock batches
  private List<StockObserver> observers;  // List of observers

  // Constructor to inject dependencies
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

  /**
   * Retrieves all items from the inventory.
   *
   * @return List of all inventory items.
   */
  public List<Inventory> getAllItems() {
    // Fetch all inventory items from the DAO and return
    return inventoryDao.getAllItems();
  }

  /**
   * Calculates the total stock for a given item across all stock batches.
   *
   * @param itemId - The ID of the inventory item.
   * @return - The total quantity of stock across all batches.
   */
  public int calculateTotalStockFromBatches(int itemId) {
    List<StockBatch> stockBatches = stockBatchDao.getBatchesForItem(itemId);
    return stockBatches.stream()
      .mapToInt(StockBatch::getQuantity)
      .sum(); // Calculate total stock by summing batch quantities
  }

  /**
   * Retrieves an inventory item by its unique code.
   *
   * @param itemCode - The unique code for the inventory item.
   * @return - The Inventory item if found, otherwise null.
   */
  public Inventory getItemByCode(String itemCode) {
    return inventoryDao.getItemByCode(itemCode);
  }

  /**
   * Retrieves an inventory item by its unique code.
   *
   * @param itemId - The unique code for the inventory item.
   * @return - The Inventory item if found, otherwise null.
   */
  public Inventory getItemById(int itemId) {
    return inventoryDao.getItemById(itemId);
  }

  /**
   * Checks if the available stock for a given inventory item meets the required quantity
   * based on the transaction type (store or online).
   *
   * @param inventoryItem The inventory item being checked.
   * @param quantity The quantity requested by the user.
   * @param transactionType The type of transaction (store or online).
   * @return true if sufficient stock is available, false otherwise.
   */
  public boolean checkAvailableStock(Inventory inventoryItem, int quantity, String transactionType) {
    if ("over-the-counter".equals(transactionType)) {
      // Check if store stock is available
      return inventoryItem.getStoreStock() >= quantity;
    } else if ("online".equals(transactionType)) {
      // Check if online stock is available
      return inventoryItem.getOnlineStock() >= quantity;
    } else {
      throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
    }
  }

  /**
   * Updates the stock of an inventory item after a purchase.
   * This updates both the shelf and overall store stock.
   *
   * @param itemCode - The unique code for the inventory item.
   * @param quantity - The quantity to deduct from the stock.
   */
  public void updateInventoryStock(String itemCode, int quantity, String shelfType) {
    Inventory item = inventoryDao.getItemByCode(itemCode);

    if (item != null) {
      // Update stock based on shelf type
      if ("over-the-counter".equalsIgnoreCase(shelfType)) {
        int updatedStoreStock = item.getStoreStock() - quantity;
        item.setStoreStock(updatedStoreStock);
      } else if ("online".equalsIgnoreCase(shelfType)) {
        int updatedOnlineStock = item.getOnlineStock() - quantity;
        item.setOnlineStock(updatedOnlineStock);
      } else {
        throw new IllegalArgumentException("Invalid shelf type: " + shelfType);
      }

      // Update the inventory in the database
      inventoryDao.updateInventory(item);

    } else {
      throw new IllegalArgumentException("Item not found: " + itemCode);
    }
  }

  // Restock an item based on the shelf type (store or online), replenishing until shelf capacity is reached
  public void restockItem(String itemCode, String shelfType) {
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

      // Save the updated inventory
      inventoryDao.updateInventory(item);

      // Calculate the total stock across all batches for the item
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
      stockBatchDao.updateBatch(batch); // Update the batch after adjusting
    }
  }

  // Adjusts the store or online stock based on shelf type
  private void adjustShelfStock(Inventory item, String shelfType, int quantity) {
    if ("store".equalsIgnoreCase(shelfType)) {
      int currentStoreStock = item.getStoreStock();
      int maxStoreRestock = item.getShelfCapacity() - currentStoreStock;

      // Ensure restock quantity does not exceed shelf capacity
      if (quantity > maxStoreRestock) {
        quantity = maxStoreRestock;
      }

      item.setStoreStock(currentStoreStock + quantity);
    } else if ("online".equalsIgnoreCase(shelfType)) {
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
  private int getMaxRestockQuantity(Inventory item, String shelfType) {
    if ("store".equalsIgnoreCase(shelfType)) {
      return item.getShelfCapacity() - item.getStoreStock();
    } else if ("online".equalsIgnoreCase(shelfType)) {
      return item.getShelfCapacity() - item.getOnlineStock();
    } else {
      throw new IllegalArgumentException("Invalid shelf type: " + shelfType);
    }
  }
}

