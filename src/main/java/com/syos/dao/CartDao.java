package com.syos.dao;

import com.syos.model.CartItem;
import java.util.List;

public interface CartDao {
  void addToCart(CartItem cartItem);
  void updateCartItem(CartItem cartItem);
  void removeFromCart(int userId, String itemCode);
  List<CartItem> getCartItems(int userId);
  void clearCart(int userId);
  boolean isItemInCart(int userId, String itemCode);
}
