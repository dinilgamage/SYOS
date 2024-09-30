package com.syos.api.handlers;

import com.syos.dao.InventoryDao;
import com.syos.dao.TransactionDao;
import com.syos.dao.impl.BillDaoImpl;
import com.syos.dao.impl.BillItemDaoImpl;
import com.syos.dao.impl.InventoryDaoImpl;
import com.syos.dao.impl.StockBatchDaoImpl;
import com.syos.dao.impl.TransactionDaoImpl;
import com.syos.dao.impl.UserDaoImpl;
import com.syos.facade.StoreFacade;
import com.syos.facade.StoreFacadeImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.syos.service.BillService;
import com.syos.service.DiscountService;
import com.syos.service.InventoryService;
import com.syos.service.ReportService;
import com.syos.service.TransactionService;
import com.syos.service.UserService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class UserLoginHandler implements HttpHandler {

  private final StoreFacade storeFacade;

  public UserLoginHandler(InventoryService inventoryService, BillService billService,
    ReportService reportService, InventoryDao inventoryDao,
    DiscountService discountService, UserService userService) {
    // Initialize the StoreFacade with required services
    this.storeFacade = new StoreFacadeImpl(inventoryService, billService, reportService, inventoryDao, discountService, userService);
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("POST".equals(exchange.getRequestMethod())) {
      // Parse input from request body
      Scanner scanner = new Scanner(exchange.getRequestBody());
      String[] loginData = scanner.nextLine().split("&"); // Expected format: email=john@mail.com&password=pass123
      String email = loginData[0].split("=")[1];
      String password = loginData[1].split("=")[1];

      boolean isAuthenticated = storeFacade.loginUser(email, password);
      String response;
      if (isAuthenticated) {
        response = "Login Successful!";
        exchange.sendResponseHeaders(200, response.getBytes().length);
      } else {
        response = "Invalid Credentials!";
        exchange.sendResponseHeaders(401, response.getBytes().length); // Unauthorized
      }

      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.close();
    } else {
      exchange.sendResponseHeaders(405, -1); // Method Not Allowed
    }
  }
}
