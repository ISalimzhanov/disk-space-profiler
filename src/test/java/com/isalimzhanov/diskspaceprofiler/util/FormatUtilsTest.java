package com.isalimzhanov.diskspaceprofiler.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FormatUtilsTest {

    @ParameterizedTest
    @MethodSource("formatSizeTestCases")
    void formatSizeTest(long size, String expected) {
        assertEquals(expected, FormatUtils.formatSize(size));
    }

    private static Stream<Arguments> formatSizeTestCases() {
        return Stream.of(
                Arguments.of(0, "0.0 B"),
                Arguments.of(1, "1.0 B"),
                Arguments.of(1023, "1023.0 B"),
                Arguments.of(1024, "1.0 KB"),
                Arguments.of(1536, "1.5 KB"),
                Arguments.of(1024 * 1024, "1.0 MB"),
                Arguments.of(1024L * 1024 * 1024, "1.0 GB"),
                Arguments.of(1024L * 1024 * 1024 * 1024, "1.0 TB")
        );
    }

    @Test
    void formatSizeNegativeSizeTest() {
        assertThrows(IllegalArgumentException.class, () -> FormatUtils.formatSize(-1));
    }
}
