package com.homebakery.ui;

import java.text.NumberFormat;
import java.util.Locale;

public final class BakeryFormat {
    private static final Locale PH = new Locale("en", "PH");

    private BakeryFormat() {
    }

    public static NumberFormat peso() {
        return NumberFormat.getCurrencyInstance(PH);
    }
}
