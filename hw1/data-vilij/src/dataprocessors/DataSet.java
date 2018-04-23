package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DataSet {
    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    private static String nameFormatCheck(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }

    private static Point2D locationOf(String locationString) {
        String[] coordinateStrings = locationString.trim().split(",");
        return new Point2D(Double.parseDouble(coordinateStrings[0]), Double.parseDouble(coordinateStrings[1]));
    }

    private Map<String, String>  labels;
    private Map<String, Point2D> locations;
    private double max=0;
    private double min=100;
    /** Creates an empty dataset. */
    public DataSet() {
        labels = new HashMap<>();
        locations = new HashMap<>();
    }

    public Map<String, String> getLabels()     { return labels; }

    public Map<String, Point2D> getLocations() { return locations; }

    public double getMax(){ return max;}
    public double getMin(){ return min;}
    public void toChartData(XYChart<Number, Number> chart){
        Set<String> label = new HashSet<>(labels.values());
        for (String pointlabel : label) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(pointlabel);
            labels.entrySet().stream().filter(entry -> entry.getValue().equals(pointlabel)).forEach(entry -> {
                Point2D point = locations.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            });
            chart.getData().add(series);
            for (XYChart.Data<Number, Number> data : series.getData()) {
                if (data.getXValue().doubleValue() > max) {
                    max = data.getXValue().doubleValue();
                }
                if (data.getXValue().doubleValue() < min) {
                    min = data.getXValue().doubleValue();
                }
            }
        }
    }

    public void updateLabel(String instanceName, String newlabel) {
        if (labels.get(instanceName) == null)
            throw new NoSuchElementException();
        labels.put(instanceName, newlabel);
    }

    public void addInstance(String tsdLine) throws InvalidDataNameException {
        String[] arr = tsdLine.split("\t");
        labels.put(nameFormatCheck(arr[0]), arr[1]);
        locations.put(arr[0], locationOf(arr[2]));
    }

    public static DataSet fromTSDFile(Path tsdFilePath) throws IOException {
        DataSet dataset = new DataSet();
        Files.lines(tsdFilePath).forEach(line -> {
            try {
                dataset.addInstance(line);
            } catch (InvalidDataNameException e) {
                e.printStackTrace();
            }
        });
        return dataset;
    }
}
