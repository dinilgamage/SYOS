package com.syos;

import com.syos.cli.MainMenu;
import com.syos.config.AppConfig;

public class Main {

  public static void main(String[] args) {
    AppConfig appConfig = new AppConfig();
    MainMenu mainMenu = appConfig.initializeMainMenu();
    mainMenu.displayMenu();
  }
}
