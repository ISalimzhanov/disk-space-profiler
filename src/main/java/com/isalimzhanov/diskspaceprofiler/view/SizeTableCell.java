package com.isalimzhanov.diskspaceprofiler.view;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import com.isalimzhanov.diskspaceprofiler.util.FormatUtils;
import javafx.scene.control.TreeTableCell;

public class SizeTableCell extends TreeTableCell<Resource, Long> {

    @Override
    protected void updateItem(Long item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getTableRow() == null || item == null) {
            clearContent();
        } else {
            showItemContent(item);
        }
    }

    private void clearContent() {
        setText(null);
    }

    private void showItemContent(Long item) {
        setText(FormatUtils.formatSize(item));
    }
}