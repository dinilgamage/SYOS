package com.syos.servlet;

import com.google.gson.Gson;
import com.syos.model.CartItem;
import com.syos.service.CartService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/getCartItems")
public class GetCartItemsServlet extends HttpServlet {
  private final CartService cartService = new CartService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int userId = (int) request.getSession().getAttribute("userId");
    List<CartItem> cartItems = cartService.getCartItems(userId);

    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    out.print(new Gson().toJson(cartItems));
  }
}
