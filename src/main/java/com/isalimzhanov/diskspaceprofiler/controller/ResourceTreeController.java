package com.isalimzhanov.diskspaceprofiler.controller;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import com.isalimzhanov.diskspaceprofiler.service.ResourceTreeService;
import com.isalimzhanov.diskspaceprofiler.view.ResourceTreeView;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;
import java.util.LinkedList;

public final class ResourceTreeController {

    private final ResourceTreeView view;
    private final ResourceTreeService resourceTreeService;

    public ResourceTreeController(ResourceTreeView view) {
        this.resourceTreeService = new ResourceTreeService(new LinkedList<>(), new HashMap<>());
        this.view = view;
        view.addTreeEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                resourceTreeService.selectNewRoot(view);
            }
        });
        view.setGoBackAction(event -> resourceTreeService.restorePreviousRoot(view));
        view.setOpenParentAction(event -> resourceTreeService.openRootParent(view));
    }

    public void addResource(Resource resource) {
        resourceTreeService.addResourceToTree(resource, view);
    }
}
