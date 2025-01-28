// File: src/main/java/com/syos/servlet/ProcessCheckoutServlet.java
package com.syos.servlet;

import com.syos.dao.impl.CartDaoImpl;
import com.syos.dao.impl.OrderDaoImpl;
import com.syos.service.OrderService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

@WebServlet("/processCheckout")
public class ProcessCheckoutServlet extends HttpServlet {
  private OrderService orderService;

  @Override
  public void init() throws ServletException {
    super.init();
    orderService = new OrderService(new OrderDaoImpl(), new CartDaoImpl());
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int userId = (int) request.getSession().getAttribute("userId");
    String deliveryAddress = request.getParameter("deliveryAddress");
    String paymentDetails = request.getParameter("paymentDetails");

    try {
      orderService.processOrder(userId, deliveryAddress, paymentDetails);
      response.sendRedirect("orderConfirmation.jsp");
    } catch (Exception e) {
      e.printStackTrace();
      response.sendRedirect("checkout.jsp?error=true");
    }
  }
}