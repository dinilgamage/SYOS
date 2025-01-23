package com.syos.servlet;

import com.syos.dao.impl.InventoryDaoImpl;
import com.syos.service.CartService;
import com.syos.service.InventoryService;
import com.syos.model.Inventory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
  private InventoryService inventoryService;
  private CartService cartService;

  @Override
  public void init() throws ServletException {
    this.inventoryService = new InventoryService(new InventoryDaoImpl());
    this.cartService = new CartService();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    Integer userId = (Integer) request.getSession().getAttribute("userId");
    if (userId == null) {
      response.sendRedirect("login.jsp");
      return;
    }

    List<Inventory> inventoryItems = inventoryService.getAllItems();

    // this slows down the login time by 2 sec
    int cartSize = cartService.getCartSize(userId);

    request.setAttribute("inventoryItems", inventoryItems);
    request.setAttribute("cartSize", cartSize);
    request.getSession().setAttribute("cartSize", cartSize);
    request.getRequestDispatcher("dashboard.jsp").forward(request, response);
  }
}
