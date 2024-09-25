package com.syos.dao.impl;

import com.syos.dao.BillItemDao;
import com.syos.database.DatabaseConnection;
import com.syos.model.BillItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillItemDaoImpl implements BillItemDao {

  private static final String INSERT_BILL_ITEM_SQL = "INSERT INTO Bill_Items (bill_id, item_id, quantity, item_price, total_price) VALUES (?, ?, ?, ?, ?)";
  private static final String SELECT_BILL_ITEMS_BY_BILL_ID = "SELECT * FROM Bill_Items WHERE bill_id = ?";

  @Override
  public void saveBillItem(BillItem billItem) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BILL_ITEM_SQL)) {

      preparedStatement.setInt(1, billItem.getBillId());
      preparedStatement.setInt(2, billItem.getItemId());
      preparedStatement.setInt(3, billItem.getQuantity());
      preparedStatement.setBigDecimal(4, billItem.getItemPrice());
      preparedStatement.setBigDecimal(5, billItem.getTotalPrice());

      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      // Proper error handling can be implemented here
    }
  }

  @Override
  public List<BillItem> getBillItemsByBillId(int billId) {
    List<BillItem> billItems = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BILL_ITEMS_BY_BILL_ID)) {

      preparedStatement.setInt(1, billId);
      ResultSet rs = preparedStatement.executeQuery();

      while (rs.next()) {
        BillItem billItem = mapRowToBillItem(rs);
        billItems.add(billItem);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Proper error handling can be implemented here
    }
    return billItems;
  }

  // Helper method to map a ResultSet row to a BillItem object
  private BillItem mapRowToBillItem(ResultSet rs) throws SQLException {
    int billItemId = rs.getInt("bill_item_id");
    int billId = rs.getInt("bill_id");
    int itemId = rs.getInt("item_id");
    int quantity = rs.getInt("quantity");
    BigDecimal itemPrice = rs.getBigDecimal("item_price");
    BigDecimal totalPrice = rs.getBigDecimal("total_price");

    // Create a BillItem object using the updated constructor
    BillItem billItem = new BillItem(itemId, quantity, itemPrice);
    billItem.setBillId(billId);  // Set the billId to associate it with the correct bill
    billItem.setTotalPrice(totalPrice);  // Set the total price

    return billItem;
  }
}

