package com.syos.api.handlers;

import com.syos.dao.InventoryDao;
import com.syos.facade.StoreFacade;
import com.syos.facade.StoreFacadeImpl;
import com.syos.model.Inventory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.syos.service.BillService;
import com.syos.service.DiscountService;
import com.syos.service.InventoryService;
import com.syos.service.ReportService;
import com.syos.service.UserService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class InventoryHandler implements HttpHandler {

  private final StoreFacade storeFacade;

  public InventoryHandler(InventoryService inventoryService, BillService billService,
    ReportService reportService, InventoryDao inventoryDao,
    DiscountService discountService, UserService userService) {
    // Initialize the StoreFacade with required services
    this.storeFacade = new StoreFacadeImpl(inventoryService, billService, reportService, inventoryDao, discountService, userService);
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("GET".equals(exchange.getRequestMethod())) {
      List<Inventory> inventoryList = storeFacade.getAllItems();
      StringBuilder response = new StringBuilder("Available Items:\n");

      for (Inventory item : inventoryList) {
        response.append("Item Code: ").append(item.getItemCode())
          .append(", Name: ").append(item.getName())
          .append(", Stock: ").append(item.getOnlineStock())
          .append("\n");
      }

      exchange.sendResponseHeaders(200, response.toString().getBytes().length);
      OutputStream os = exchange.getResponseBody();
      os.write(response.toString().getBytes());
      os.close();
    } else {
      exchange.sendResponseHeaders(405, -1); // Method Not Allowed
    }
  }
}
