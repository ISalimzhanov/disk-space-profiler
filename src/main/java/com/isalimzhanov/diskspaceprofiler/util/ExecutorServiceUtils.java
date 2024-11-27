package com.isalimzhanov.diskspaceprofiler.util;

import io.methvin.watcher.DirectoryWatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceUtils {

    private static final int MAX_THREAD_NUMBER = 10;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);
    private static final List<DirectoryWatcher> WATCHERS = new ArrayList<>();

    private ExecutorServiceUtils() {
    }

    public static void submitDaemon(Runnable task) {
        executorService.submit(task);
    }

    public static void shutdown() {
        WATCHERS.forEach(watcher -> {
            try {
                watcher.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        executorService.shutdown();
    }

    public static void submitWatcher(DirectoryWatcher watcher) {
        WATCHERS.add(watcher);
    }
}
