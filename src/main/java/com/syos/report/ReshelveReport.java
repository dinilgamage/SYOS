package com.syos.report;

import java.time.LocalDate;
import java.util.List;

import com.syos.dao.InventoryDao;
import com.syos.enums.TransactionType;
import com.syos.model.Inventory;

public class ReshelveReport extends Report {

  private InventoryDao inventoryDao;
  private List<Inventory> itemsToReshelve;

  // Constructor to inject dependencies (InventoryDao)
  public ReshelveReport(InventoryDao inventoryDao) {
    this.inventoryDao = inventoryDao;
  }

  @Override
  protected void prepareHeader() {
    System.out.println("=== Reshelve Report ===");
  }

  @Override
  protected void collectData(LocalDate date, TransactionType type) {
    System.out.println("Collecting reshelve data for " + type);

    // Fetch reshelve data based on the type (online, in-store, or both)
    if (type == TransactionType.BOTH) {
      itemsToReshelve = inventoryDao.getItemsToReshelveForBoth();
    } else if (type == TransactionType.ONLINE) {
      itemsToReshelve = inventoryDao.getItemsToReshelveForOnline();
    } else {
      itemsToReshelve = inventoryDao.getItemsToReshelveForInStore();
    }
  }

  @Override
  protected void formatReport() {
    System.out.println("Formatting Reshelve Report...");
  }

  @Override
  protected void displayReport(TransactionType transactionType) {
    System.out.println("Items that need to be reshelved:");

    for (Inventory item : itemsToReshelve) {
      // Display only the relevant stock information based on the transaction type
      if (transactionType == TransactionType.STORE) {
        if (item.getStoreStock() < item.getShelfCapacity()) {
          System.out.println("Item Code: " + item.getItemCode() +
            ", Name: " + item.getName() +
            ", Current Store Stock: " + item.getStoreStock() +
            ", Shelf Capacity: " + item.getShelfCapacity());
        }
      } else if (transactionType == TransactionType.ONLINE) {
        if (item.getOnlineStock() < item.getShelfCapacity()) {
          System.out.println("Item Code: " + item.getItemCode() +
            ", Name: " + item.getName() +
            ", Current Online Stock: " + item.getOnlineStock() +
            ", Shelf Capacity: " + item.getShelfCapacity());
        }
      } else if (transactionType == TransactionType.BOTH) {
        // Display both store and online stock if BOTH is selected
        if (item.getStoreStock() < item.getShelfCapacity()) {
          System.out.println("Item Code: " + item.getItemCode() +
            ", Name: " + item.getName() +
            ", Current Store Stock: " + item.getStoreStock() +
            ", Shelf Capacity: " + item.getShelfCapacity());
        }
        if (item.getOnlineStock() < item.getShelfCapacity()) {
          System.out.println("Item Code: " + item.getItemCode() +
            ", Name: " + item.getName() +
            ", Current Online Stock: " + item.getOnlineStock() +
            ", Shelf Capacity: " + item.getShelfCapacity());
        }
      }
    }
  }

}
