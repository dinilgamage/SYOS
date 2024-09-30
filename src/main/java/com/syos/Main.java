package com.syos;

import com.syos.api.HttpServerInitializer;
import com.syos.cli.MainMenu;
import com.syos.config.AppConfig;
import com.syos.dao.impl.BillDaoImpl;
import com.syos.dao.impl.BillItemDaoImpl;
import com.syos.dao.impl.InventoryDaoImpl;
import com.syos.dao.impl.StockBatchDaoImpl;
import com.syos.dao.impl.TransactionDaoImpl;
import com.syos.dao.impl.UserDaoImpl;
import com.syos.observer.ReorderObserver;
import com.syos.service.InventoryService;
import com.syos.service.BillService;
import com.syos.service.ReportService;
import com.syos.dao.InventoryDao;
import com.syos.service.DiscountService;
import com.syos.service.TransactionService;
import com.syos.service.UserService;

import java.io.IOException;

public class Main {

  public static void main(String[] args) {
    AppConfig appConfig = new AppConfig(); // Create an instance of AppConfig
    MainMenu mainMenu = appConfig.initializeMainMenu(); // Use it to initialize MainMenu

    // Initialize the DAOs and Services
    InventoryDaoImpl inventoryDao = new InventoryDaoImpl();
    StockBatchDaoImpl stockBatchDao = new StockBatchDaoImpl();
    InventoryService inventoryService = new InventoryService(inventoryDao, stockBatchDao);

    // Register the ReorderObserver to monitor stock levels
    ReorderObserver reorderObserver = new ReorderObserver();
    inventoryService.registerObserver(reorderObserver);

    BillService billService = new BillService(new BillDaoImpl(), new BillItemDaoImpl(),
      new TransactionService(new TransactionDaoImpl()), inventoryService, new InventoryDaoImpl());
    ReportService reportService = new ReportService(new InventoryDaoImpl(), new TransactionDaoImpl(), new StockBatchDaoImpl());
    DiscountService discountService = new DiscountService();
    UserService userService = new UserService(new UserDaoImpl());

    // Start the CLI Menu (for in-store operations)
    new Thread(mainMenu::displayMenu).start();

    // Start the HTTP Server (for online store operations)
    HttpServerInitializer httpServerInitializer = new HttpServerInitializer();
    try {
      httpServerInitializer.startServer(inventoryService, billService, reportService, inventoryDao, discountService, userService);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
