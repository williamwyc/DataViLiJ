package classification;

import algorithm.Classifier;
import dataprocessors.DataSet;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClassifier extends Classifier{
    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;

    private ApplicationTemplate applicationTemplate;

    private final AtomicBoolean tocontinue;
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

    private int nextCounter;
    private XYChart.Series<Number, Number> line= new XYChart.Series<>();
    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue,
                            ApplicationTemplate applicationTemplate) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.applicationTemplate = applicationTemplate;
    }
    private void addLine(){
        LineChart<Number, Number> chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
        ((AppUI)applicationTemplate.getUIComponent()).getRunButton().setDisable(true);
        ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
        chart.setVisible(true);
        chart.getData().clear();
        dataset.toChartData(chart);
        double max = dataset.getMax();
        double min = dataset.getMin();
        line.getData().add(new XYChart.Data<>(min,0));
        line.getData().add(new XYChart.Data<>(max,0));
        chart.getData().add(line);
        line.getData().get(0).getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: transparent, transparent");
        line.getData().get(1).getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: transparent, transparent");
        line.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: red");
    }
    private void calculate(){
        int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
        int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
        int constant = new Double(RAND.nextDouble() * 1000).intValue();
        double y1 = (constant - xCoefficient * dataset.getMin()) / yCoefficient;
        double y2 = (constant - xCoefficient * dataset.getMax()) / yCoefficient;
        line.getData().get(0).setYValue(y1);
        line.getData().get(1).setYValue(y2);
    }
    private void nextButtonAction(){
        Button nextButton = ((AppUI)applicationTemplate.getUIComponent()).getNextButton();
        nextCounter+=updateInterval;
        Platform.runLater(this::calculate);
        ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
        if(nextCounter>maxIterations){
            nextButton.setDisable(true);
        }
    }
    @Override
    public void run() {
        try{
            Platform.runLater(this::addLine);
            Button nextButton = ((AppUI)applicationTemplate.getUIComponent()).getNextButton();
            nextCounter = 1;
            nextButton.setOnAction(e->nextButtonAction());
            if(!tocontinue.get()){
                nextButton.setDisable(false);
                nextCounter+=updateInterval;
                Platform.runLater(this::calculate);
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
            }
            for (int i = 1; i <= maxIterations&&tocontinue.get(); i++) {
                if (i % updateInterval == 0) {
                    Platform.runLater(this::calculate);
                    Thread.sleep(1000);
                }
            }
            ((AppUI)applicationTemplate.getUIComponent()).getRunButton().setDisable(false);
            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
            ((AppUI)applicationTemplate.getUIComponent()).setRunning(false);
        }catch(InterruptedException i){
            i.printStackTrace();
        }
    }
}
