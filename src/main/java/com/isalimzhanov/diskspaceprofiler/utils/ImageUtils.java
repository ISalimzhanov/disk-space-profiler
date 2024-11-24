package com.isalimzhanov.diskspaceprofiler.utils;

import javafx.scene.image.Image;

import java.io.InputStream;

public class ImageUtils {

    public static final String BACK_ARROW_PATH = "/images/back-arrow.png";
    public static final String OPEN_PARENT_PATH = "/images/open-parent.png";

    private ImageUtils() {
    }

    public static Image loadImage(String imagePath) {
        return new Image(getImageStream(imagePath));
    }

    private static InputStream getImageStream(String imagePath) {
        InputStream stream = ImageUtils.class.getResourceAsStream(imagePath);
        if (stream == null) {
            throw new IllegalArgumentException("Image not found: " + imagePath);
        }
        return stream;
    }
}
