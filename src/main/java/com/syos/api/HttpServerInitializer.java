package com.syos.api;

import com.syos.api.handlers.UserRegistrationHandler;
import com.syos.api.handlers.UserLoginHandler;
import com.syos.api.handlers.InventoryHandler;
import com.syos.api.handlers.PurchaseHandler;
import com.syos.service.InventoryService;
import com.syos.service.BillService;
import com.syos.service.ReportService;
import com.syos.dao.InventoryDao;
import com.syos.service.DiscountService;
import com.syos.service.UserService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpServerInitializer {

  public void startServer(InventoryService inventoryService, BillService billService,
    ReportService reportService, InventoryDao inventoryDao,
    DiscountService discountService, UserService userService) throws IOException {

    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

    // Register your endpoints with the required dependencies
    server.createContext("/register", new UserRegistrationHandler(inventoryService, billService, reportService, inventoryDao, discountService, userService));
    server.createContext("/login", new UserLoginHandler(inventoryService, billService, reportService, inventoryDao, discountService, userService));
    server.createContext("/inventory", new InventoryHandler(inventoryService, billService, reportService, inventoryDao, discountService, userService));
    server.createContext("/purchase", new PurchaseHandler(inventoryService, billService, reportService, inventoryDao, discountService, userService));

    server.setExecutor(null); // Use default executor
    server.start();
    System.out.println("Server started on port 8080");
  }
}
