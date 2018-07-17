package com.github.samcarlberg.colorburst;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

/**
 * Does all the heavy lifting.
 */
final class Renderer {

  private Colors colors;
  private boolean isRunning = false;
  private long startTime;

  private final List<Anchor> anchors = new ArrayList<>();

  private int numPixelsWritten = 0;

  public Renderer(Colors colors, Anchor seedAnchor) {
    this.colors = colors;
    anchors.add(seedAnchor);
  }

  public void doRenderPass() {
    if (!anchors.isEmpty() && isRunning) {
      Anchor anchor = getNextAnchor();
      if (anchor == null) {
        stop();
        return;
      }

      List<Pos> availableNeighbors = anchor.getAvailableNeighbors();
      Pos pos = Util.pickRandom(availableNeighbors);
      Color color = colors.pickClosestColor(anchor.getColor());
      colors.markUsed(color);

      Anchor newAnchor = new Anchor(pos, color, colors);
      if (newAnchor.hasAvailableNeighbors() || colors.isOpen(pos)) {
        anchors.add(newAnchor);
      }

      colors.getImage().getPixelWriter().setArgb(pos.x, pos.y, color.argb);
      colors.updateProgress(++numPixelsWritten);
      if (anchor.hasAvailableNeighbors()) {
        // The anchor is still viable, place it back in the list
        anchors.add(anchor);
      }

      if (isRunning && (!colors.getColors().isEmpty() && !anchors.isEmpty())) {
        Platform.runLater(this::doRenderPass);
      } else {
        colors.updateProgress(++numPixelsWritten);
        stop();
      }
    } else {
      colors.updateProgress(++numPixelsWritten);
      stop();
    }
  }

  private Anchor getNextAnchor() {
    Anchor anchor = null;
    boolean canUseAnchor = false;

    do {
      if (anchors.isEmpty()) {
        stop();
        return null;
      }
      anchor = Util.pickRandom(anchors);
      canUseAnchor = anchor.hasAvailableNeighbors();
    } while (!canUseAnchor);
    return anchor;
  }

  /**
   * Starts this renderer.
   */
  public void start() {
    isRunning = true;
    startTime = System.currentTimeMillis();
    doRenderPass();
  }

  /**
   * Stops this renderer.
   */
  public void stop() {
    long time = System.currentTimeMillis() - startTime;
    isRunning = false;
    // Some debug diagnostics
    System.out.println("Stopping");
    System.out.printf("Ran in %.3f minutes%n", time / 60e3);
  }

}
