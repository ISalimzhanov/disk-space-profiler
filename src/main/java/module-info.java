module com.isalimzhanov.diskspaceprofiler {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires commons.collections;
    requires jdk.compiler;
    requires org.apache.logging.log4j.core;
    requires org.apache.commons.lang3;
    requires directory.watcher;

    opens com.isalimzhanov.diskspaceprofiler to javafx.fxml;
    opens com.isalimzhanov.diskspaceprofiler.controller to javafx.fxml;
    exports com.isalimzhanov.diskspaceprofiler;
}