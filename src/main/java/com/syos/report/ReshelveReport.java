package com.syos.report;

import java.time.LocalDate;
import java.util.List;

import com.syos.dao.InventoryDao;
import com.syos.enums.ReportFilterType;
import com.syos.model.Inventory;

public class ReshelveReport extends Report {

  private InventoryDao inventoryDao;
  private List<Inventory> itemsToReshelve;

  public ReshelveReport(InventoryDao inventoryDao) {
    this.inventoryDao = inventoryDao;
  }

  @Override
  protected void prepareHeader() {
    System.out.println("=== Reshelve Report ===");
  }

  @Override
  protected void collectData(LocalDate date, ReportFilterType type) {
    System.out.println("Collecting reshelve data for " + type);

    if (type == ReportFilterType.BOTH) {
      itemsToReshelve = inventoryDao.getItemsToReshelveForBoth();
    } else if (type == ReportFilterType.ONLINE) {
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
  protected void displayReport(ReportFilterType reportFilterType) {
    System.out.println("Items that need to be reshelved:");

    for (Inventory item : itemsToReshelve) {
      // Display only the relevant stock information based on the transaction type
      if (reportFilterType == ReportFilterType.STORE) {
        if (item.getStoreStock() < item.getShelfCapacity()) {
          System.out.println("Item Code: " + item.getItemCode() +
            ", Name: " + item.getName() +
            ", Current Store Stock: " + item.getStoreStock() +
            ", Shelf Capacity: " + item.getShelfCapacity());
        }
      } else if (reportFilterType == ReportFilterType.ONLINE) {
        if (item.getOnlineStock() < item.getShelfCapacity()) {
          System.out.println("Item Code: " + item.getItemCode() +
            ", Name: " + item.getName() +
            ", Current Online Stock: " + item.getOnlineStock() +
            ", Shelf Capacity: " + item.getShelfCapacity());
        }
      } else if (reportFilterType == ReportFilterType.BOTH) {
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
  // Getter method for itemsToReshelve for testing purposes
  public List<Inventory> getItemsToReshelve() {
    return itemsToReshelve;
  }
}
