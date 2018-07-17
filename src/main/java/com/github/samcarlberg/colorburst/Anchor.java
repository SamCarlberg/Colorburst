package com.github.samcarlberg.colorburst;

import java.util.ArrayList;
import java.util.List;

/**
 * A point on the image that has a color and position, and can check its neighboring pixels .
 */
public final class Anchor {

  private final Pos pos;
  private final Color color;
  private final Colors colors;
  private final int imageWidth;
  private final int imageHeight;

  public Anchor(Pos pos, Color color, Colors colors) {
    this.pos = pos;
    this.color = color;
    this.colors = colors;
    this.imageWidth = (int) colors.getImage().getWidth();
    this.imageHeight = (int) colors.getImage().getHeight();
  }

  /**
   * Gets the color of the pixel at this anchor.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Gets the positions of all the immediately adjacent neighboring pixels in a 3x3 grid centered on this anchor that
   * have not yet been colored.
   */
  public List<Pos> getAvailableNeighbors() {
    int minX = Math.max(0, pos.x - 1);
    int maxX = Math.min(imageWidth - 1, pos.x + 1);
    int minY = Math.max(0, pos.y - 1);
    int maxY = Math.min(imageHeight - 1, pos.y + 1);

    List<Pos> availablePositions = new ArrayList<>();
    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        if (x == pos.x && y == pos.y) {
          continue;
        }
        if (colors.isOpen(x, y)) {
          availablePositions.add(new Pos(x, y));
        }
      }
    }
    return availablePositions;
  }

  /**
   * Checks if there are any neighboring pixels that have not yet been colored.
   */
  public boolean hasAvailableNeighbors() {
    int minX = Math.max(0, pos.x - 1);
    int maxX = Math.min(imageWidth - 1, pos.x + 1);
    int minY = Math.max(0, pos.y - 1);
    int maxY = Math.min(imageHeight - 1, pos.y + 1);

    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        if (x == pos.x && y == pos.y) {
          continue;
        }
        if (colors.isOpen(x, y)) {
          return true;
        }
      }
    }
    return false;
  }

}
