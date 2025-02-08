package com.syos.dao.impl;

import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;
import com.syos.dao.InventoryDao;
import com.syos.database.DatabaseConnection;
import com.syos.enums.DiscountType;
import com.syos.enums.StockThreshold;
import com.syos.exception.DaoException;
import com.syos.model.Inventory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDaoImpl implements InventoryDao {

  private static final String SELECT_ITEM_BY_CODE = "SELECT * FROM Inventory WHERE item_code = ?";
  private static final String SELECT_ITEM_BY_ID = "SELECT * FROM Inventory WHERE item_id = ?";
  private static final String UPDATE_INVENTORY_SQL = "UPDATE Inventory SET store_stock = ?, online_stock = ?, discount_strategy = ?, discount_value = ? WHERE item_code = ?";
  private static final String SELECT_LOW_STOCK_ITEMS = "SELECT * FROM Inventory WHERE store_stock < ? OR online_stock < ?";
  private static final String SELECT_ITEMS_TO_RESHELVE_FOR_IN_STORE = "SELECT * FROM Inventory WHERE store_stock < shelf_capacity";
  private static final String SELECT_ITEMS_TO_RESHELVE_FOR_ONLINE = "SELECT * FROM Inventory WHERE online_stock < shelf_capacity";
  private static final String SELECT_ITEMS_TO_RESHELVE_FOR_BOTH = "SELECT * FROM Inventory WHERE store_stock < shelf_capacity OR online_stock < shelf_capacity";
  private static final String SELECT_ITEMS_BELOW_REORDER_LEVEL = "SELECT * FROM Inventory WHERE (store_stock + online_stock) < ?";
  private static final String SELECT_ALL_ITEMS = "SELECT * FROM Inventory";
  private static final String SELECT_ITEMS_BY_CATEGORY = "SELECT * FROM Inventory WHERE category = ?";
  private static final String SELECT_ITEMS_BY_NAME = "SELECT * FROM Inventory WHERE name LIKE ?";

  @Override
  public Inventory getItemByCode(String itemCode) {
    Inventory inventory = null;
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ITEM_BY_CODE)) {

      preparedStatement.setString(1, itemCode);
      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        inventory = mapRowToInventory(rs);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving item by code: " + itemCode, e);
    }
    return inventory;
  }

  @Override
  public Inventory getItemById(int itemId) {
    Inventory inventory = null;
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ITEM_BY_ID)) {

      preparedStatement.setInt(1, itemId);
      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        inventory = mapRowToInventory(rs);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving item by ID: " + itemId, e);
    }
    return inventory;
  }

  @Override
  public List<Inventory> getAllItems() {
    List<Inventory> items = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ITEMS);
         ResultSet rs = preparedStatement.executeQuery()) {

      while (rs.next()) {
        Inventory inventory = mapRowToInventory(rs);
        items.add(inventory);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving all items", e);
    }
    System.out.println("All items: " + items);
    return items;
  }

  @Override
  public List<Inventory> getItemsByCategory(String category) {
    List<Inventory> items = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ITEMS_BY_CATEGORY)) {

      preparedStatement.setString(1, category);
      ResultSet rs = preparedStatement.executeQuery();

      while (rs.next()) {
        Inventory item = mapRowToInventory(rs);
        items.add(item);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving items by category: " + category, e);
    }
    return items;
  }

  @Override
  public List<Inventory> searchItemsByName(String name) {
    List<Inventory> items = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ITEMS_BY_NAME)) {

      preparedStatement.setString(1, "%" + name + "%");
      try (ResultSet rs = preparedStatement.executeQuery()) {
        while (rs.next()) {
          Inventory item = mapRowToInventory(rs);
          items.add(item);
        }
      }
    } catch (SQLException e) {
      throw new DaoException("Error searching items by name: " + name, e);
    }
    return items;
  }

  @Override
  public void updateInventory(Inventory inventory) {
    int maxRetries = 3; // Maximum retry attempts
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
      Connection connection = null;
      try {
        connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false); // Begin transaction

        String lockSql = "SELECT store_stock, online_stock FROM Inventory WHERE item_code = ? FOR UPDATE";
        try (PreparedStatement lockStmt = connection.prepareStatement(lockSql)) {
          lockStmt.setString(1, inventory.getItemCode());
          try (ResultSet resultSet = lockStmt.executeQuery()) {
            if (!resultSet.next()) {
              throw new DaoException("Item not found for update: " + inventory.getItemCode());
            }
          }
        }

        // Step 2: Perform the inventory update
        try (PreparedStatement updateStmt = connection.prepareStatement(UPDATE_INVENTORY_SQL)) {
          updateStmt.setInt(1, inventory.getStoreStock());
          updateStmt.setInt(2, inventory.getOnlineStock());
          updateStmt.setString(3, inventory.getDiscountType().toString());
          updateStmt.setBigDecimal(4, inventory.getDiscountValue());
          updateStmt.setString(5, inventory.getItemCode());

          int rowsUpdated = updateStmt.executeUpdate();
          if (rowsUpdated == 0) {
            throw new DaoException("Update failed, no rows affected for item code: " + inventory.getItemCode());
          }
        }

        connection.commit(); // Commit if everything succeeds
        return;
      } catch (MySQLTransactionRollbackException e) {
        if (attempt == maxRetries) {
          throw new DaoException("Deadlock detected, retries exhausted for item code: " + inventory.getItemCode(), e);
        }
        try {
          Thread.sleep(100);
        } catch (InterruptedException ignored) {}

      } catch (SQLException e) {
        if (connection != null) {
          try {
            connection.rollback();
          } catch (SQLException rollbackEx) {
            throw new DaoException("Transaction rollback failed", rollbackEx);
          }
        }
        throw new DaoException("Error updating inventory for item code: " + inventory.getItemCode(), e);

      } finally {
        if (connection != null) {
          try {
            connection.setAutoCommit(true);
            connection.close();
          } catch (SQLException closeEx) {
            throw new DaoException("Error closing connection", closeEx);
          }
        }
      }
    }
  }


  @Override
  public List<Inventory> getLowStockItems() {
    List<Inventory> lowStockItems = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_LOW_STOCK_ITEMS)) {

      preparedStatement.setInt(1, StockThreshold.REORDER_LEVEL.getValue());
      preparedStatement.setInt(2, StockThreshold.REORDER_LEVEL.getValue());

      ResultSet rs = preparedStatement.executeQuery();
      while (rs.next()) {
        Inventory inventory = mapRowToInventory(rs);
        lowStockItems.add(inventory);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving low stock items", e);
    }
    return lowStockItems;
  }

  @Override
  public List<Inventory> getItemsBelowReorderLevel(int threshold) {
    List<Inventory> items = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ITEMS_BELOW_REORDER_LEVEL)) {

      preparedStatement.setInt(1, threshold);
      ResultSet rs = preparedStatement.executeQuery();

      while (rs.next()) {
        Inventory item = mapRowToInventory(rs);
        items.add(item);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving items below reorder level", e);
    }
    return items;
  }

  @Override
  public List<Inventory> getItemsToReshelveForInStore() {
    return getItemsToReshelve(SELECT_ITEMS_TO_RESHELVE_FOR_IN_STORE);
  }

  @Override
  public List<Inventory> getItemsToReshelveForOnline() {
    return getItemsToReshelve(SELECT_ITEMS_TO_RESHELVE_FOR_ONLINE);
  }

  @Override
  public List<Inventory> getItemsToReshelveForBoth() {
    return getItemsToReshelve(SELECT_ITEMS_TO_RESHELVE_FOR_BOTH);
  }

  // Helper method to execute the query and map results to Inventory objects
  private List<Inventory> getItemsToReshelve(String query) {
    List<Inventory> items = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query);
         ResultSet rs = preparedStatement.executeQuery()) {

      while (rs.next()) {
        Inventory item = mapRowToInventory(rs);
        items.add(item);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving items to reshelve", e);
    }
    return items;
  }

  // Helper method to map a ResultSet row to an Inventory object
  private Inventory mapRowToInventory(ResultSet rs) throws SQLException {
    int itemId = rs.getInt("item_id");
    String itemCode = rs.getString("item_code");
    String name = rs.getString("name");
    String desc = rs.getString("desc");
    String category = rs.getString("category");
    BigDecimal price = rs.getBigDecimal("price");
    String discountStrategyStr = rs.getString("discount_strategy");
    BigDecimal discountValue = rs.getBigDecimal("discount_value");
    int storeStock = rs.getInt("store_stock");
    int onlineStock = rs.getInt("online_stock");
    int shelfCapacity = rs.getInt("shelf_capacity");

    DiscountType discountType = DiscountType.fromString(discountStrategyStr);  // Convert String to Enum

    Inventory inventory = new Inventory(itemCode, name, desc, category, price, storeStock, onlineStock, shelfCapacity);
    inventory.setDiscountType(discountType);
    inventory.setDiscountValue(discountValue);
    inventory.setItemId(itemId);

    return inventory;
  }
}
