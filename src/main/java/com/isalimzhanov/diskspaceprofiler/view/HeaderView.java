package com.isalimzhanov.diskspaceprofiler.view;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public final class HeaderView extends VBox {

    private final Text text;

    public HeaderView() {
        this.text = new Text("Disk Space Profiler");
        text.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        getChildren().add(text);
    }
}
