package com.syos.test;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

public class LoadTest {
  private static final int NUM_USERS = 100;  // Number of concurrent users
  private static final String BASE_URL = "http://localhost:8080/SYOS_war_exploded";

  public static void main(String[] args) throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(NUM_USERS);
    List<User> users = generateUsers(NUM_USERS); // Generate new users

    for (User user : users) {
      executorService.submit(() -> {
        try {
          simulateUserSession(user);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }

    executorService.shutdown();
    executorService.awaitTermination(10, TimeUnit.MINUTES);
  }

  private static void simulateUserSession(User user) throws Exception {
    CloseableHttpClient client = HttpClients.createDefault();
    HttpClientContext context = HttpClientContext.create();
    context.setCookieStore(new BasicCookieStore()); // Maintain session

    System.out.println("User " + user.email + " logging in...");
    boolean loggedIn = sendPostRequest(client, context, BASE_URL + "/login",
      "email=" + user.email + "&password=" + user.password);

    if (loggedIn) {
      System.out.println("User " + user.email + " adding items to cart...");
      sendPostRequest(client, context, BASE_URL + "/addToCart",
        "{\"itemCode\": \"F003\", \"name\": \"Grapes 1kg\", \"price\": 200, \"quantity\": 1}");

      sendPostRequest(client, context, BASE_URL + "/addToCart",
        "{\"itemCode\": \"F004\", \"name\": \"Orange 1kg\", \"price\": 140, \"quantity\": 2}");

      System.out.println("User " + user.email + " updating cart...");
      sendPostRequest(client, context, BASE_URL + "/updateCartItem",
        "{ \"itemCode\": \"F004\", \"quantity\": 3}");

      System.out.println("User " + user.email + " removing an item...");
      sendPostRequest(client, context, BASE_URL + "/removeFromCart",
        "{ \"itemCode\": \"F003\"}");

      System.out.println("User " + user.email + " placing an order...");
      sendPostRequest(client, context, BASE_URL + "/placeOrder",
        "{}");

      System.out.println("User " + user.email + " logging out...");
      sendPostRequest(client, context, BASE_URL + "/logout", "");
    } else {
      System.out.println("User " + user.email + " failed to log in.");
    }

    client.close();
  }

  private static boolean sendPostRequest(HttpClient client, HttpClientContext context, String url, String formData) {
    try {
      HttpPost post = new HttpPost(url);
      post.setHeader("Content-Type", "application/json");

      if (!formData.isEmpty()) {
        post.setEntity(new StringEntity(formData));
      }

      String response = EntityUtils.toString(client.execute(post, context).getEntity());
      System.out.println("Response from " + url + ": " + response); // Print the response for debugging

      // Check if the response contains "success": true
      JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
      return jsonResponse.get("success").getAsBoolean();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private static List<User> generateUsers(int count) {
    List<User> users = new ArrayList<>();
    for (int i = 1; i <= count; i++) {
      String email = "user" + i + "@example.com";
      String password = "password" + i;
      String name = "User" + i;
      users.add(new User(email, password, name));
    }
    return users;
  }

  static class User {
    String email, password, name;

    User(String email, String password, String name) {
      this.email = email;
      this.password = password;
      this.name = name;
    }
  }
}