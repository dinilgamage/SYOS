package com.syos.service;

import com.syos.dao.InventoryDao;
import com.syos.dao.StockBatchDao;
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

      // Calculate the total stock across all batches for the item
      int totalStockAcrossBatches = calculateTotalStockFromBatches(item.getItemId());

      // If the total stock falls below a certain threshold, notify observers
      if (totalStockAcrossBatches < 50) {  // 50 is an example threshold
        notifyObservers(item);
      }

    } else {
      throw new IllegalArgumentException("Item not found: " + itemCode);
    }
  }


  // Restock an item based on the shelf type (store or online), ensuring it doesn't exceed shelf capacity
  public void restockItem(String itemCode, int quantity, String shelfType) {
    Inventory item = inventoryDao.getItemByCode(itemCode);

    if (item != null) {
      // Get the nearest expiry batch for the item
      StockBatch nearestBatch = stockBatchDao.getNearestExpiryBatch(item.getItemId());

      if (nearestBatch != null && nearestBatch.getQuantity() >= quantity) {
        // Check which shelf is being restocked
        if ("store".equalsIgnoreCase(shelfType)) {
          int currentStoreStock = item.getStoreStock();
          int maxStoreRestock = item.getShelfCapacity() - currentStoreStock;

          // Ensure the restock quantity does not exceed shelf capacity
          if (quantity > maxStoreRestock) {
            quantity = maxStoreRestock;  // Adjust quantity to fit within the shelf capacity
          }

          // Update store stock
          int updatedStoreStock = currentStoreStock + quantity;
          item.setStoreStock(updatedStoreStock);

        } else if ("online".equalsIgnoreCase(shelfType)) {
          int currentOnlineStock = item.getOnlineStock();
          int maxOnlineRestock = item.getShelfCapacity() - currentOnlineStock;

          // Ensure the restock quantity does not exceed shelf capacity
          if (quantity > maxOnlineRestock) {
            quantity = maxOnlineRestock;  // Adjust quantity to fit within the shelf capacity
          }

          // Update online stock
          int updatedOnlineStock = currentOnlineStock + quantity;
          item.setOnlineStock(updatedOnlineStock);

        } else {
          throw new IllegalArgumentException("Invalid shelf type: " + shelfType);
        }

        // Update the stock batch quantity
        int remainingBatchQuantity = nearestBatch.getQuantity() - quantity;
        nearestBatch.setQuantity(remainingBatchQuantity);

        // Save the changes to the inventory and stock batch
        inventoryDao.updateInventory(item);
        stockBatchDao.updateBatch(nearestBatch);

      } else {
        throw new IllegalArgumentException("Insufficient stock in nearest batch for item: " + itemCode);
      }
    } else {
      throw new IllegalArgumentException("Item not found: " + itemCode);
    }
  }
}

