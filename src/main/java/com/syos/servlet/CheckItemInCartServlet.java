package com.syos.servlet;

import com.google.gson.JsonObject;
import com.syos.service.CartService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/isItemInCart")
public class CheckItemInCartServlet extends HttpServlet {

  private final CartService cartService = new CartService();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();

    try {
      int userId = (int) request.getSession().getAttribute("userId");
      String itemCode = request.getParameter("itemCode");

      boolean isInCart = cartService.isItemInCart(userId, itemCode);

      JsonObject jsonResponse = new JsonObject();
      jsonResponse.addProperty("isInCart", isInCart);
      out.print(jsonResponse.toString());
    } catch (Exception e) {
      e.printStackTrace();

      JsonObject jsonResponse = new JsonObject();
      jsonResponse.addProperty("error", "Failed to check item in cart.");
      out.print(jsonResponse.toString());
    }
  }
}

