package com.isalimzhanov.diskspaceprofiler;

import com.isalimzhanov.diskspaceprofiler.controller.DiskSpaceProfilerController;
import com.isalimzhanov.diskspaceprofiler.service.ResourceTraversalTask;
import com.isalimzhanov.diskspaceprofiler.view.DiskSpaceProfileView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiskSpaceProfiler extends Application {

    private static final int MAX_THREADS = 100;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        DiskSpaceProfileView view = new DiskSpaceProfileView();
        DiskSpaceProfilerController controller = new DiskSpaceProfilerController(view);

        ExecutorService executorService = null;
        try {
            ResourceTraversalTask traversalTask = new ResourceTraversalTask(Path.of("/home/iskander/"), controller.getResourceHandler());
            executorService = Executors.newFixedThreadPool(MAX_THREADS);
            executorService.submit(traversalTask);
        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }
        }

        Scene scene = new Scene(view, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}