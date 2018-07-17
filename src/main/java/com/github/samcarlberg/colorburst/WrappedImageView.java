package com.github.samcarlberg.colorburst;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * An image view that resizes itself to fit its container.
 */
public final class WrappedImageView extends ImageView {

  public WrappedImageView() {
    setPreserveRatio(false);
  }

  @Override
  public double minWidth(double height) {
    return 40;
  }

  @Override
  public double maxWidth(double height) {
    return 16384;
  }

  @Override
  public double minHeight(double width) {
    return 40;
  }

  @Override
  public double maxHeight(double width) {
    return 16384;
  }

  @Override
  public double prefWidth(double height) {
    Image im = getImage();
    if (im == null) {
      return minWidth(height);
    }
    return im.getWidth();
  }

  @Override
  public double prefHeight(double width) {
    Image image = getImage();
    if (image == null) {
      return minHeight(width);
    }
    return image.getHeight();
  }


  @Override
  public boolean isResizable() {
    return true;
  }

  @Override
  public void resize(double width, double height) {
    setFitWidth(width);
    setFitHeight(height);
  }
}