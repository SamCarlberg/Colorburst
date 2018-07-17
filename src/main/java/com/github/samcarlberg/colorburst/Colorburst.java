package com.github.samcarlberg.colorburst;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public final class Colorburst extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws IOException {
    Pane rootPane = FXMLLoader.load(MainWindowController.class.getResource("MainWindow.fxml"));
    stage.setScene(new Scene(rootPane));
    stage.setTitle("Colorburst");
    stage.setOnCloseRequest(e -> {
      Platform.exit();
    });
    stage.show();
  }

}
