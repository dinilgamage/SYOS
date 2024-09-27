package com.syos;

import com.syos.cli.MainMenu;
import com.syos.config.AppConfig;

public class Main {

  public static void main(String[] args) {
    AppConfig appConfig = new AppConfig(); // Create an instance of AppConfig
    MainMenu mainMenu = appConfig.initializeMainMenu(); // Use it to initialize MainMenu
    mainMenu.displayMenu(); // Start the application
  }
}
