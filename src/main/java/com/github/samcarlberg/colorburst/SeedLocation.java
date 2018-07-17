package com.github.samcarlberg.colorburst;

/**
 * Enumeration of the possible seed locations.
 */
public enum SeedLocation {

  TOP_LEFT("Top left"),
  TOP_CENTER("Top center"),
  TOP_RIGHT("Top right"),
  CENTER_LEFT("Center left"),
  CENTER("Center"),
  CENTER_RIGHT("Center right"),
  BOTTOM_LEFT("Bottom left"),
  BOTTOM_CENTER("Bottom center"),
  BOTTOM_RIGHT("Bottom right");

  private final String simpleName;

  SeedLocation(String simpleName) {
    this.simpleName = simpleName;
  }

  @Override
  public String toString() {
    return simpleName;
  }
}
