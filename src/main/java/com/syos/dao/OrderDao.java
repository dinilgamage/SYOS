package com.syos.dao;

import com.syos.model.Order;
import com.syos.model.OrderItem;

import java.sql.SQLException;
import java.util.List;

public interface OrderDao {
  void saveOrder(Order order, List<OrderItem> orderItems) throws SQLException;
}