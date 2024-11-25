package com.isalimzhanov.diskspaceprofiler.service;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;

public final class ResourceTreeService {

    private final ConcurrentLinkedDeque<TreeItem<Resource>> previousRoots;
    private final ConcurrentMap<Path, TreeItem<Resource>> resourceMap;

    public ResourceTreeService(ConcurrentLinkedDeque<TreeItem<Resource>> previousRoots, ConcurrentMap<Path, TreeItem<Resource>> resourceMap) {
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

    public void addResourceToTree(Resource resource, TreeTableView<Resource> treeTable) {
        TreeItem<Resource> item = createTreeItem(resource);
        if (resourceMap.containsKey(resource.getPath().getParent())) {
            addItemToParent(item, resourceMap.get(resource.getPath().getParent()));
        } else {
            addItemToRoot(item, treeTable);
        }
        resourceMap.put(resource.getPath(), item);
    }

    private void addItemToParent(TreeItem<Resource> item, TreeItem<Resource> parent) {
        parent.getChildren().add(item);
    }

    private void addItemToRoot(TreeItem<Resource> item, TreeTableView<Resource> treeTable) {
        if (treeTable.getRoot() == null) {
            treeTable.setRoot(item);
        } else {
            addItemToParent(item, treeTable.getRoot());
        }
    }

    private TreeItem<Resource> createTreeItem(Resource resource) {
        return new TreeItem<>(resource);
    }

    public void updateResourceInTree(Resource resource) {
        TreeItem<Resource> item = resourceMap.get(resource.getPath());
        item.setValue(resource);
    }

    public void updateResourceInTreeRecursively(Resource resource) {
        TreeItem<Resource> item = resourceMap.get(resource.getPath());
        updateResourceInTreeRecursively(resource, item);
    }

    private void updateResourceInTreeRecursively(Resource resource, TreeItem<Resource> item) {
        Resource oldResource = item.getValue();
        item.setValue(resource);
        if (item.getParent() != null) {
            TreeItem<Resource> parent = item.getParent();
            Resource parentResource = parent.getValue();
            long newParentSize = parentResource.getSize() - oldResource.getSize() + resource.getSize();
            updateResourceInTreeRecursively(
                    new Resource(parentResource.getDisplayName(), newParentSize, parentResource.getPath()),
                    parent
            );
        }
    }

    public void updateResourceNameByPath(Path path, String newName) {
        TreeItem<Resource> item = resourceMap.get(path);
        Resource oldResource = item.getValue();
        item.setValue(new Resource(newName, oldResource.getSize(), path));
    }

    public void deleteResourceByPath(Path path, TreeTableView<Resource> tableView) {
        TreeItem<Resource> item = resourceMap.get(path);
        if (item.getParent() == null) {
            tableView.setRoot(null);
        } else {
            item.getParent().getChildren().remove(item);
        }
        resourceMap.remove(path);
    }
}
