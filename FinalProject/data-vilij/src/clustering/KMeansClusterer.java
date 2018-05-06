package clustering;

import algorithm.Clusterer;
import dataprocessors.DataSet;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */
public class KMeansClusterer extends Clusterer {

    private DataSet dataset;
    private List<Point2D> centroids;

    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;
    private ApplicationTemplate applicationTemplate;
    private int nextCounter;

    public KMeansClusterer(DataSet dataset,
                           int maxIterations,
                           int updateInterval,
                           boolean continuous,
                           int numberOfClusters,
                           ApplicationTemplate applicationTemplate) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(continuous);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public int getMaxIterations() { return maxIterations; }

    @Override
    public int getUpdateInterval() { return updateInterval; }

    @Override
    public boolean tocontinue() { return tocontinue.get(); }

    private void nextButtonAction(){
        Button nextButton = ((AppUI)applicationTemplate.getUIComponent()).getNextButton();
        nextCounter+=1;
        assignLabels();
        recomputeCentroids();
        LineChart<Number, Number> chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
        chart.getData().clear();
        dataset.toChartData(chart);
        ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
        if(nextCounter>maxIterations){
            nextButton.setDisable(true);
            ((AppUI)applicationTemplate.getUIComponent()).getRunButton().setDisable(false);
            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
            ((AppUI)applicationTemplate.getUIComponent()).setRunning(false);
        }
    }
    @Override
    public void run() {
        int iteration = 0;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                LineChart<Number, Number> chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
                ((AppUI)applicationTemplate.getUIComponent()).getRunButton().setDisable(true);
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
                chart.setVisible(true);
                chart.setAnimated(false);
            }
        });
        Button nextButton = ((AppUI)applicationTemplate.getUIComponent()).getNextButton();
        nextCounter = 1;
        nextButton.setOnAction(e->nextButtonAction());
        initializeCentroids();
        if(!tocontinue.get()){
            nextButton.setDisable(false);
            nextCounter+=updateInterval;
            assignLabels();
            recomputeCentroids();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    LineChart<Number, Number> chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
                    chart.getData().clear();
                    dataset.toChartData(chart);
                }
            });

            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
        }
        else{
            while (iteration++ < maxIterations) {
                assignLabels();
                recomputeCentroids();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        LineChart<Number, Number> chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
                        chart.getData().clear();
                        dataset.toChartData(chart);
                    }

                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ((AppUI)applicationTemplate.getUIComponent()).getRunButton().setDisable(false);
            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
            ((AppUI)applicationTemplate.getUIComponent()).setRunning(false);
        }

    }

    private void initializeCentroids() {
        Set<String>  chosen        = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(dataset.getLabels().keySet());
        Random       r             = new Random();
        while (chosen.size() < numberOfClusters) {
            int i = r.nextInt(instanceNames.size());
            while (chosen.contains(instanceNames.get(i)))
                ++i;
            chosen.add(instanceNames.get(i));
        }
        centroids = chosen.stream().map(name -> dataset.getLocations().get(name)).collect(Collectors.toList());
    }

    private void assignLabels() {
        dataset.getLocations().forEach((instanceName, location) -> {
            double minDistance      = Double.MAX_VALUE;
            int    minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            dataset.getLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });

    }

    private void recomputeCentroids() {
        IntStream.range(0, numberOfClusters).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum = dataset.getLabels()
                    .entrySet()
                    .stream()
                    .filter(entry -> i == Integer.parseInt(entry.getValue()))
                    .map(entry -> dataset.getLocations().get(entry.getKey()))
                    .reduce(new Point2D(0, 0), (p, q) -> {
                        clusterSize.incrementAndGet();
                        return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                    });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))) {
                centroids.set(i, newCentroid);
            }
        });

    }

    private static double computeDistance(Point2D p, Point2D q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }

}
