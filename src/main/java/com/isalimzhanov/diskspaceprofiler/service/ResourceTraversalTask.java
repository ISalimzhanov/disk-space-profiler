package com.isalimzhanov.diskspaceprofiler.service;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class ResourceTraversalTask extends Task<Void> {

    private static final Logger LOGGER = LogManager.getLogger(ResourceTraversalTask.class);

    private final Path rootPath;
    private final Consumer<Resource> addResourceConsumer, updateResourceConsumer;
    private final FileService fileService;

    public ResourceTraversalTask(
            Path rootPath, Consumer<Resource> addResourceConsumer,
            Consumer<Resource> updateResourceConsumer, FileService fileService
    ) {
        this.rootPath = rootPath;
        this.addResourceConsumer = addResourceConsumer;
        this.updateResourceConsumer = updateResourceConsumer;
        this.fileService = fileService;
    }

    @Override
    protected Void call() {
        LOGGER.info("Started traversing path: {}", rootPath);
        long startTime = System.currentTimeMillis();
        try {
            traverse(rootPath);
        } catch (Exception e) {
            LOGGER.error("Failed to traverse path: {}", rootPath, e);
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("Resource traversal finished successfully, in {}", endTime - startTime);
        return null;
    }

    private long traverse(Path path) {
        if (!fileService.doesExist(path)) {
            LOGGER.error("Path {} doesn't exists", path);
            return 0L;
        }
        try {
            addResourceConsumer.accept(fileService.buildResource(path, null));
            long size = calculateSize(path);
            updateResourceConsumer.accept(fileService.buildResource(path, size));
            return size;
        } catch (Exception e) {
            LOGGER.error("Failed to traverse path {}", path, e);
        }
        return 0L;
    }

    private long calculateSize(Path path) throws IOException {
        if (fileService.isFile(path)) {
            return fileService.getFileSize(path);
        }
        if (fileService.isDirectory(path)) {
            return calculateSizeOfDirectory(path);
        }
        LOGGER.warn("Path {} is not a file or directory", path);
        return 0L;
    }

    private long calculateSizeOfDirectory(Path path) throws IOException {
        long size = 0;
        for (Path child : fileService.getNestedPaths(path)) {
            size += traverse(child);
        }
        return size;
    }
}
