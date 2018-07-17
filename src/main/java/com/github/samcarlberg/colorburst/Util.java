package com.github.samcarlberg.colorburst;

import java.util.Collection;

public final class Util {

  /**
   * Picks a random element from a collection and removes it.
   *
   * @param values the list to pick from
   * @param <E>    the type of values in the list
   *
   * @return the randomly selected element
   */
  public static <E> E pickRandom(Collection<? extends E> values) {
    int i = (int) (Math.random() * values.size());
    E element = values.stream()
        .skip(i)
        .findFirst()
        .get();
    values.remove(element);
    return element;
  }

}
