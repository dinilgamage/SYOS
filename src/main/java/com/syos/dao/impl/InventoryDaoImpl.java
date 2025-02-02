package com.syos.dao.impl;

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
  public void updateInventory(Inventory inventory) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_INVENTORY_SQL)) {

      preparedStatement.setInt(1, inventory.getStoreStock());
      preparedStatement.setInt(2, inventory.getOnlineStock());
      preparedStatement.setString(3, inventory.getDiscountType().toString());
      preparedStatement.setBigDecimal(4, inventory.getDiscountValue());
      preparedStatement.setString(5, inventory.getItemCode());

      int rowsUpdated = preparedStatement.executeUpdate();
      if (rowsUpdated == 0) {
        throw new DaoException("Update failed, no rows affected for item code: " + inventory.getItemCode());
      }

    } catch (SQLException e) {
      throw new DaoException("Error updating inventory for item code: " + inventory.getItemCode(), e);
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
