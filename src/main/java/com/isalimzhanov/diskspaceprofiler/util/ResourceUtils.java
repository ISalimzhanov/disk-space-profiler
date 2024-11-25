package com.isalimzhanov.diskspaceprofiler.util;

import com.isalimzhanov.diskspaceprofiler.exception.ValidationException;
import com.isalimzhanov.diskspaceprofiler.model.Resource;
import org.apache.commons.lang3.StringUtils;

public class ResourceUtils {

    private ResourceUtils() {
    }

    public static void validate(Resource resource) {
        StringBuilder builder = new StringBuilder("Invalid resource " + resource + "\n");
        boolean invalid = false;
        if (StringUtils.isBlank(resource.getDisplayName())) {
            invalid = true;
            builder.append("Resource's name must not be blank\n");
        }
        if (resource.getSize() != null && resource.getSize() < 0) {
            invalid = true;
            builder.append("Resource's size must not be negative\n");
        }
        if (resource.getPath() == null) {
            invalid = true;
            builder.append("Resource's path must not be null\n");
        }
        if (invalid) {
            throw new ValidationException(builder.toString());
        }
    }
}
