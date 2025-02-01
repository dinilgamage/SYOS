package com.syos.dao.impl;

import com.syos.dao.OrderDao;
import com.syos.database.DatabaseConnection;
import com.syos.model.Order;
import com.syos.model.OrderItem;
import com.syos.exception.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

  private static final String INSERT_ORDER_SQL = "INSERT INTO `Order` (customer_id, transaction_id, order_date, delivery_date, total_amount, payment_method, order_status, email, first_name, last_name, address, apartment, city, postal_code, phone, shipping_method) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String INSERT_ORDER_ITEM_SQL = "INSERT INTO OrderItem (order_id, item_code, item_name, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
  private static final String SELECT_ORDER_BY_ID = "SELECT * FROM `Order` WHERE order_id = ?";
  private static final String SELECT_ORDERS_BY_USER_ID = "SELECT * FROM `Order` WHERE customer_id = ?";

  @Override
  public void saveOrder(Order order, List<OrderItem> orderItems) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement orderStatement = connection.prepareStatement(INSERT_ORDER_SQL, Statement.RETURN_GENERATED_KEYS);
         PreparedStatement orderItemStatement = connection.prepareStatement(INSERT_ORDER_ITEM_SQL)) {

      connection.setAutoCommit(false);

      orderStatement.setInt(1, order.getCustomerId());
      orderStatement.setInt(2, order.getTransactionId());
      orderStatement.setTimestamp(3, new Timestamp(order.getOrderDate().getTime()));
      orderStatement.setTimestamp(4, new Timestamp(order.getDeliveryDate().getTime()));
      orderStatement.setDouble(5, order.getTotalAmount());
      orderStatement.setString(6, order.getPaymentMethod());
      orderStatement.setString(7, order.getOrderStatus());
      orderStatement.setString(8, order.getEmail());
      orderStatement.setString(9, order.getFirstName());
      orderStatement.setString(10, order.getLastName());
      orderStatement.setString(11, order.getAddress());
      orderStatement.setString(12, order.getApartment());
      orderStatement.setString(13, order.getCity());
      orderStatement.setString(14, order.getPostalCode());
      orderStatement.setString(15, order.getPhone());
      orderStatement.setString(16, order.getShippingMethod());
      orderStatement.executeUpdate();

      try (ResultSet generatedKeys = orderStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          int orderId = generatedKeys.getInt(1);
          order.setOrderId(orderId);

          for (OrderItem item : orderItems) {
            orderItemStatement.setInt(1, orderId);
            orderItemStatement.setString(2, item.getProductId());
            orderItemStatement.setString(3, item.getProductName());
            orderItemStatement.setDouble(4, item.getPrice());
            orderItemStatement.setInt(5, item.getQuantity());
            orderItemStatement.setDouble(6, item.getSubtotal());
            orderItemStatement.addBatch();
          }
          orderItemStatement.executeBatch();
        }
      }

      connection.commit();
    } catch (SQLException e) {
      throw new DaoException("Error saving order", e);
    }
  }

  @Override
  public Order getOrderById(int orderId) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ORDER_BY_ID)) {

      statement.setInt(1, orderId);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapRowToOrder(resultSet);
        } else {
          throw new DaoException("Order not found");
        }
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving order by ID: " + orderId, e);
    }
  }

  @Override
  public List<Order> getOrdersByUserId(int userId) {
    List<Order> orders = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ORDERS_BY_USER_ID)) {

      statement.setInt(1, userId);
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          orders.add(mapRowToOrder(resultSet));
        }
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving orders for user ID: " + userId, e);
    }
    return orders;
  }

  private Order mapRowToOrder(ResultSet resultSet) throws SQLException {
    Order order = new Order();
    order.setOrderId(resultSet.getInt("order_id"));
    order.setCustomerId(resultSet.getInt("customer_id"));
    order.setTransactionId(resultSet.getInt("transaction_id"));
    order.setOrderDate(resultSet.getTimestamp("order_date"));
    order.setDeliveryDate(resultSet.getTimestamp("delivery_date"));
    order.setTotalAmount(resultSet.getDouble("total_amount"));
    order.setPaymentMethod(resultSet.getString("payment_method"));
    order.setOrderStatus(resultSet.getString("order_status"));
    order.setEmail(resultSet.getString("email"));
    order.setFirstName(resultSet.getString("first_name"));
    order.setLastName(resultSet.getString("last_name"));
    order.setAddress(resultSet.getString("address"));
    order.setApartment(resultSet.getString("apartment"));
    order.setCity(resultSet.getString("city"));
    order.setPostalCode(resultSet.getString("postal_code"));
    order.setPhone(resultSet.getString("phone"));
    order.setShippingMethod(resultSet.getString("shipping_method"));
    return order;
  }
}