package com.isalimzhanov.diskspaceprofiler.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.file.Path;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class Resource {

    private final LongProperty sizeProperty;
    private final StringProperty displayName;
    private final Path path;

    private Resource(Path path) {
        this.sizeProperty = new SimpleLongProperty();
        this.displayName = new SimpleStringProperty();
        this.path = path;
    }

    public synchronized void setSize(long sizeProperty) {
        if (sizeProperty < 0) {
            throw new IllegalArgumentException("Resource's size must not be negative");
        }
        this.sizeProperty.set(sizeProperty);
    }

    public synchronized void addSize(long size) {
        if (this.sizeProperty.get() + size < 0) {
            throw new IllegalArgumentException("Resource's size must not be negative after addition");
        }
        this.sizeProperty.set(this.sizeProperty.get() + size);
    }

    public synchronized long getSize() {
        return sizeProperty.get();
    }

    public Path getPath() {
        return path;
    }

    public synchronized void setDisplayName(String displayName) {
        if (StringUtils.isBlank(displayName)) {
            throw new IllegalArgumentException("Resource's name must not be blank");
        }
        this.displayName.set(displayName);
    }

    public synchronized String getDisplayName() {
        return displayName.get();
    }

    public StringProperty getDisplayNameProperty() {
        return displayName;
    }

    public LongProperty getSizeProperty() {
        return sizeProperty;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(JSON_STYLE)
                .append("name", displayName)
                .append("size", sizeProperty)
                .append("path", path)
                .build();
    }

    public static Resource create(String displayName, Path path, long size) {
        Resource resource = new Resource(path);
        resource.setDisplayName(displayName);
        resource.setSize(size);
        return resource;
    }
}
