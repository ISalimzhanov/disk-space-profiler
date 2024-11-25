package com.isalimzhanov.diskspaceprofiler.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceUtils {

    private static final int MAX_THREAD_NUMBER = 10;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);

    private ExecutorServiceUtils() {
    }

    public static void submit(Runnable task) {
        executorService.submit(task);
    }

    public static void shutdown() {
        executorService.shutdown();
    }
}
