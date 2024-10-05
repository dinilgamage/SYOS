package com.syos.dao.impl;

import com.syos.dao.BillDao;
import com.syos.database.DatabaseConnection;
import com.syos.exception.DaoException;
import com.syos.model.Bill;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillDaoImpl implements BillDao {

  private static final String INSERT_BILL_SQL = "INSERT INTO Bill (transaction_id, total_amount, cash_tendered, change_amount) VALUES (?, ?, ?, ?)";
  private static final String SELECT_BILL_BY_ID = "SELECT * FROM Bill WHERE bill_id = ?";
  private static final String SELECT_BILLS_BY_DATE = "SELECT * FROM Bill WHERE bill_date = ?";

  @Override
  public Bill getBillById(int billId) {
    Bill bill = null;
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BILL_BY_ID)) {

      preparedStatement.setInt(1, billId);
      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        bill = mapRowToBill(rs);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving bill with ID: " + billId, e);
    }
    return bill;
  }

  @Override
  public void saveBill(Bill bill) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BILL_SQL, Statement.RETURN_GENERATED_KEYS)) {

      preparedStatement.setInt(1, bill.getTransactionId());
      preparedStatement.setBigDecimal(2, bill.getTotalAmount());
      preparedStatement.setBigDecimal(3, bill.getCashTendered());
      preparedStatement.setBigDecimal(4, bill.getChangeAmount());

      int affectedRows = preparedStatement.executeUpdate();

      // Retrieve the generated bill_id and set it in the Bill object
      if (affectedRows > 0) {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            bill.setBillId(generatedKeys.getInt(1));
          }
        }
      } else {
        throw new DaoException("Inserting the bill failed, no rows affected.");
      }

    } catch (SQLException e) {
      throw new DaoException("Error saving bill", e);
    }
  }

  @Override
  public List<Bill> getBillsByDate(LocalDate date) {
    List<Bill> bills = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BILLS_BY_DATE)) {

      preparedStatement.setDate(1, Date.valueOf(date));
      ResultSet rs = preparedStatement.executeQuery();

      while (rs.next()) {
        bills.add(mapRowToBill(rs));
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving bills for date: " + date, e);
    }
    return bills;
  }

  // Helper method to map a ResultSet row to a Bill object
  private Bill mapRowToBill(ResultSet rs) throws SQLException {
    int billId = rs.getInt("bill_id");
    int transactionId = rs.getInt("transaction_id");
    BigDecimal totalAmount = rs.getBigDecimal("total_amount");
    BigDecimal cashTendered = rs.getBigDecimal("cash_tendered");
    BigDecimal changeAmount = rs.getBigDecimal("change_amount");

    Bill bill = new Bill(transactionId, totalAmount, cashTendered, changeAmount);
    bill.setBillId(billId);
    return bill;
  }
}
