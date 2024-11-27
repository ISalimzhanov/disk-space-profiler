package com.isalimzhanov.diskspaceprofiler.controller;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import com.isalimzhanov.diskspaceprofiler.service.FileService;
import com.isalimzhanov.diskspaceprofiler.service.ResourceTraversalTask;
import com.isalimzhanov.diskspaceprofiler.service.ResourceTreeService;
import com.isalimzhanov.diskspaceprofiler.service.UsagePieChartService;
import com.isalimzhanov.diskspaceprofiler.util.ExecutorServiceUtils;
import com.isalimzhanov.diskspaceprofiler.view.SizeTableCell;
import io.methvin.watcher.DirectoryChangeEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ResourceTreeController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(ResourceTreeController.class);

    private static final double NAME_COLUMN_WIDTH_COEFFICIENT = 0.7;
    private static final double SIZE_COLUMN_WIDTH_COEFFICIENT = 0.3;
    private static final long DEBOUNCE_DELAY_MS = 100;

    private final ResourceTreeService resourceTreeService = new ResourceTreeService(new ConcurrentLinkedDeque<>(), new ConcurrentHashMap<>());
    private final UsagePieChartService usagePieChartService = new UsagePieChartService();
    private final Queue<Runnable> uiUpdateQueue = new ConcurrentLinkedQueue<>();
    private final FileService fileService = new FileService();

    @FXML
    public HBox warningBox;

    @FXML
    public PieChart usagePieChart;

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
        LOGGER.info("Initializing resource tree controller");
        setupTreeTableColumns();
        setupEventHandlers();
        setupTraversalTask();
        setupRootWatcher();
        startUIUpdateHandler();
        setupUsagePieChart();
        LOGGER.info("Successfully initialized resource tree controller");
    }

    private void setupUsagePieChart() {
        treeTable.rootProperty().addListener(
                (observable, oldValue, newValue) -> updatePieChart(newValue)
        );
    }

    private void updatePieChart(TreeItem<Resource> newRoot) {
        Runnable updateTask = () -> usagePieChartService.updatePieChart(treeTable.getRoot(), usagePieChart);
        newRoot.valueProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(updateTask));
        Platform.runLater(updateTask);
    }

    private void setupRootWatcher() {
        ExecutorServiceUtils.submit(() -> fileService.watchRootPaths(event -> {
            LOGGER.info("Handling directory change event: {}", event);
            try {
                if (fileService.shouldSkip(event.path())) {
                    LOGGER.debug("Skipping event: {}", event);
                    return;
                }
                switch (event.eventType()) {
                    case CREATE -> handleResourceCreatedEvent(event);
                    case MODIFY -> handleResourceModifiedEvent(event);
                    case DELETE -> handleResourceDeletedEvent(event);
                    default ->
                            throw new IllegalArgumentException("Unexpected event type " + event.eventType() + " for path " + event.path());
                }
                LOGGER.info("Successfully handled directory change event: {}", event);
            } catch (IOException e) {
                LOGGER.warn("Failed to handled directory change event: {}", event);
            }
        }));
    }

    private void handleResourceDeletedEvent(DirectoryChangeEvent event) {
        scheduleUIUpdate(() -> resourceTreeService.deleteResourceByPath(event.path(), treeTable));
    }

    private void handleResourceModifiedEvent(DirectoryChangeEvent event) throws IOException {
        if (event.isDirectory()) {
            scheduleUIUpdate(() -> resourceTreeService.updateResourceNameByPath(event.path(), fileService.getName(event.path())));
        } else {
            Resource resource = fileService.buildResource(event.path(), fileService.getFileSize(event.path()));
            scheduleUIUpdate(() -> resourceTreeService.updateResource(resource));
        }
    }

    private void handleResourceCreatedEvent(DirectoryChangeEvent event) throws IOException {
        long size = event.isDirectory() ? 0L : fileService.getFileSize(event.path());
        Resource resource = fileService.buildResource(event.path(), size);
        scheduleUIUpdate(() -> resourceTreeService.addOrUpdateResource(resource, treeTable));
    }

    private void setupTraversalTask() {
        ResourceTraversalTask traversalTask = new ResourceTraversalTask(resource -> scheduleUIUpdate(() -> addResource(resource)), fileService);
        traversalTask.setOnSucceeded(event -> Platform.runLater(() -> warningBox.setVisible(false)));
        ExecutorServiceUtils.submit(traversalTask);
    }

    private void setupEventHandlers() {
        goBackButton.setOnAction(this::handleGoBackButton);
        openParentButton.setOnAction(this::handleOpenParentButton);
        treeTable.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleTreeItemClick);
        treeTable.widthProperty().addListener((observableValue, oldWidth, newWidth) -> updateColumnsWidth(newWidth.doubleValue()));
        BorderPane parentHBox = (BorderPane) usagePieChart.getParent();
        usagePieChart.prefWidthProperty().bind(parentHBox.widthProperty().multiply(0.5));
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
        setupColumn(nameColumn, Resource::getDisplayNameProperty);
        setupColumn(sizeColumn, resource -> resource.getSizeProperty().asObject(), SizeTableCell::new);
    }

    private <T> void setupColumn(
            TreeTableColumn<Resource, T> column,
            Function<Resource, ObservableValue<T>> valueFactory
    ) {
        column.setCellValueFactory(cell -> {
            if (cell.getValue() != null && cell.getValue().getValue() != null) {
                return valueFactory.apply(cell.getValue().getValue());
            }
            return null;
        });
    }

    private <T> void setupColumn(
            TreeTableColumn<Resource, T> column,
            Function<Resource, ObservableValue<T>> valueFactory,
            Supplier<TreeTableCell<Resource, T>> cellFactory
    ) {
        setupColumn(column, valueFactory);
        column.setCellFactory(cell -> cellFactory.get());
    }

    public void addResource(Resource resource) {
        scheduleUIUpdate(() -> resourceTreeService.addResource(resource, treeTable));
    }

    private void startUIUpdateHandler() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(DEBOUNCE_DELAY_MS), e -> processUIUpdates()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void processUIUpdates() {
        Platform.runLater(() -> {
            Runnable task;
            while ((task = uiUpdateQueue.poll()) != null) {
                task.run();
            }
        });
    }

    private void scheduleUIUpdate(Runnable task) {
        uiUpdateQueue.add(task);
    }
}
