package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import vilij.components.ErrorDialog;

import javax.tools.Tool;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;
    private Map<Point2D, String> dataNames;

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        dataNames = new HashMap<>();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      checkedname(tsdString);
                      String   name  = checkedname(list.get(0));
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                      dataNames.put(point,name);
                  }
                  catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    public void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        double max = 0;
        double min = 100;
        double sum = 0;
        double counter = 0;
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            });
            chart.getData().add(series);
            for (XYChart.Data<Number, Number> data : series.getData()) {
                if(data.getXValue().doubleValue()>max){
                    max = data.getXValue().doubleValue();
                }
                if(data.getXValue().doubleValue()<min){
                    min = data.getXValue().doubleValue();
                }
                sum += data.getYValue().doubleValue();
                counter += 1;
                Point2D point = new Point2D(data.getXValue().doubleValue(),data.getYValue().doubleValue());
                Tooltip tooltip = new Tooltip(dataNames.get(point));
                Tooltip.install(data.getNode(),tooltip);
            }
        }
        double average = sum/counter ;
        XYChart.Series<Number, Number> line= new XYChart.Series<>();
        line.getData().add(new XYChart.Data<>(min,average));
        line.getData().add(new XYChart.Data<>(max,average));
        chart.getData().add(line);
        chart.setLegendVisible(false);
        line.getData().get(0).getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: transparent, transparent");
        line.getData().get(1).getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: transparent, transparent");
        line.setName("Average Line");
        line.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: red");
    }


    public void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }
}
