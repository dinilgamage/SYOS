package com.syos.servlet;

import com.syos.dao.impl.InventoryDaoImpl;
import com.syos.service.InventoryService;
import com.syos.model.Inventory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
  private InventoryService inventoryService;

  @Override
  public void init() throws ServletException {
    this.inventoryService = new InventoryService(new InventoryDaoImpl());
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    List<Inventory> inventoryItems = inventoryService.getAllItems();
    request.setAttribute("inventoryItems", inventoryItems);
    request.getRequestDispatcher("dashboard.jsp").forward(request, response);
  }
}
