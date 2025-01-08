package com.syos.test;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentLoginTester {

  private static final String LOGIN_URL = "http://localhost:8080/SYOS/login";
  private static final int NUM_CLIENTS = 5000; // Number of concurrent clients

  public static void main(String[] args) {
    ExecutorService executor = Executors.newFixedThreadPool(NUM_CLIENTS);

    for (int i = 0; i < NUM_CLIENTS; i++) {
      int clientId = i;
      executor.execute(() -> sendLoginRequest(clientId));
    }

    executor.shutdown();
  }

  private static void sendLoginRequest(int clientId) {
    try {
      URL url = new URL(LOGIN_URL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);

      String postData = "email=john@example.com&password=password123";

      try (OutputStream os = connection.getOutputStream()) {
        os.write(postData.getBytes());
      }

      int responseCode = connection.getResponseCode();
      System.out.println("Client " + clientId + " → Response Code: " + responseCode);

    } catch (Exception e) {
      System.out.println("Client " + clientId + " → Error: " + e.getMessage());
    }
  }
}
