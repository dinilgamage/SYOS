package com.syos.config;

import com.syos.cli.MainMenu;
import com.syos.cli.OnlineMenu;
import com.syos.cli.StoreMenu;
import com.syos.facade.StoreFacadeImpl;
import com.syos.processor.*;
import com.syos.service.*;
import com.syos.dao.impl.*;

public class AppConfig {

  public StoreFacadeImpl initializeStoreFacade() {
    InventoryService inventoryService = new InventoryService(new InventoryDaoImpl(), new StockBatchDaoImpl());
    BillService billService = new BillService(new BillDaoImpl(), new BillItemDaoImpl(),
      new TransactionService(new TransactionDaoImpl()), inventoryService, new InventoryDaoImpl());
    ReportService reportService = new ReportService(new InventoryDaoImpl(), new TransactionDaoImpl(), new StockBatchDaoImpl());
    DiscountService discountService = new DiscountService();

    return new StoreFacadeImpl(inventoryService, billService, reportService, new InventoryDaoImpl(), discountService,
      new UserService(new UserDaoImpl()));
  }

  public MainMenu initializeMainMenu() {
    StoreFacadeImpl storeFacade = initializeStoreFacade();
    BillingProcessor billingProcessor = new BillingProcessor(storeFacade);
    DiscountProcessor discountProcessor = new DiscountProcessor(storeFacade);
    ReportProcessor reportProcessor = new ReportProcessor(storeFacade);
    ShelfRestockProcessor shelfRestockProcessor = new ShelfRestockProcessor(storeFacade);
    UserRegistrationProcessor userRegistrationProcessor = new UserRegistrationProcessor(storeFacade);
    UserLoginProcessor userLoginProcessor = new UserLoginProcessor(storeFacade);
    UserMenuProcessor userMenuProcessor = new UserMenuProcessor(storeFacade, billingProcessor);

    OnlineMenu onlineMenu = new OnlineMenu(userRegistrationProcessor, userLoginProcessor, userMenuProcessor);
    StoreMenu storeMenu = new StoreMenu(billingProcessor, shelfRestockProcessor, reportProcessor, discountProcessor);

    return new MainMenu(onlineMenu, storeMenu);
  }
}
