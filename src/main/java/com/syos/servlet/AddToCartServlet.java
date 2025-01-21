package com.syos.servlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.syos.model.CartItem;
import com.syos.service.CartService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

@WebServlet("/addToCart")
public class AddToCartServlet extends HttpServlet {

  private final CartService cartService = new CartService();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    response.setContentType("application/json");
    PrintWriter out = response.getWriter();

    try {
      // Parse JSON request body
      StringBuilder stringBuilder = new StringBuilder();
      BufferedReader reader = request.getReader();
      String line;
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
      }

      JsonObject json = JsonParser.parseString(stringBuilder.toString()).getAsJsonObject();

      // Extract cart item data
      int userId = (int) request.getSession().getAttribute("userId");
      String itemCode = json.get("itemCode").getAsString();
      String itemName = json.get("name").getAsString();
      BigDecimal price = json.get("price").getAsBigDecimal();
      int quantity = json.get("quantity").getAsInt();

      // Check if item is already in the cart
      if (cartService.isItemInCart(userId, itemCode)) {
        // Respond with item already in cart status
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        responseJson.addProperty("message", "Item already in cart.");
        out.print(responseJson.toString());
        return;
      }

      // Create CartItem and save to DB
      CartItem cartItem = new CartItem(userId, itemCode, itemName, quantity, price);
      cartService.addToCart(cartItem);

      // Respond with success
      JsonObject responseJson = new JsonObject();
      responseJson.addProperty("success", true);
      out.print(responseJson.toString());
    } catch (Exception e) {
      e.printStackTrace();

      // Respond with failure
      JsonObject responseJson = new JsonObject();
      responseJson.addProperty("success", false);
      responseJson.addProperty("message", e.getMessage());
      out.print(responseJson.toString());
    }
  }
}
