package com.isalimzhanov.diskspaceprofiler.exception;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.file.Path;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class WatchingDirectoryFailedException extends RuntimeException {

    private final Path directoryPath;

    public WatchingDirectoryFailedException(Path directoryPath, Throwable cause) {
        super(cause);
        this.directoryPath = directoryPath;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(JSON_STYLE)
                .appendSuper(super.toString())
                .append("directoryPath", directoryPath)
                .build();
    }
}
