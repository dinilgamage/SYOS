package com.syos.api.handlers;

import com.syos.facade.StoreFacade;
import com.syos.facade.StoreFacadeImpl;
import com.syos.service.InventoryService;
import com.syos.service.BillService;
import com.syos.service.ReportService;
import com.syos.dao.InventoryDao;
import com.syos.service.DiscountService;
import com.syos.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class UserRegistrationHandler implements HttpHandler {

  private final StoreFacade storeFacade;

  public UserRegistrationHandler(InventoryService inventoryService, BillService billService,
    ReportService reportService, InventoryDao inventoryDao,
    DiscountService discountService, UserService userService) {
    // Initialize the StoreFacade with required services
    this.storeFacade = new StoreFacadeImpl(inventoryService, billService, reportService, inventoryDao, discountService, userService);
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("POST".equals(exchange.getRequestMethod())) {
      // Parse the input from request body
      Scanner scanner = new Scanner(exchange.getRequestBody());
      String[] userData = scanner.nextLine().split("&"); // Expected format: name=John&email=john@mail.com&password=pass123
      String name = userData[0].split("=")[1];
      String email = userData[1].split("=")[1];
      String password = userData[2].split("=")[1];

      try {
        storeFacade.registerUser(name, email, password);
        String response = "Registration Successful!";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
      } catch (Exception e) {
        String response = "Registration Failed: " + e.getMessage();
        exchange.sendResponseHeaders(400, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
      }
    } else {
      exchange.sendResponseHeaders(405, -1); // Method Not Allowed
    }
  }
}
