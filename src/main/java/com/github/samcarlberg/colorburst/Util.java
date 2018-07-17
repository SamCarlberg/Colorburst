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

}
