package com.isalimzhanov.diskspaceprofiler.service;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public final class ResourceTraversalTask extends Task<Void> {

    private static final Logger LOGGER = LogManager.getLogger(ResourceTraversalTask.class);

    private final Consumer<Resource> addResourceConsumer;
    private final FileService fileService;

    public ResourceTraversalTask(Consumer<Resource> addResourceConsumer, FileService fileService) {
        this.addResourceConsumer = addResourceConsumer;
        this.fileService = fileService;
    }

    @Override
    protected Void call() {
        LOGGER.info("Started traversal");
        long startTime = System.currentTimeMillis();
        List<Path> rootPaths = fileService.getRootPaths();
        if (rootPaths.size() > 1) {
            addResourceConsumer.accept(Resource.create("", null, 0L));
        }
        rootPaths.forEach(rootPath -> {
                    try {
                        traverse(rootPath);
                    } catch (Exception e) {
                        LOGGER.error("Failed to traverse path: {}", rootPath, e);
                    }
                }
        );
        long endTime = System.currentTimeMillis();
        LOGGER.info("Resource traversal finished successfully, in {} ms", endTime - startTime);
        return null;
    }

    private void traverse(Path path) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        if (fileService.shouldSkip(path)) {
            return;
        }
        try {
            if (fileService.isFile(path)) {
                addResourceConsumer.accept(fileService.buildResource(path, fileService.getFileSize(path)));
                return;
            }
            if (fileService.isDirectory(path)) {
                addResourceConsumer.accept(fileService.buildResource(path, 0L));
                for (Path child : fileService.getNestedPaths(path)) {
                    traverse(child);
                }
                return;
            }
            LOGGER.warn("Path {} is not a file or directory", path);
        } catch (Exception e) {
            LOGGER.error("Failed to traverse path {}", path, e);
        }
    }
}
