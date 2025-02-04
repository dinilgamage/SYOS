package com.syos.service;

import com.syos.dao.OrderDao;
import com.syos.exception.InsufficientStockException;
import com.syos.model.Order;
import com.syos.model.OrderItem;
import com.syos.model.CartItem;
import com.syos.dao.CartDao;
import com.syos.enums.TransactionType;
import com.syos.model.Transaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {
  private OrderDao orderDao;
  private CartDao cartDao;
  private TransactionService transactionService;
  private InventoryService inventoryService;

  public OrderService(OrderDao orderDao,
    CartDao cartDao,
    TransactionService transactionService,
    InventoryService inventoryService) {
    this.orderDao = orderDao;
    this.cartDao = cartDao;
    this.transactionService = transactionService;
    this.inventoryService = inventoryService;
  }

  public synchronized void processOrder(Order order) throws Exception {
    List<CartItem> cartItems = cartDao.getCartItems(order.getCustomerId());
    if (cartItems.isEmpty()) {
      throw new Exception("Cart is empty");
    }

    List<OrderItem> orderItems = cartItems.stream()
      .map(this::convertToOrderItem)
      .collect(Collectors.toList());

    processOrder(order,
      orderItems);
    cartDao.clearCart(order.getCustomerId());
  }

  public synchronized void processOrder(Order order, int orderId) throws Exception {
    List<OrderItem> orderItems = orderDao.getOrderItems(orderId);
    if (orderItems.isEmpty()) {
      throw new Exception("Order is empty");
    }

    processOrder(order,
      orderItems);
  }

  private void processOrder(Order order, List<OrderItem> orderItems) throws Exception {
    double totalAmount = orderItems.stream().mapToDouble(OrderItem::getSubtotal).sum();
    Transaction transaction = transactionService.createTransaction(TransactionType.ONLINE,
      BigDecimal.valueOf(totalAmount),
      order.getCustomerId());

    order.setTransactionId(transaction.getTransactionId());
    order.setOrderDate(new Date());
    order.setDeliveryDate(calculateDeliveryDate());
    order.setTotalAmount(totalAmount);
    order.setOrderStatus("Processing");
    order.setOrderItems(orderItems);

    // Update inventory stock levels
    for (OrderItem orderItem : orderItems) {
      if (inventoryService.checkAvailableStock(orderItem.getProductId(),
        orderItem.getQuantity(),
        TransactionType.ONLINE)) {
        inventoryService.updateInventoryStock(orderItem.getProductId(),
          orderItem.getQuantity(),
          TransactionType.ONLINE);
      } else {
        throw new InsufficientStockException("Insufficient stock");
      }

    }
    orderDao.saveOrder(order, orderItems);
  }

  public Order getOrderById(int orderId) throws Exception {
    return orderDao.getOrderById(orderId);
  }

  public List<Order> getOrdersByUserId(int userId) throws Exception {
    return orderDao.getOrdersByUserId(userId);
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

  private Date calculateDeliveryDate() {
    // Implement logic to calculate the delivery date
    return new Date();
  }
}