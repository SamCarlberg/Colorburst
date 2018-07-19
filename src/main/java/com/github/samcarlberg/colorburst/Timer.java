package com.github.samcarlberg.colorburst;

/**
 * Times how long something takes.
 */
public class Timer {

  private long start = -1;

  public static Timer createStarted() {
    Timer timer = new Timer();
    timer.start();
    return timer;
  }

  public static Timer createStopped() {
    return new Timer();
  }

  public void start() {
    start = System.nanoTime();
  }

  public void stop() {
    start = -1;
  }

  public long getNanos() {
    checkRunning();
    return System.nanoTime() - start;
  }

  private void checkRunning() {
    if (start < 0) {
      throw new IllegalStateException("Timer is not running!");
    }
  }

}
