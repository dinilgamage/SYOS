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
      statement.setBigDecimal(5, cartItem.getPrice());

      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error adding item to cart", e);
    }
  }

  @Override
  public void updateCartItem(CartItem cartItem) {
    String sql = "UPDATE Cart SET quantity = ?, price = ? WHERE user_id = ? AND item_code = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, cartItem.getQuantity());
      statement.setBigDecimal(2, cartItem.getPrice());
      statement.setInt(3, cartItem.getUserId());
      statement.setString(4, cartItem.getItemCode());

      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error updating cart item", e);
    }
  }

  @Override
  public void removeFromCart(int userId, String itemCode) {
    String sql = "DELETE FROM Cart WHERE user_id = ? AND item_code = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, userId);
      statement.setString(2, itemCode);

      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error removing item from cart", e);
    }
  }

  @Override
  public List<CartItem> getCartItems(int userId) {
    String sql = "SELECT * FROM Cart WHERE user_id = ?";
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
          resultSet.getBigDecimal("price")
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
}
