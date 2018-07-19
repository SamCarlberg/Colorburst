package com.github.samcarlberg.colorburst;

import java.util.List;

public final class Util {

  /**
   * Picks a random element from a list and removes it.
   *
   * @param values the list to pick from
   * @param <E>    the type of values in the list
   *
   * @return the randomly selected element
   */
  public static <E> E pickRandom(List<? extends E> values) {
    int i = (int) (Math.random() * values.size());
    E element = values.get(i);
    values.remove(element);
    return element;
  }

  public static String nanosToTime(long nanos) {
    long ms = nanos / 1_000_000;
    long seconds = ms / 1_000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    ms %= 1000;
    seconds %= 60;
    minutes %= 60;
    hours %= 60;
    if (hours > 0) {
      return String.format("%d hour%s %d minute%s %d second%s", hours, plural(hours), minutes, plural(minutes), seconds, plural(seconds));
    } else if (minutes > 0) {
      return String.format("%d minute%s %d second%s", minutes, plural(minutes), seconds, plural(seconds));
    } else {
      return String.format("%d second%s", seconds, plural(seconds));
    }
  }

  private static String plural(long val) {
    return val == 1 ? "" : "s";
  }

}
