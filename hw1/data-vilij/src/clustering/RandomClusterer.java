package clustering;

import algorithm.Clusterer;
import dataprocessors.DataSet;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClusterer extends Clusterer {


    private DataSet dataset;
    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;
    private int nextCounter;
    private ApplicationTemplate applicationTemplate;
    private static final Random RAND = new Random();
    private List<Point2D> pointList;



    public RandomClusterer(DataSet dataset,
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
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }
    private void nextButtonAction(){
        Button nextButton = ((AppUI)applicationTemplate.getUIComponent()).getNextButton();
        nextCounter+=updateInterval;
        Platform.runLater(this::initialize);
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
        if(!tocontinue.get()){
            nextButton.setDisable(false);
            nextCounter+=updateInterval;
            Platform.runLater(this::initialize);
            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
        }
        else{
            for (int i = 1; i <= maxIterations&&tocontinue.get();i++) {
                Platform.runLater(RandomClusterer.this::initialize);
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

    public void initialize(){
        LineChart<Number, Number> chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
        dataset.getLabels().clear();
        chart.getData().clear();
        for (Point2D point2D:dataset.getLocations().values()) {
            dataset.getLocations().entrySet().stream().filter(entry -> entry.getValue().equals(point2D)).forEach(entry -> {
                String label = ""+(int)((Math.random()*numberOfClusters)+1);
                dataset.getLabels().put(entry.getKey(),label);
            });
        }
        dataset.toChartData(chart);
    }
}
