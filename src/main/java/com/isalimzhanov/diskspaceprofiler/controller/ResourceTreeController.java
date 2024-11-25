package com.isalimzhanov.diskspaceprofiler.controller;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import com.isalimzhanov.diskspaceprofiler.service.FileService;
import com.isalimzhanov.diskspaceprofiler.service.ResourceTraversalTask;
import com.isalimzhanov.diskspaceprofiler.service.ResourceTreeService;
import com.isalimzhanov.diskspaceprofiler.util.ExecutorServiceUtils;
import com.isalimzhanov.diskspaceprofiler.util.FormatUtils;
import com.isalimzhanov.diskspaceprofiler.view.LoadingTableCell;
import io.methvin.watcher.DirectoryChangeEvent;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ResourceTreeController implements Initializable {

    private static final double NAME_COLUMN_WIDTH_COEFFICIENT = 0.7;
    private static final double SIZE_COLUMN_WIDTH_COEFFICIENT = 0.3;

    private final ResourceTreeService resourceTreeService = new ResourceTreeService(new ConcurrentLinkedDeque<>(), new ConcurrentHashMap<>());
    private final FileService fileService = new FileService();

    // FIXME get correct root
    private final Path rootPath = Path.of("/home/iskander/IdeaProjects/SomeOtherFolder");

    @FXML
    private Button goBackButton;

    @FXML
    private Button openParentButton;

    @FXML
    private TreeTableView<Resource> treeTable;

    @FXML
    private TreeTableColumn<Resource, String> nameColumn;

    @FXML
    private TreeTableColumn<Resource, Long> sizeColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTreeTableColumns();
        setupEventHandlers();
        setupTraversalTask();
        ExecutorServiceUtils.submit(this::setupRootWatcher);
    }

    private void setupRootWatcher() {
        try {
            fileService.watchDirectory(rootPath, event -> {
                switch (event.eventType()) {
                    case CREATE -> handleResourceCreatedEvent(event);
                    case MODIFY -> handleResourceModifiedEvent(event);
                    case DELETE -> handleResourceDeletedEvent(event);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleResourceDeletedEvent(DirectoryChangeEvent event) {
        resourceTreeService.deleteResourceByPath(event.path(), treeTable);
    }

    private void handleResourceModifiedEvent(DirectoryChangeEvent event) throws IOException {
        if (event.isDirectory()) {
            resourceTreeService.updateResourceNameByPath(event.path(), fileService.getName(event.path()));
        } else {
            Resource resource = fileService.buildResource(event.path(), fileService.getFileSize(event.path()));
            resourceTreeService.updateResourceInTreeRecursively(resource);
        }
    }

    private void handleResourceCreatedEvent(DirectoryChangeEvent event) throws IOException {
        long size = event.isDirectory() ? 0L : fileService.getFileSize(event.path());
        Resource resource = fileService.buildResource(event.path(), size);
        resourceTreeService.addResourceToTree(resource, treeTable);
    }

    private void setupTraversalTask() {
        ResourceTraversalTask traversalTask = new ResourceTraversalTask(rootPath, this::addResource, this::updateResource, fileService);
        ExecutorServiceUtils.submit(traversalTask);
    }

    private void setupEventHandlers() {
        goBackButton.setOnAction(this::handleGoBackButton);
        openParentButton.setOnAction(this::handleOpenParentButton);
        treeTable.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleTreeItemClick);
        treeTable.widthProperty().addListener((observableValue, oldWidth, newWidth) -> updateColumnsWidth(newWidth.doubleValue()));
    }

    private void updateColumnsWidth(double tableWidth) {
        nameColumn.setPrefWidth(tableWidth * NAME_COLUMN_WIDTH_COEFFICIENT);
        sizeColumn.setPrefWidth(tableWidth * SIZE_COLUMN_WIDTH_COEFFICIENT);
    }

    private void handleTreeItemClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            resourceTreeService.selectNewRoot(treeTable);
        }
    }

    private void handleOpenParentButton(ActionEvent event) {
        resourceTreeService.openRootParent(treeTable);
    }

    private void handleGoBackButton(ActionEvent event) {
        resourceTreeService.restorePreviousRoot(treeTable);
    }

    private void setupTreeTableColumns() {
        setupColumn(nameColumn, Resource::getDisplayName, null);
        setupColumn(sizeColumn, Resource::getSize, () -> new LoadingTableCell<>(FormatUtils::formatSize));
    }

    private <T> void setupColumn(TreeTableColumn<Resource, T> column, Function<Resource, T> valueProvider, Supplier<TreeTableCell<Resource, T>> cellFactory) {
        column.setCellValueFactory(cell -> new SimpleObjectProperty<>(valueProvider.apply(cell.getValue().getValue())));
        if (cellFactory != null) {
            column.setCellFactory(cell -> cellFactory.get());
        }
    }

    public void addResource(Resource resource) {
        resourceTreeService.addResourceToTree(resource, treeTable);
    }

    public void updateResource(Resource resource) {
        resourceTreeService.updateResourceInTree(resource);
    }
}
