package com.isalimzhanov.diskspaceprofiler.service;

import com.isalimzhanov.diskspaceprofiler.model.Resource;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TreeItem;

import java.util.Comparator;
import java.util.List;

public class UsagePieChartService {

    private static final int MAX_SECTIONS_NUMBER = 10;
    private static final String OTHERS_SECTION_NAME = "Others";
    private static final String LABEL_PATTERN = "%s - %.2f%%";

    public void updatePieChart(TreeItem<Resource> root, PieChart pieChart) {
        ObservableList<PieChart.Data> pieChartData = pieChart.getData();
        pieChartData.clear();
        Resource rootResource = root.getValue();
        List<Resource> resources = root.getChildren().stream()
                .map(TreeItem::getValue)
                .sorted(Comparator.comparing(resource -> -resource.getSize()))
                .toList();

        for (int i = 0; i < Math.min(resources.size(), MAX_SECTIONS_NUMBER - 1); i++) {
            pieChartData.add(buildChildData(resources.get(i), rootResource));
        }
        addOthersSectionIfNeeded(resources, rootResource, pieChartData);
    }

    private void addOthersSectionIfNeeded(List<Resource> resources, Resource rootResource, ObservableList<PieChart.Data> pieChartData) {
        if (resources.size() > MAX_SECTIONS_NUMBER) {
            long othersTotalSize = resources.stream()
                    .skip(MAX_SECTIONS_NUMBER)
                    .mapToLong(Resource::getSize)
                    .sum();
            pieChartData.add(buildOthersTotalData(othersTotalSize, rootResource));
        }
    }

    private PieChart.Data buildChildData(Resource child, Resource parent) {
        double spaceUsagePercentage = calculateSpaceUsagePercentage(child.getSize(), parent.getSize());
        return new PieChart.Data(LABEL_PATTERN.formatted(child.getDisplayName(), spaceUsagePercentage), spaceUsagePercentage);
    }

    private PieChart.Data buildOthersTotalData(long othersTotalSize, Resource parent) {
        double spaceUsagePercentage = calculateSpaceUsagePercentage(othersTotalSize, parent.getSize());
        return new PieChart.Data(LABEL_PATTERN.formatted(OTHERS_SECTION_NAME, spaceUsagePercentage), spaceUsagePercentage);
    }

    private double calculateSpaceUsagePercentage(long childSize, long parentSize) {
        return childSize * 100.0 / parentSize;
    }
}
