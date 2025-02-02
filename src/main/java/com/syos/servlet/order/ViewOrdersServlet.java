package com.syos.servlet.order;

import com.google.gson.Gson;
import com.syos.dao.impl.CartDaoImpl;
import com.syos.dao.impl.InventoryDaoImpl;
import com.syos.dao.impl.OrderDaoImpl;
import com.syos.dao.impl.TransactionDaoImpl;
import com.syos.model.Order;
import com.syos.service.InventoryService;
import com.syos.service.OrderService;
import com.syos.service.TransactionService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/viewOrders")
public class ViewOrdersServlet extends HttpServlet {
  private OrderService orderService;

  @Override
  public void init() {
    orderService = new OrderService(new OrderDaoImpl(), new CartDaoImpl(),
      new TransactionService(new TransactionDaoImpl()), new InventoryService(new InventoryDaoImpl()));
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int customerId = (int) request.getSession().getAttribute("userId");
    try {
      List<Order> orders = orderService.getOrdersByUserId(customerId);
      response.setContentType("application/json");
      response.getWriter().write(new Gson().toJson(orders));
    } catch (Exception e) {
      e.printStackTrace();
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to fetch orders");
    }
  }
}