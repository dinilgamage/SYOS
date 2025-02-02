package com.syos.servlet;

import com.syos.dao.impl.InventoryDaoImpl;
import com.syos.service.CartService;
import com.syos.service.InventoryService;
import com.syos.model.Inventory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
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

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    JsonObject jsonResponse = new JsonObject();
    jsonResponse.add("inventoryItems", new Gson().toJsonTree(inventoryItems));
    response.getWriter().write(jsonResponse.toString());
  }
}