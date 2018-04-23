package classification;

import algorithm.Classifier;
import dataprocessors.DataSet;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
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
    // currently, this value does not change after instantiation
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

    private double y1;
    private double y2;
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
    public double getY1(){return y1;}
    public double getY2(){return y2;}

    public void addLine(){
        LineChart<Number, Number> chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
        ((AppUI)applicationTemplate.getUIComponent()).getRunButton().setDisable(true);
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
    public void calculate(){
        int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
        int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
        int constant = new Double(RAND.nextDouble() * 1000).intValue();
        y1 = (constant - xCoefficient * dataset.getMin()) / yCoefficient;
        y2 = (constant - xCoefficient * dataset.getMax()) / yCoefficient;
        line.getData().get(0).setYValue(y1);
        line.getData().get(1).setYValue(y2);
    }
    @Override
    public void run() {
        try{
            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
            Platform.runLater(this::addLine);
            for (int i = 1; i <= maxIterations && tocontinue(); i++) {
                Platform.runLater(this::calculate);
                Thread.sleep(updateInterval*1000);
            }
            ((AppUI)applicationTemplate.getUIComponent()).getRunButton().setDisable(false);
            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            /*if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i); //
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                flush();
                break;
            }*/
        }catch(InterruptedException i){

        }
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /** A placeholder main method to just make sure this code runs smoothly */
    public static void main(String... args) throws IOException {
        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("C:\\Users\\WilliamWYC\\Desktop\\Course\\CSE219\\yichuwu\\cse219homework\\hw1\\data-vilij\\resources\\data\\sample-data.tsd"));
       // RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
       // classifier.run(); // no multithreading yet
    }
}
