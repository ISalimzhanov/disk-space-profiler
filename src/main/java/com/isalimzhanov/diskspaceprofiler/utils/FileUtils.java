package com.isalimzhanov.diskspaceprofiler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private static final Logger LOGGER = LogManager.getLogger(FileUtils.class);

    private FileUtils() {
    }

    public static boolean doesExists(Path path) {
        return Files.exists(path) || Files.isSymbolicLink(path);
    }

    public static boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    public static boolean isFile(Path path) {
        return Files.isRegularFile(path);
    }

    public static long getSizeInBytes(Path path) throws IOException {
        return Files.size(path);
    }

    public static String getName(Path path) {
        if (path.getFileName() == null) {
            return path.toString();
        }
        return path.getFileName().toString();
    }

    public static List<Path> getChildrenPaths(Path path) throws IOException {
        List<Path> children = new ArrayList<>();
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    children.add(entry);
                }
            } catch (AccessDeniedException e) {
                LOGGER.warn("Access denied to directory: {}", path, e);
            }
        }
        return children;
    }
}
