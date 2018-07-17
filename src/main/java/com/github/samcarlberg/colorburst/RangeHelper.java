package com.github.samcarlberg.colorburst;

@FunctionalInterface
public interface RangeHelper {

  /**
   * Clamps a value to a range.
   *
   * @param value the value to clamp
   *
   * @return the clamped value
   */
  int clamp(int value);
}
