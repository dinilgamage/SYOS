package com.syos.dao.impl;

import com.syos.dao.OrderDao;
import com.syos.model.Order;
import com.syos.model.OrderItem;
import com.syos.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OrderDaoImpl implements OrderDao {
  @Override
  public void saveOrder(Order order, List<OrderItem> orderItems) throws SQLException {
    try (Connection connection = DatabaseConnection.getConnection()) {
      connection.setAutoCommit(false);

      String orderSql = "INSERT INTO orders (customer_id, transaction_id, order_date, delivery_date, total_amount, payment_method, order_status, shipping_address, billing_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
      try (PreparedStatement orderStmt = connection.prepareStatement(orderSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
        orderStmt.setInt(1, order.getCustomerId());
        orderStmt.setInt(2, order.getTransactionId());
        orderStmt.setTimestamp(3, new java.sql.Timestamp(order.getOrderDate().getTime()));
        orderStmt.setTimestamp(4, new java.sql.Timestamp(order.getDeliveryDate().getTime()));
        orderStmt.setDouble(5, order.getTotalAmount());
        orderStmt.setString(6, order.getPaymentMethod());
        orderStmt.setString(7, order.getOrderStatus());
        orderStmt.setString(8, order.getShippingAddress());
        orderStmt.setString(9, order.getBillingAddress());
        orderStmt.executeUpdate();

        int orderId;
        try (var rs = orderStmt.getGeneratedKeys()) {
          if (rs.next()) {
            orderId = rs.getInt(1);
          } else {
            throw new SQLException("Failed to retrieve order ID");
          }
        }

        String orderItemSql = "INSERT INTO order_items (order_id, product_id, product_name, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement orderItemStmt = connection.prepareStatement(orderItemSql)) {
          for (OrderItem item : orderItems) {
            orderItemStmt.setInt(1, orderId);
            orderItemStmt.setString(2, item.getProductId());
            orderItemStmt.setString(3, item.getProductName());
            orderItemStmt.setDouble(4, item.getPrice());
            orderItemStmt.setInt(5, item.getQuantity());
            orderItemStmt.setDouble(6, item.getSubtotal());
            orderItemStmt.addBatch();
          }
          orderItemStmt.executeBatch();
        }

        connection.commit();
      } catch (SQLException e) {
        connection.rollback();
        throw e;
      }
    }
  }
}