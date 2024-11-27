package com.isalimzhanov.diskspaceprofiler.service;

import com.isalimzhanov.diskspaceprofiler.exception.WatchingDirectoryFailedException;
import com.isalimzhanov.diskspaceprofiler.util.ExecutorServiceUtils;
import io.methvin.watcher.DirectoryChangeListener;
import io.methvin.watcher.DirectoryWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class DirectoryWatchingService {

    private static final Logger LOGGER = LogManager.getLogger(DirectoryWatchingService.class);

    private final FileService fileService;

    public DirectoryWatchingService(FileService fileService) {
        this.fileService = fileService;
    }

    public void watchRootPaths(DirectoryChangeListener listener) {
        fileService.getRootPaths().forEach(path -> {
            LOGGER.info("Watching directory: {}", path);
            try {
                DirectoryWatcher watcher = DirectoryWatcher.builder()
                        .path(path)
                        .listener(listener)
                        .fileHashing(false)
                        .build();
                ExecutorServiceUtils.submitWatcher(watcher);
                LOGGER.info("Started watching directory: {}", path);
            } catch (IOException e) {
                LOGGER.error("Failed to watch directory: {}", path, e);
                throw new WatchingDirectoryFailedException(path, e);
            }
        });
    }
}
