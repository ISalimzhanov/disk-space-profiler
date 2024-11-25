package com.isalimzhanov.diskspaceprofiler.view;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeTableCell;

import java.util.function.Function;

public class LoadingTableCell<T> extends TreeTableCell<Resource, T> {

    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;

    private final ProgressIndicator loadingIndicator;
    private final Function<T, String> textMapper;

    public LoadingTableCell(Function<T, String> textMapper) {
        super();
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefWidth(WIDTH);
        loadingIndicator.setPrefHeight(HEIGHT);
        this.textMapper = textMapper;
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getTableRow() == null) {
            clearContent();
        } else if (item == null) {
            showLoadingIndicator();
        } else {
            showItemContent(item);
        }
    }

    private void clearContent() {
        setText(null);
        setGraphic(null);
    }

    private void showLoadingIndicator() {
        setGraphic(loadingIndicator);
        setText(null);
    }

    private void showItemContent(T item) {
        setGraphic(null);
        setText(textMapper.apply(item));
    }
}