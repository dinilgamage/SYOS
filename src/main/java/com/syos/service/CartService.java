package com.syos.service;

import com.syos.dao.CartDao;
import com.syos.dao.impl.CartDaoImpl;
import com.syos.model.CartItem;

import java.util.List;

public class CartService {

  private final CartDao cartDao;

  public CartService() {
    this.cartDao = new CartDaoImpl();
  }

  public void addToCart(CartItem cartItem) {
    cartDao.addToCart(cartItem);
  }

  public void updateCartItem(CartItem cartItem) {
    cartDao.updateCartItem(cartItem);
  }

  public void removeFromCart(int userId, String itemCode) {
    cartDao.removeFromCart(userId, itemCode);
  }

  public List<CartItem> getCartItems(int userId) {
    return cartDao.getCartItems(userId);
  }

  public void clearCart(int userId) {
    cartDao.clearCart(userId);
  }

  public boolean isItemInCart(int userId, String itemCode) {
    return cartDao.isItemInCart(userId, itemCode);
  }
}
