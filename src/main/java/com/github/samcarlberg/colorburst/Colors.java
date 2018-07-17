package com.github.samcarlberg.colorburst;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.WritableImage;

public final class Colors {

  private static final double LOG_2 = Math.log(2);

  private final Color seedColor;
  private final SeedLocation seedLocation;
  private final double colorDepth;
  private final double colorSkip;

  private final int width;
  private final int height;

  private final Collection<Color> colors = new HashSet<>();

  private Color[][][] colorSpace;
  private Collection<Color> usedColors = new HashSet<>();

  private final DoubleProperty progress = new SimpleDoubleProperty(this, "progress", 0);

  private WritableImage image;
  private Renderer renderer;
  private final int size;

  public Colors(int width, int height, Color seedColor, SeedLocation seedLocation) {
    this.size = width * height;
    if (size > 16_777_216) {
      throw new IllegalArgumentException(
          "Image is too large! Maximum number of pixels is 16,777,216, but the image is " + size + " pixels");
    }
    this.width = width;
    this.height = height;
    this.image = new WritableImage(width, height);
    this.colorDepth = Math.pow(2, (log2(width) + log2(height)) / 3);
    this.colorSkip = 256 / colorDepth;
    System.out.println("Color depth: " + colorDepth);
    System.out.println("Color skip: " + colorSkip);
    this.seedColor = seedColor;
    this.seedLocation = seedLocation;
  }

  public ReadOnlyDoubleProperty progressProperty() {
    return progress;
  }

  public double getProgress() {
    return progress.get();
  }

  public void updateProgress(int numPixelsWritten) {
    progress.setValue(((double) numPixelsWritten) / (size));
  }

  private static double log2(double d) {
    return Math.log(d) / LOG_2;
  }

  public WritableImage getImage() {
    return image;
  }

  public Collection<Color> getColors() {
    return colors;
  }

  public void start() {
    // Generate colors
    // Note: we occasionally generate more colors than there are pixels, and some colors therefore go unused

    int dim = (int) colorDepth;
    if (dim < colorDepth) {
      dim++;
    }
    colorSpace = new Color[dim][dim][dim];

    for (int i = 0; i < colorDepth; i++) {
      for (int j = 0; j < colorDepth; j++) {
        for (int k = 0; k < colorDepth; k++) {
          Color color = new Color((int) (colorSkip * i), (int) (colorSkip * j), (int) (colorSkip * k));
          colors.add(color);
          colorSpace[i][j][k] = color;
        }
      }
    }

    System.out.println("Generated " + colors.size() + " colors");

    Pos anchorPos = getAnchorPos();
    Color anchorColor = pickClosestColor(seedColor);
    markUsed(anchorColor);

    renderer = new Renderer(this, new Anchor(anchorPos, anchorColor, this));
    renderer.start();
  }

  public void stop() {
    renderer.stop();
  }

  private Pos getAnchorPos() {
    switch (seedLocation) {
      case TOP_LEFT:
        return new Pos(0, 0);
      case TOP_CENTER:
        return new Pos(width / 2, 0);
      case TOP_RIGHT:
        return new Pos(width - 1, 0);
      case CENTER_LEFT:
        return new Pos(0, height / 2);
      case CENTER:
        return new Pos(width / 2, height / 2);
      case CENTER_RIGHT:
        return new Pos(width - 1, height / 2);
      case BOTTOM_LEFT:
        return new Pos(0, height - 1);
      case BOTTOM_CENTER:
        return new Pos(width / 2, height - 1);
      case BOTTOM_RIGHT:
        return new Pos(width - 1, height - 1);
      default:
        throw new IllegalArgumentException("Unknown seed position: " + seedLocation);
    }
  }

  /**
   * Checks if a pixel in the image is not yet colored.
   *
   * @param x the X-coordinate of the pixel to check
   * @param y the Y-coordinate of the pixel to check
   *
   * @return true if the pixel is not colored, false if it has a color
   */
  public boolean isOpen(int x, int y) {
    return (image.getPixelReader().getArgb(x, y) | 0xFF0000) == 0xFF0000;
  }

  /**
   * Checks if a pixel in the image is not yet colored.
   *
   * @param pos the position of the pixel to check
   *
   * @return true if the pixel is not colored, false if it has a color
   */
  public boolean isOpen(Pos pos) {
    return isOpen(pos.x, pos.y);
  }

  /**
   * Flags a color as used. It will no longer be able to be drawn on the image.
   *
   * @param color the color to mark
   */
  void markUsed(Color color) {
    colors.remove(color);
    usedColors.add(color);
  }

  public Color pickClosestColor(Color color) {
    return fastSearch(color)
        .orElseGet(() -> slowSearch(color));
  }

  /**
   * Fast search. Looks in a small cube centered about the input color, rather than search the entire state space.
   *
   * @param color the color to find the closest match for
   *
   * @return an optional containing the closest unused color, or an empty optional if no such color was found within the cube
   */
  private Optional<Color> fastSearch(Color color) {
    // Rough coordinates for the color in the state space
    int dim = colorSpace.length;
    int r = (int) (color.r / colorSkip) + 1;
    int g = (int) (color.g / colorSkip) + 1;
    int b = (int) (color.b / colorSkip) + 1;

    // Larger cube sizes drastically reduce the number of misses, but takes longer.
    // Scale the upper bound so that larger state spaces have higher coverage

    // TODO streamify?
    for (int i = 1; i < log2(Math.sqrt(size)) + 1; i++) {
      int max = (2 * i) + 1;

      RangeHelper helper = value -> Math.max(0, Math.min(value - max / 2, dim - 1));
      Set<Color> options = new HashSet<>();

      // Top and bottom planes
      for (int x = 0; x < max; x++) {
        for (int y = 0; y < max; y++) {
          Color[] plane = colorSpace[helper.clamp(r + x)][helper.clamp(g + y)];
          options.add(plane[helper.clamp(b)]);
          options.add(plane[helper.clamp(b + max - 1)]);
        }
      }

      // Intermediate planes. Ignore the internals of the cube since prior iterations will have covered those, or in the
      // initial case of max=3, the center value is the input color and we don't want to return it anyway
      for (int z = 1; z < max - 1; z++) {
        for (int x = 0; x < max; x++) {
          // Two edges
          options.add(colorSpace[helper.clamp(r + x)][helper.clamp(g)][helper.clamp(b + z)]);
          options.add(colorSpace[helper.clamp(r + x)][helper.clamp(g + max - 1)][helper.clamp(b + z)]);
        }
        // Other edges - start and end one position in to avoid duplicating the corners
        for (int y = 1; y < max - 1; y++) {
          options.add(colorSpace[helper.clamp(r)][helper.clamp(g + y)][helper.clamp(b + z)]);
          options.add(colorSpace[helper.clamp(r + max - 1)][helper.clamp(g + y)][helper.clamp(b + z)]);
        }
      }
      Optional<Color> closest = options.stream()
          .parallel()
          .filter(c -> !usedColors.contains(c))
          .min(Comparator.comparingDouble(color::distanceFrom));
      if (closest.isPresent()) {
        return closest;
      }
    }
    return Optional.empty();
  }

  /**
   * Slow search - exhaustively search over all available colors to find the best match
   * Typically happens about 0.1% to 1% of the time - the fast search typically gets everything*
   * This is the original algorithm used by theomg's omnichroma
   */
  private Color slowSearch(Color color) {
    return colors.stream()
        .parallel()
        .min(Comparator.comparingDouble(color::distanceFrom))
        .get();
  }

}
