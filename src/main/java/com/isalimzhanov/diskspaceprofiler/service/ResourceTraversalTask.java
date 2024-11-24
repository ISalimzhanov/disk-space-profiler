package com.isalimzhanov.diskspaceprofiler.service;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import com.isalimzhanov.diskspaceprofiler.utils.FileUtils;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class ResourceTraversalTask extends Task<Void> {

    private static final Logger LOGGER = LogManager.getLogger(ResourceTraversalTask.class);
    private static final int PERCENTAGE_SCALE = 4;
    private static final BigDecimal HUNDRED = new BigDecimal(100);

    private final Path rootPath;
    private final Consumer<Resource> resourceHandler;

    public ResourceTraversalTask(Path rootPath, Consumer<Resource> resourceHandler) {
        this.rootPath = rootPath;
        this.resourceHandler = resourceHandler;
    }

    @Override
    protected Void call() {
        LOGGER.info("Started traversing path: {}", rootPath);
        long startTime = System.currentTimeMillis();
        try {
            long totalSize = FileUtils.getSizeInBytes(rootPath);
            traverse(rootPath, totalSize);
        } catch (Exception e) {
            LOGGER.error("Failed to traverse path: {}", rootPath, e);
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("Resource traversal finished successfully, in {}", endTime - startTime);
        return null;
    }

    private void traverse(Path path, long totalSize) throws IOException {
        if (!FileUtils.doesExists(path)) {
            LOGGER.error("Path {} doesn't exists", path);
            return;
        }
        if (!FileUtils.isDirectory(path) && !FileUtils.isFile(path)) {
            LOGGER.warn("Path {} is not a file or directory", path);
            return;
        }
        Resource resource = createResource(path, totalSize);
        resourceHandler.accept(resource);
        for (Path child : FileUtils.getChildrenPaths(path)) {
            traverse(child, totalSize);
        }
    }

    private Resource createResource(Path path, long totalSize) throws IOException {
        long size = FileUtils.getSizeInBytes(path);
        return new Resource(
                FileUtils.getName(path),
                size,
                getSpaceUsagePercentage(size, totalSize),
                path
        );
    }

    private BigDecimal getSpaceUsagePercentage(long size, long totalSize) {
        return new BigDecimal(size).multiply(HUNDRED).divide(new BigDecimal(totalSize), PERCENTAGE_SCALE, RoundingMode.DOWN);
    }
}
