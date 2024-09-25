package com.syos;

import com.syos.cli.MainMenu;
import com.syos.cli.OnlineMenu;
import com.syos.cli.StoreMenu;
import com.syos.dao.impl.BillDaoImpl;
import com.syos.dao.impl.BillItemDaoImpl;
import com.syos.dao.impl.InventoryDaoImpl;
import com.syos.dao.impl.StockBatchDaoImpl;
import com.syos.dao.impl.TransactionDaoImpl;
import com.syos.facade.StoreFacadeImpl;
import com.syos.service.BillService;
import com.syos.service.DiscountService;
import com.syos.service.InventoryService;
import com.syos.service.ReportService;
import com.syos.service.TransactionService;

public class Main {

  public static void main(String[] args) {
    // Initialize the main menu and start the application
    MainMenu mainMenu = initializeMainMenu();
    mainMenu.displayMenu();
  }

  // Extract the initialization logic for the MainMenu
  private static MainMenu initializeMainMenu() {
    StoreFacadeImpl storeFacade = initializeStoreFacade();

    // Initialize the menus
    OnlineMenu onlineMenu = new OnlineMenu(storeFacade);
    StoreMenu storeMenu = new StoreMenu(storeFacade);

    // Return the MainMenu object
    return new MainMenu(onlineMenu, storeMenu);
  }

  // Extract the initialization logic for StoreFacadeImpl
  private static StoreFacadeImpl initializeStoreFacade() {
    // Initialize dependencies
    InventoryService inventoryService = new InventoryService(new InventoryDaoImpl(), new StockBatchDaoImpl());
    BillService billService = new BillService(new BillDaoImpl(), new BillItemDaoImpl(),
      new TransactionService(new TransactionDaoImpl()), inventoryService, new InventoryDaoImpl());
    ReportService reportService = new ReportService(new InventoryDaoImpl(), new TransactionDaoImpl(), new StockBatchDaoImpl());
    DiscountService discountService = new DiscountService();

    // Return the initialized StoreFacadeImpl
    return new StoreFacadeImpl(inventoryService, billService, reportService, new InventoryDaoImpl(), discountService);
  }
}
