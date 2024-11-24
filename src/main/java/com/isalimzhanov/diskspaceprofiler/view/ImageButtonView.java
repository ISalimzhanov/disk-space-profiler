package com.isalimzhanov.diskspaceprofiler.view;

import com.isalimzhanov.diskspaceprofiler.utils.ImageUtils;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public final class ImageButtonView extends Button {

    public ImageButtonView(String imagePath, int width, int height) {
        ImageView imageView = new ImageView(ImageUtils.loadImage(imagePath));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        this.setGraphic(imageView);
    }
}
