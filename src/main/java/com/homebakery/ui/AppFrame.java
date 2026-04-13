package com.homebakery.ui;

import java.awt.Dimension;

/** Matches main dashboard workspace proportions (~1280×800). */
public final class AppFrame {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 800;
    public static final Dimension SIZE = new Dimension(WIDTH, HEIGHT);
    public static final Dimension MIN_SIZE = new Dimension(1100, 680);

    private AppFrame() {
    }
}
