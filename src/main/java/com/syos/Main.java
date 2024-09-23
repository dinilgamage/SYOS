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
import com.syos.service.InventoryService;
import com.syos.service.ReportService;
import com.syos.service.TransactionService;

public class Main {

  public static void main(String[] args) {
    // Initialize dependencies
    InventoryService inventoryService = new InventoryService(new InventoryDaoImpl(), new StockBatchDaoImpl());
    BillService billService = new BillService(new BillDaoImpl(), new BillItemDaoImpl(),
      new TransactionService(new TransactionDaoImpl()), inventoryService);
    ReportService reportService = new ReportService(new InventoryDaoImpl(), new TransactionDaoImpl(), new StockBatchDaoImpl());

    // Initialize the StoreFacade
    StoreFacadeImpl storeFacade = new StoreFacadeImpl(inventoryService, billService, reportService);

    // Initialize the menus
    OnlineMenu onlineMenu = new OnlineMenu(storeFacade);
    StoreMenu storeMenu = new StoreMenu(storeFacade);
    MainMenu mainMenu = new MainMenu(onlineMenu, storeMenu);

    // Display the main menu
    mainMenu.displayMenu();
  }
}
