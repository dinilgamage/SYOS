package com.syos.dao.impl;

import com.syos.dao.StockBatchDao;
import com.syos.database.DatabaseConnection;
import com.syos.exception.DaoException;
import com.syos.model.StockBatch;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockBatchDaoImpl implements StockBatchDao {

  private static final String SELECT_BATCHES_BY_ITEM_ID = "SELECT * FROM Stock_Batches WHERE item_id = ?";
  private static final String UPDATE_BATCH_SQL = "UPDATE Stock_Batches SET quantity = ?, expiry_date = ? WHERE batch_id = ?";
  private static final String SELECT_NEAREST_EXPIRY_BATCH = "SELECT * FROM Stock_Batches WHERE item_id = ? ORDER BY expiry_date ASC LIMIT 1";
  private static final String SELECT_ALL_STOCK_BATCHES = "SELECT * FROM Stock_Batches";

  @Override
  public List<StockBatch> getBatchesForItem(int itemId) {
    List<StockBatch> batches = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BATCHES_BY_ITEM_ID)) {
      preparedStatement.setInt(1, itemId);
      ResultSet rs = preparedStatement.executeQuery();

      while (rs.next()) {
        StockBatch batch = mapRowToStockBatch(rs);
        batches.add(batch);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving batches for item ID: " + itemId, e);
    }
    return batches;
  }

  @Override
  public void updateBatch(StockBatch batch) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BATCH_SQL)) {

      preparedStatement.setInt(1, batch.getQuantity());
      preparedStatement.setDate(2, Date.valueOf(batch.getExpiryDate()));
      preparedStatement.setInt(3, batch.getBatchId());

      int rowsUpdated = preparedStatement.executeUpdate();
      if (rowsUpdated == 0) {
        throw new DaoException("Update failed, no rows affected for batch ID: " + batch.getBatchId());
      }
    } catch (SQLException e) {
      throw new DaoException("Error updating batch ID: " + batch.getBatchId(), e);
    }
  }

  @Override
  public StockBatch getNearestExpiryBatch(int itemId) {
    StockBatch batch = null;

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_NEAREST_EXPIRY_BATCH)) {
      preparedStatement.setInt(1, itemId);
      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        batch = mapRowToStockBatch(rs);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving nearest expiry batch for item ID: " + itemId, e);
    }
    return batch;
  }

  @Override
  public List<StockBatch> getAllStockBatches() {
    List<StockBatch> stockBatches = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STOCK_BATCHES);
         ResultSet rs = preparedStatement.executeQuery()) {

      while (rs.next()) {
        StockBatch stockBatch = new StockBatch(
          rs.getInt("batch_id"),
          rs.getInt("item_id"),
          rs.getInt("quantity"),
          rs.getDate("date_received").toLocalDate(),
          rs.getDate("expiry_date").toLocalDate()
        );
        stockBatch.setBatchId(rs.getInt("batch_id"));
        stockBatches.add(stockBatch);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving all stock batches", e);
    }
    return stockBatches;
  }

  // Helper method to map a ResultSet row to a StockBatch object
  private StockBatch mapRowToStockBatch(ResultSet rs) throws SQLException {
    int batchId = rs.getInt("batch_id");
    int itemId = rs.getInt("item_id");
    int quantity = rs.getInt("quantity");
    LocalDate dateReceived = rs.getDate("date_received").toLocalDate();
    LocalDate expiryDate = rs.getDate("expiry_date").toLocalDate();

    return new StockBatch(batchId, itemId, quantity, dateReceived, expiryDate);
  }
}
