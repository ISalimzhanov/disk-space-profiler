package com.isalimzhanov.diskspaceprofiler.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.nio.file.Path;


public class Resource implements Displayable {

    @NotBlank
    private final String name;

    @Positive
    private final long size;

    @NotNull
    @Min(0)
    private final BigDecimal memoryUsagePercentage;

    @NotNull
    private final Path path;

    public Resource(String name, long size, BigDecimal memoryUsagePercentage, Path path) {
        this.name = name;
        this.size = size;
        this.memoryUsagePercentage = memoryUsagePercentage;
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public BigDecimal getSpaceUsagePercentage() {
        return memoryUsagePercentage;
    }

    public Path getPath() {
        return path;
    }

    public String getDisplayName() {
        return name;
    }
}
