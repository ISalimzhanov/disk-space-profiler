package com.isalimzhanov.diskspaceprofiler.util;

public class FormatUtils {

    private static final String[] SIZE_UNITS = {"B", "KB", "MB", "GB", "TB"};
    private static final int SIZE_UNIT_MULTIPLIER = 1024;

    private FormatUtils() {
    }

    public static String formatSize(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size must not be negative");
        }
        int unitIndex = 0;
        double displaySize = size;
        while (displaySize >= SIZE_UNIT_MULTIPLIER && unitIndex < SIZE_UNITS.length - 1) {
            displaySize /= SIZE_UNIT_MULTIPLIER;
            unitIndex++;
        }
        return String.format("%.1f %s", displaySize, SIZE_UNITS[unitIndex]);
    }
}
