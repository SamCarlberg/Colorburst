package com.github.samcarlberg.colorburst;

public final class Color {

  /**
   * The red component of this color, in the range [0, 255].
   */
  public final int r;
  /**
   * The green component of this color, in the range [0, 255].
   */
  public final int g;
  /**
   * The blue component of this color, in the range [0, 255].
   */
  public final int b;

  /**
   * The 32-bit ARGB representation of this color. The alpha channel is always 255.
   */
  public final int argb;

  @SuppressWarnings({"NumericOverflow", "PointlessBitwiseExpression"})
  public Color(int r, int g, int b) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.argb = 0xFF << 24
        | r << 16
        | g << 8
        | b << 0;
  }

  public Color(javafx.scene.paint.Color value) {
    this((int) value.getRed() * 255, (int) value.getGreen() * 255, (int) (value.getBlue() * 255));
  }

  /**
   * Calculates the geometric distance between this color and another one.
   *
   * @param other the color to compute the distance to
   *
   * @return the distance between these two colors
   */
  public double distanceFrom(Color other) {
    return Math.sqrt(
        Math.pow((this.r - other.r), 2)
            + Math.pow(this.g - other.g, 2)
            + Math.pow(this.b - other.b, 2)
    );
  }
}
