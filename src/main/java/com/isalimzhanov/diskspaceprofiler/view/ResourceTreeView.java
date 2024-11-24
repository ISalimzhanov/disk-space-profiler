package com.isalimzhanov.diskspaceprofiler.view;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import com.isalimzhanov.diskspaceprofiler.utils.ImageUtils;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;


public final class ResourceTreeView extends BorderPane {

    private static final int BUTTON_WIDTH_PX = 24;
    private static final int BUTTON_HEIGHT_PX = 24;

    private final TreeTableView<Resource> treeTable;
    private final ImageButtonView goBackButton;
    private final ImageButtonView openParentButton;

    public ResourceTreeView() {
        getStyleClass().add("resource-tree-view");
        this.treeTable = createTree();
        this.openParentButton = new ImageButtonView(ImageUtils.OPEN_PARENT_PATH, BUTTON_WIDTH_PX, BUTTON_HEIGHT_PX);
        this.goBackButton = new ImageButtonView(ImageUtils.BACK_ARROW_PATH, BUTTON_WIDTH_PX, BUTTON_HEIGHT_PX);

        HBox buttonBox = new HBox(goBackButton, openParentButton);
        buttonBox.getStyleClass().add("button-box");

        setTop(buttonBox);
        setCenter(treeTable);
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/resource-tree.css")).toExternalForm());
    }

    private TreeTableView<Resource> createTree() {
        TreeTableView<Resource> treeTable = new TreeTableView<>();
        treeTable.getColumns().addAll(createColumns());
        return treeTable;
    }

    private List<TreeTableColumn<Resource, ?>> createColumns() {
        return List.of(
                createColumn("Name", resource -> new SimpleObjectProperty<>(resource.getDisplayName())),
                createColumn("Size", resource -> new SimpleLongProperty(resource.getSize()).asObject()),
                createColumn("Space usage %", resource -> new SimpleObjectProperty<>(resource.getSpaceUsagePercentage()))
        );
    }

    private <T> TreeTableColumn<Resource, T> createColumn(String name, Function<Resource, Property<T>> propertyMapper) {
        TreeTableColumn<Resource, T> column = new TreeTableColumn<>(name);
        column.setCellValueFactory(param -> propertyMapper.apply(param.getValue().getValue()));
        return column;
    }

    public void setGoBackAction(EventHandler<ActionEvent> eventHandler) {
        goBackButton.setOnAction(eventHandler);
    }

    public void setOpenParentAction(EventHandler<ActionEvent> eventHandler) {
        openParentButton.setOnAction(eventHandler);
    }

    public void addTreeEventHandler(EventType<MouseEvent> eventType, EventHandler<MouseEvent> eventHandler) {
        treeTable.addEventHandler(eventType, eventHandler);
    }

    public TreeItem<Resource> getRoot() {
        return treeTable.getRoot();
    }

    public void setRoot(TreeItem<Resource> newRoot) {
        treeTable.setRoot(newRoot);
    }

    public void setSelectedItemToRoot() {
        TreeItem<Resource> selectedItem = treeTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            throw new IllegalStateException("There is no selected item");
        }
        if (!selectedItem.isLeaf()) {
            treeTable.setRoot(selectedItem);
        }
    }

    public void addTreeItem(TreeItem<Resource> item, TreeItem<Resource> parent) {
        parent.getChildren().add(item);
    }
}
