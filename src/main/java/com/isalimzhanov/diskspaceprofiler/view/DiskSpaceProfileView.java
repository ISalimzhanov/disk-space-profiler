package com.isalimzhanov.diskspaceprofiler.view;

import javafx.scene.layout.VBox;

public final class DiskSpaceProfileView extends VBox {

    private final ResourceTreeView resourceTree;
    private final HeaderView header;

    public DiskSpaceProfileView() {
        this.header = new HeaderView();
        this.resourceTree = new ResourceTreeView();

        getChildren().add(header);
        getChildren().add(resourceTree);
    }

    public ResourceTreeView getResourceTree() {
        return resourceTree;
    }
}
