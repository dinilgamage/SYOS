package com.syos.dao.impl;

import com.syos.dao.CartDao;
import com.syos.database.DatabaseConnection;
import com.syos.model.CartItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDaoImpl implements CartDao {

  @Override
  public void addToCart(CartItem cartItem) {
    String sql = "INSERT INTO Cart (user_id, item_code, item_name, quantity, price) VALUES (?, ?, ?, ?, ?)";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, cartItem.getUserId());
      statement.setString(2, cartItem.getItemCode());
      statement.setString(3, cartItem.getItemName());
      statement.setInt(4, cartItem.getQuantity());
      statement.setDouble(5, cartItem.getPrice());

      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error adding item to cart", e);
    }
  }

  @Override
  public boolean updateCartItem(int userId, String itemCode, int quantity) {
    String sql = "UPDATE Cart SET quantity = ? WHERE user_id = ? AND item_code = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, quantity);
      statement.setInt(2, userId);
      statement.setString(3, itemCode);

      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      throw new RuntimeException("Error updating cart item", e);
    }
  }

  @Override
  public boolean removeFromCart(int userId, String itemCode) {
    String sql = "DELETE FROM Cart WHERE user_id = ? AND item_code = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, userId);
      statement.setString(2, itemCode);

      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      throw new RuntimeException("Error removing item from cart", e);
    }
  }

  @Override
  public List<CartItem> getCartItems(int userId) {
    String sql = "SELECT c.user_id, c.item_code, c.item_name, c.quantity, c.price, i.online_stock AS stock " +
      "FROM Cart c " +
      "JOIN Inventory i ON c.item_code = i.item_code " +
      "WHERE c.user_id = ?";
    List<CartItem> cartItems = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, userId);
      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        CartItem cartItem = new CartItem(
          resultSet.getInt("user_id"),
          resultSet.getString("item_code"),
          resultSet.getString("item_name"),
          resultSet.getInt("quantity"),
          resultSet.getDouble("price"),
          resultSet.getInt("stock") // Retrieve stock from the result set
        );
        cartItems.add(cartItem);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error fetching cart items", e);
    }

    return cartItems;
  }

  @Override
  public void clearCart(int userId) {
    String sql = "DELETE FROM Cart WHERE user_id = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, userId);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error clearing cart", e);
    }
  }

  @Override
  public boolean isItemInCart(int userId, String itemCode) {
    String sql = "SELECT * FROM Cart WHERE user_id = ? AND item_code = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, userId);
      statement.setString(2, itemCode);
      ResultSet resultSet = statement.executeQuery();

      return resultSet.next();
    } catch (SQLException e) {
      throw new RuntimeException("Error checking if item is in cart", e);
    }
  }

  @Override
  public int getCartSize(int userId) {
    String sql = "SELECT COUNT(*) FROM Cart WHERE user_id = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, userId);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return resultSet.getInt(1);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting cart size", e);
    }
    return 0;
  }
}
