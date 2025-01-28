package com.syos.service;

import com.syos.model.Order;
import com.syos.model.OrderItem;
import com.syos.model.CartItem;
import com.syos.dao.OrderDao;
import com.syos.dao.CartDao;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {
  private OrderDao orderDao;
  private CartDao cartDao;

  public OrderService(OrderDao orderDao, CartDao cartDao) {
    this.orderDao = orderDao;
    this.cartDao = cartDao;
  }

  public void processOrder(int userId, String deliveryAddress, String paymentDetails) throws Exception {
    List<CartItem> cartItems = cartDao.getCartItems(userId);
    if (cartItems.isEmpty()) {
      throw new Exception("Cart is empty");
    }

    List<OrderItem> orderItems = cartItems.stream()
      .map(this::convertToOrderItem)
      .collect(Collectors.toList());

    double totalAmount = orderItems.stream().mapToDouble(OrderItem::getSubtotal).sum();
    Order order = new Order();
    order.setCustomerId(userId);
    order.setTransactionId(generateTransactionId());
    order.setOrderDate(new Date());
    order.setDeliveryDate(calculateDeliveryDate());
    order.setTotalAmount(totalAmount);
    order.setPaymentMethod(paymentDetails);
    order.setOrderStatus("Processing");
    order.setShippingAddress(deliveryAddress);
    order.setBillingAddress(deliveryAddress); // Assuming billing address is same as shipping address

    orderDao.saveOrder(order, orderItems);
    cartDao.clearCart(userId);
  }

  private OrderItem convertToOrderItem(CartItem cartItem) {
    OrderItem orderItem = new OrderItem();
    orderItem.setProductId(cartItem.getItemCode());
    orderItem.setProductName(cartItem.getItemName());
    orderItem.setPrice(cartItem.getPrice());
    orderItem.setQuantity(cartItem.getQuantity());
    orderItem.setSubtotal(cartItem.getPrice() * cartItem.getQuantity());
    return orderItem;
  }

  private int generateTransactionId() {
    // Implement logic to generate a unique transaction ID
    return 0;
  }

  private Date calculateDeliveryDate() {
    // Implement logic to calculate the delivery date
    return new Date();
  }
}