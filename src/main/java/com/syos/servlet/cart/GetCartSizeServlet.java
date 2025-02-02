package com.syos.servlet.cart;

import com.syos.service.CartService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/getCartSize")
public class GetCartSizeServlet extends HttpServlet {
  private final CartService cartService = new CartService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Integer userId = (Integer) request.getSession().getAttribute("userId");
    int cartSize = (userId != null) ? cartService.getCartSize(userId) : 0;

    response.setContentType("application/json");
    response.getWriter().write("{\"cartSize\": " + cartSize + "}");
  }
}