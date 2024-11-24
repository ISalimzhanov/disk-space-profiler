package com.isalimzhanov.diskspaceprofiler.controller;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import com.isalimzhanov.diskspaceprofiler.view.DiskSpaceProfileView;

import java.util.function.Consumer;

public final class DiskSpaceProfilerController {

    private final ResourceTreeController resourceTreeController;

    private final Consumer<Resource> resourceHandler;

    public DiskSpaceProfilerController(DiskSpaceProfileView view) {
        this.resourceTreeController = new ResourceTreeController(view.getResourceTree());
        resourceHandler = resourceTreeController::addResource;
    }

    public Consumer<Resource> getResourceHandler() {
        return resourceHandler;
    }
}
