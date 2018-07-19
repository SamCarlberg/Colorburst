package com.github.samcarlberg.colorburst;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.UnaryOperator;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;

public final class MainWindowController {

  @FXML
  private Pane root;
  @FXML
  private ComboBox<SeedLocation> seedLocationChooser;
  @FXML
  private ImageView imageView;
  @FXML
  private TextField width;
  @FXML
  private TextField height;
  @FXML
  private ColorPicker colorPicker;
  @FXML
  private Pane progressInfo;
  @FXML
  private Label progressLabel;
  @FXML
  private Label timeLabel;
  @FXML
  private ProgressBar progressBar;
  @FXML
  private Button saveButton;

  private Colors colors;
  private File lastSaveDir;
  private Timer timer;

  @FXML
  private void initialize() {
    seedLocationChooser.getItems().setAll(SeedLocation.values());
    seedLocationChooser.getSelectionModel().select(SeedLocation.BOTTOM_CENTER);
    UnaryOperator<TextFormatter.Change> onlyIntegerText = c -> {
      if (c.getControlNewText().matches("^|\\d+$")) {
        return c;
      } else {
        return null;
      }
    };
    width.setTextFormatter(new TextFormatter<>(onlyIntegerText));
    height.setTextFormatter(new TextFormatter<>(onlyIntegerText));
  }

  @FXML
  private void start() {
    if (colors != null) {
      colors.stop();
      timer.stop();
    }
    int width = Integer.parseInt(this.width.getText());
    int height = Integer.parseInt(this.height.getText());
    colors = new Colors(width, height,
        new Color(colorPicker.getValue()),
        seedLocationChooser.getValue());
    imageView.setImage(colors.getImage());
    progressBar.progressProperty().bind(colors.progressProperty());
    StringBinding percentProgress = Bindings.createStringBinding(
        () -> {
          double progress = colors.getProgress();
          if (progress >= 1) {
            return "Progress: Complete";
          } else {
            return String.format("Progress: %.2f%%", progress * 100);
          }
        },
        colors.progressProperty());
    progressLabel.textProperty().bind(percentProgress);
    timer = Timer.createStarted();
    timeLabel.textProperty().bind(Bindings.createStringBinding(() -> "Elapsed time: " + Util.nanosToTime(timer.getNanos()), colors.progressProperty()));
    progressInfo.setManaged(true);
    saveButton.setManaged(true);
    colors.start();
  }

  @FXML
  private void save() throws IOException {
    if (colors == null) {
      return;
    }
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Colorburst");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
    fileChooser.setInitialFileName("colorburst-" + (int) colors.getImage().getWidth() + "x" + (int) colors.getImage().getHeight() + ".png");
    fileChooser.setInitialDirectory(lastSaveDir);
    File file = fileChooser.showSaveDialog(root.getScene().getWindow());
    if (file != null) {
      BufferedImage bufferedImage = SwingFXUtils.fromFXImage(colors.getImage(), null);
      ImageIO.write(bufferedImage, "png", file);
      lastSaveDir = file.getParentFile();
    }
  }

  @FXML
  private void showContextMenu(ContextMenuEvent event) {
    if (colors == null) {
      return;
    }
    MenuItem save = new MenuItem("Save image");
    save.setOnAction(__ -> {
      try {
        save();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    });
    ContextMenu contextMenu = new ContextMenu(save);
    contextMenu.show(root.getScene().getWindow(), event.getScreenX(), event.getScreenY());
  }

}
