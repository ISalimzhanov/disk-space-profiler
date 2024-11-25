package com.isalimzhanov.diskspaceprofiler.service;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import io.methvin.watcher.DirectoryChangeListener;
import io.methvin.watcher.DirectoryWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class FileService {

    private static final Logger LOGGER = LogManager.getLogger(FileService.class);

    public boolean doesExist(Path path) {
        return Files.exists(path) || Files.isSymbolicLink(path);
    }

    public boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    public boolean isFile(Path path) {
        return Files.isRegularFile(path);
    }

    public long getFileSize(Path filePath) throws IOException {
        if (!isFile(filePath)) {
            throw new IllegalArgumentException("Path is not a file");
        }
        return Files.size(filePath);
    }

    public String getName(Path path) {
        if (path.getFileName() == null) {
            return path.toString();
        }
        return path.getFileName().toString();
    }

    public List<Path> getNestedPaths(Path path) throws IOException {
        List<Path> nested = new ArrayList<>();
        try {
            if (Files.isDirectory(path)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    for (Path entry : stream) {
                        nested.add(entry);
                    }
                }
            }
            return nested;
        } catch (AccessDeniedException e) {
            LOGGER.warn("Failed to get nested paths of {}, due to the lack of access rights", path);
            return nested;
        }
    }

    public void watchDirectory(Path path, DirectoryChangeListener listener) throws IOException {
        LOGGER.info("Watching directory: {}", path);
        try {
            DirectoryWatcher watcher = DirectoryWatcher.builder()
                    .path(path)
                    .listener(listener)
                    .build();
            watcher.watchAsync();
        } catch (IOException e) {
            LOGGER.error("Failed to watch directory: {}", path, e);
            throw e;
        }
    }


    public Resource buildResource(Path path, Long size) {
        return new Resource(
                getName(path),
                size,
                path
        );
    }
}
