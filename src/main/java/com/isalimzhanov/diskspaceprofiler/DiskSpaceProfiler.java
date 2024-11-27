package com.isalimzhanov.diskspaceprofiler;

import com.isalimzhanov.diskspaceprofiler.util.ExecutorServiceUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DiskSpaceProfiler extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(DiskSpaceProfiler.class.getResource("/views/DiskSpaceProfileView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @Override
    public void stop() {
        ExecutorServiceUtils.shutdown();
    }
}