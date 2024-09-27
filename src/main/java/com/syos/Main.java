package com.syos;

import com.syos.cli.MainMenu;
import com.syos.cli.OnlineMenu;
import com.syos.cli.StoreMenu;
import com.syos.dao.impl.BillDaoImpl;
import com.syos.dao.impl.BillItemDaoImpl;
import com.syos.dao.impl.InventoryDaoImpl;
import com.syos.dao.impl.StockBatchDaoImpl;
import com.syos.dao.impl.TransactionDaoImpl;
import com.syos.dao.impl.UserDaoImpl;
import com.syos.facade.StoreFacadeImpl;
import com.syos.processor.BillingProcessor;
import com.syos.processor.DiscountProcessor;
import com.syos.processor.ReportProcessor;
import com.syos.processor.ShelfRestockProcessor;
import com.syos.service.BillService;
import com.syos.service.DiscountService;
import com.syos.service.InventoryService;
import com.syos.service.ReportService;
import com.syos.service.TransactionService;
import com.syos.service.UserService;

public class Main {

  public static void main(String[] args) {
    // Initialize the main menu and start the application
    MainMenu mainMenu = initializeMainMenu();
    mainMenu.displayMenu();
  }

  // Extract the initialization logic for the MainMenu
  private static MainMenu initializeMainMenu() {
    StoreFacadeImpl storeFacade = initializeStoreFacade();
    BillingProcessor billingProcessor = new BillingProcessor(storeFacade);
    DiscountProcessor discountProcessor = new DiscountProcessor(storeFacade);
    ReportProcessor reportProcessor = new ReportProcessor(storeFacade);
    ShelfRestockProcessor shelfRestockProcessor = new ShelfRestockProcessor(storeFacade);

    // Initialize the menus
    OnlineMenu onlineMenu = new OnlineMenu(storeFacade,billingProcessor);
    StoreMenu storeMenu = new StoreMenu(billingProcessor, shelfRestockProcessor, reportProcessor, discountProcessor);

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
    return new StoreFacadeImpl(inventoryService, billService, reportService, new InventoryDaoImpl(), discountService,
      new UserService(new UserDaoImpl()));
  }
}
