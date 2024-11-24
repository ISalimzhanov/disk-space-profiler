package com.isalimzhanov.diskspaceprofiler.service;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import com.isalimzhanov.diskspaceprofiler.view.ResourceTreeView;
import javafx.scene.control.TreeItem;

import java.nio.file.Path;
import java.util.Deque;
import java.util.Map;

public final class ResourceTreeService {

    private final Deque<TreeItem<Resource>> previousRoots;
    private final Map<Path, TreeItem<Resource>> resourceMap;

    public ResourceTreeService(Deque<TreeItem<Resource>> previousRoots, Map<Path, TreeItem<Resource>> resourceMap) {
        this.previousRoots = previousRoots;
        this.resourceMap = resourceMap;
    }

    public void restorePreviousRoot(ResourceTreeView view) {
        if (!previousRoots.isEmpty()) {
            view.setRoot(previousRoots.pop());
        }
    }

    public void selectNewRoot(ResourceTreeView view) {
        previousRoots.push(view.getRoot());
        view.setSelectedItemToRoot();
    }

    public void addResourceToTree(Resource resource, ResourceTreeView view) {
        TreeItem<Resource> item = createTreeItem(resource);
        if (resourceMap.containsKey(resource.getPath().getParent())) {
            view.addTreeItem(item, resourceMap.get(resource.getPath().getParent()));
        } else {
            addItemToRoot(item, view);
        }
        resourceMap.put(resource.getPath(), item);
    }

    private void addItemToRoot(TreeItem<Resource> item, ResourceTreeView view) {
        if (view.getRoot() == null) {
            view.setRoot(item);
        } else {
            view.addTreeItem(item, view.getRoot());
        }
    }

    private TreeItem<Resource> createTreeItem(Resource resource) {
        return new TreeItem<>(resource);
    }

    public void openRootParent(ResourceTreeView view) {
        if (view.getRoot() == null) {
            throw new IllegalStateException("Root is null");
        }
        previousRoots.push(view.getRoot());
        if (view.getRoot().getParent() != null) {
            view.setRoot(view.getRoot().getParent());
        }
    }
}
