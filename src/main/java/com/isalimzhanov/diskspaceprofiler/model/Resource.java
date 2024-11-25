package com.isalimzhanov.diskspaceprofiler.model;

import com.isalimzhanov.diskspaceprofiler.util.ResourceUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.file.Path;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class Resource implements Displayable {

    private final String name;
    private final Long size;
    private final Path path;

    public Resource(String name, Long size, Path path) {
        this.name = name;
        this.size = size;
        this.path = path;
        ResourceUtils.validate(this);
    }

    public Long getSize() {
        return size;
    }

    public Path getPath() {
        return path;
    }

    public String getDisplayName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(JSON_STYLE)
                .append("name", name)
                .append("size", size)
                .append("path", path)
                .build();
    }
}
