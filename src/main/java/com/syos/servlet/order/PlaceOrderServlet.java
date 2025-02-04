package com.syos.servlet.order;

import com.syos.dao.impl.CartDaoImpl;
import com.syos.dao.impl.InventoryDaoImpl;
import com.syos.dao.impl.OrderDaoImpl;
import com.syos.dao.impl.TransactionDaoImpl;
import com.syos.exception.InsufficientStockException;
import com.syos.model.Order;
import com.syos.service.InventoryService;
import com.syos.service.OrderService;
import com.syos.service.TransactionService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet("/placeOrder")
public class PlaceOrderServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(PlaceOrderServlet.class.getName());
  private OrderService orderService;

  @Override
  public void init() throws ServletException {
    super.init();
    orderService = new OrderService(new OrderDaoImpl(), new CartDaoImpl(),
      new TransactionService(new TransactionDaoImpl()), new InventoryService(new InventoryDaoImpl()));
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int customerId = (int) request.getSession().getAttribute("userId");
    String email = request.getParameter("email");
    String firstName = request.getParameter("firstName");
    String lastName = request.getParameter("lastName");
    String shippingMethod = request.getParameter("shippingMethod");
    String paymentMethod = request.getParameter("paymentMethod");
    String address = request.getParameter("address");
    String apartment = request.getParameter("apartment");
    String city = request.getParameter("city");
    String postalCode = request.getParameter("postalCode");
    String phone = request.getParameter("phone");
    String orderIdParam = request.getParameter("orderId");

    Order order = new Order();
    order.setCustomerId(customerId);
    order.setEmail(email);
    order.setFirstName(firstName);
    order.setLastName(lastName);
    order.setShippingMethod(shippingMethod);
    order.setPaymentMethod(paymentMethod);
    order.setAddress(address);
    order.setApartment(apartment);
    order.setCity(city);
    order.setPostalCode(postalCode);
    order.setPhone(phone);

    try {
      if (orderIdParam != null && !orderIdParam.isEmpty()) {
        int orderId = Integer.parseInt(orderIdParam);
        orderService.processOrder(order, orderId);
      } else {
        orderService.processOrder(order);
      }
      response.sendRedirect("home.jsp?orderSuccess=true");
    } catch (InsufficientStockException e) {
      logger.log(Level.WARNING, "Insufficient stock for order: " + order, e);
      response.sendRedirect("home.jsp?insufficientStock=true");
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error processing order: " + order, e);
      response.sendRedirect("checkout.jsp?orderSuccess=false");
    }
  }
}