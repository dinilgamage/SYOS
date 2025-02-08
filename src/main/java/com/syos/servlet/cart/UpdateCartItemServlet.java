package com.syos.servlet.cart;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.syos.service.CartService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/updateCartItem")
public class UpdateCartItemServlet extends HttpServlet {
  private final CartService cartService = new CartService();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    try {
      // Parse JSON request body
      BufferedReader reader = request.getReader();
      JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

      // Extract data
      Integer userId = (Integer) request.getSession().getAttribute("userId");

      String itemCode = json.get("itemCode").getAsString();
      int quantity = json.get("quantity").getAsInt();

      boolean success = cartService.updateCartItem(userId, itemCode, quantity);

      // Send response
      JsonObject responseJson = new JsonObject();
      responseJson.addProperty("success", success);
      response.getWriter().write(responseJson.toString());
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      JsonObject responseJson = new JsonObject();
      responseJson.addProperty("success", false);
      responseJson.addProperty("message", e.getMessage());
      response.getWriter().write(responseJson.toString());
    }
  }
}

