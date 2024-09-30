package com.syos.api.handlers;

import com.syos.dao.InventoryDao;
import com.syos.enums.TransactionType;
import com.syos.facade.StoreFacade;
import com.syos.facade.StoreFacadeImpl;
import com.syos.model.BillItem;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.syos.service.BillService;
import com.syos.service.DiscountService;
import com.syos.service.InventoryService;
import com.syos.service.ReportService;
import com.syos.service.UserService;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PurchaseHandler implements HttpHandler {

  private final StoreFacade storeFacade;

  public PurchaseHandler(InventoryService inventoryService, BillService billService,
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
      String[] purchaseData = scanner.nextLine().split("&"); // Example: itemCode=ABC123&quantity=2&email=john@mail.com
      String itemCode = purchaseData[0].split("=")[1];
      int quantity = Integer.parseInt(purchaseData[1].split("=")[1]);
      String email = purchaseData[2].split("=")[1];

      Integer userId = storeFacade.getUserId(email);
      List<BillItem> billItems = new ArrayList<>();
      BillItem billItem = new BillItem(itemCode, quantity, BigDecimal.valueOf(10)); // Placeholder price, can be fetched from the inventory
      billItems.add(billItem);

      storeFacade.generateBill(billItems, TransactionType.ONLINE, BigDecimal.ZERO, userId);

      String response = "Purchase successful!";
      exchange.sendResponseHeaders(200, response.getBytes().length);
      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.close();
    } else {
      exchange.sendResponseHeaders(405, -1); // Method Not Allowed
    }
  }
}
