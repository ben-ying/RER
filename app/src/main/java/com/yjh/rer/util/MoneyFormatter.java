package com.yjh.rer.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public final class MoneyFormatter {
    private static final ThreadLocal<DecimalFormat> FORMATTER = ThreadLocal.withInitial(() -> {
        DecimalFormat format = new DecimalFormat("#,##0.00");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format;
    });

    private MoneyFormatter() {
    }

    public static String format(double value) {
        return FORMATTER.get().format(value);
    }
}
