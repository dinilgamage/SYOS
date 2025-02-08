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
import java.util.logging.Level;

@WebServlet("/orderDetails")
public class OrderDetailsServlet extends HttpServlet {
  private OrderService orderService;

  @Override
  public void init() {
    orderService = new OrderService(new OrderDaoImpl(), new CartDaoImpl(),
      new TransactionService(new TransactionDaoImpl()), new InventoryService(new InventoryDaoImpl()));
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String orderIdParam = request.getParameter("orderId");
    if (orderIdParam == null || orderIdParam.isEmpty()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID is required");
      return;
    }

    try {
      int orderId = Integer.parseInt(orderIdParam);
      Order order = orderService.getOrderById(orderId);
      if (order == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
        return;
      }

      response.setContentType("application/json");
      response.getWriter().write(new Gson().toJson(order));
    } catch (NumberFormatException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Order ID");
    } catch (Exception e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to fetch order details");
    }
  }
}