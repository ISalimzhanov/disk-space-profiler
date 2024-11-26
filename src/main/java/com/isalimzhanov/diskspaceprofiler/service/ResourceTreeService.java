package com.isalimzhanov.diskspaceprofiler.service;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import java.nio.file.Path;
import java.util.Deque;
import java.util.concurrent.ConcurrentMap;

public final class ResourceTreeService {

    private final Deque<TreeItem<Resource>> previousRoots;
    private final ConcurrentMap<Path, TreeItem<Resource>> resourceMap;

    public ResourceTreeService(
            Deque<TreeItem<Resource>> previousRoots, ConcurrentMap<Path, TreeItem<Resource>> resourceMap
    ) {
        this.previousRoots = previousRoots;
        this.resourceMap = resourceMap;
    }

    public void restorePreviousRoot(TreeTableView<Resource> treeTable) {
        if (!previousRoots.isEmpty()) {
            treeTable.setRoot(previousRoots.pop());
        }
    }

    public void openRootParent(TreeTableView<Resource> treeTable) {
        if (treeTable.getRoot() == null) {
            throw new IllegalStateException("Root is null");
        }
        previousRoots.push(treeTable.getRoot());
        if (treeTable.getRoot().getParent() != null) {
            treeTable.setRoot(treeTable.getRoot().getParent());
        }
    }

    public void selectNewRoot(TreeTableView<Resource> treeTable) {
        previousRoots.push(treeTable.getRoot());
        TreeItem<Resource> selectedItem = treeTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !selectedItem.isLeaf()) {
            treeTable.setRoot(selectedItem);
        }
    }

    public void addOrUpdateResource(Resource resource, TreeTableView<Resource> treeTable) {
        if (resourceMap.containsKey(resource.getPath())) {
            updateResource(resource);
        } else {
            addResource(resource, treeTable);
        }
    }

    public void addResource(Resource resource, TreeTableView<Resource> treeTable) {
        TreeItem<Resource> item = createTreeItem(resource);
        if (treeTable.getRoot() == null) {
            treeTable.setRoot(item);
        } else if (!resourceMap.containsKey(resource.getPath().getParent())) {
            treeTable.getRoot().getChildren().add(item);
        } else {
            TreeItem<Resource> parent = resourceMap.get(resource.getPath().getParent());
            parent.getChildren().add(item);
        }
        resourceMap.put(resource.getPath(), item);
        updateSizesOfAncestors(item, resource.getSize());
    }

    private TreeItem<Resource> createTreeItem(Resource resource) {
        return new TreeItem<>(resource);
    }

    public void updateResource(Resource resource) {
        TreeItem<Resource> item = resourceMap.get(resource.getPath());
        Resource oldResource = item.getValue();
        long sizeDifference = resource.getSize() - oldResource.getSize();
        oldResource.setDisplayName(resource.getDisplayName());
        oldResource.setSize(resource.getSize());
        updateSizesOfAncestors(item, sizeDifference);
    }

    private void updateSizesOfAncestors(TreeItem<Resource> item, long sizeDifference) {
        if (sizeDifference == 0) {
            return;
        }
        if (item.getParent() != null) {
            TreeItem<Resource> parent = item.getParent();
            Resource parentResource = parent.getValue();
            parentResource.addSize(sizeDifference);
            updateSizesOfAncestors(item.getParent(), sizeDifference);
        }
    }

    public void updateResourceNameByPath(Path path, String newName) {
        TreeItem<Resource> item = resourceMap.get(path);
        Resource oldResource = item.getValue();
        oldResource.setDisplayName(newName);
    }

    public void deleteResourceByPath(Path path, TreeTableView<Resource> tableView) {
        TreeItem<Resource> item = resourceMap.get(path);
        if (item.getParent() == null) {
            tableView.setRoot(null);
        } else {
            updateSizesOfAncestors(item, -item.getValue().getSize());
            item.getParent().getChildren().remove(item);
        }
        resourceMap.remove(path);
    }
}
