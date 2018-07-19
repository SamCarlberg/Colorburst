package com.github.samcarlberg.colorburst;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
    stage.setMinWidth(1000);
    stage.setMinHeight(600);
    stage.getScene().getStylesheets().add("/com/github/samcarlberg/colorburst/midnight.css");
    stage.setTitle("Colorburst");
    stage.getIcons().addAll(
        new Image(Colorburst.class.getResourceAsStream("/com/github/samcarlberg/colorburst/icon-16.png")),
        new Image(Colorburst.class.getResourceAsStream("/com/github/samcarlberg/colorburst/icon-32.png")),
        new Image(Colorburst.class.getResourceAsStream("/com/github/samcarlberg/colorburst/icon-64.png")),
        new Image(Colorburst.class.getResourceAsStream("/com/github/samcarlberg/colorburst/icon-128.png"))
    );
    stage.setOnCloseRequest(e -> {
      Platform.exit();
    });
    stage.show();
  }

}
