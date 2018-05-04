package ui;

import actions.AppActions;
import classification.RandomClassifier;
import clustering.KMeansClusterer;
import clustering.RandomClusterer;
import dataprocessors.AppData;
import dataprocessors.DataSet;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton ; // toolbar button to take a screenshot of the data
    private LineChart<Number, Number> chart ;          // the chart where data will be displayed
    private Button                       displayButton ;  // workspace button to display data on the chart
    private Button                       nextButton;
    private TextArea                     textArea;       // text area for new data input
    private boolean                     isRunning;
    private TextArea                     loadArea;
    private Label                       plotLabel;
    private Label                       toggleLabel;
    private ToggleGroup                 group;
    private ToggleGroup                 editDone;
    private ToggleButton                clustering;
    private ToggleButton                classification;
    private ToggleButton                edit;
    private ToggleButton                done;
    private VBox                        left;
    private RadioButton                 randomClustering;
    private RadioButton                 kMeansCLustering;
    private RadioButton                 randomClassification;
    private ToggleGroup                 algorithmSet;
    private AppData appData;
    private AppActions appActions;
    private DataSet dataSet;
    private String algorithm;
    private Label iterationLabel = new Label("Max.Iterations:");
    private TextField iterationText = new TextField();
    private TextField iterationText2 = new TextField();
    private TextField iterationText3 = new TextField();
    private Label intervalLabel = new Label("Update Interval:");
    private TextField intervalText = new TextField();
    private TextField intervalText2 = new TextField();
    private TextField intervalText3 = new TextField();
    private Label continuousLabel = new Label("Continuous Run?");
    private CheckBox continuousBox = new CheckBox();
    private CheckBox continuousBox2 = new CheckBox();
    private CheckBox continuousBox3 = new CheckBox();
    private Label clusteringLabel = new Label("Number of Clustering");
    private TextField numberClustering = new TextField();
    private TextField numberClusteringK = new TextField();

    private int iteration;
    public void setIteration(int i){this.iteration = i;}
    private int interval;
    public void setInterval(int i){this.interval = i;}
    private boolean continuous;
    public void setContinuous(boolean b){ continuous = b;}
    private int numberCluster;
    public void setNumberClustering(int c){numberCluster = c;}

    private Object o = new Object();
    protected  String scrnshotPath = new String("");
    public LineChart<Number, Number> getChart() { return chart; }
    public TextArea getTextArea(){return textArea;}
    public TextArea getLoadArea(){return loadArea;}
    public Button getSaveButton(){return saveButton;}
    public Button getScrnshotButton(){return scrnshotButton;}
    public Button getRunButton(){return displayButton;}
    public Button getNextButton(){return nextButton;}
    public ToggleGroup getEditDone(){return editDone;}
    public ToggleButton getClassification(){return classification;}
    public boolean getRunning(){return isRunning;}
    public void setRunning(boolean r){
        this.isRunning = r;
    }
    public void toggleGroupVisible(){
        toggleLabel.setVisible(true);
        clustering.setVisible(true);
        classification.setVisible(true);
    }
    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        appData = new AppData(applicationTemplate);
        appActions = new AppActions(applicationTemplate);
        dataSet = new DataSet();
        left = new VBox();
        textArea = new TextArea();
        loadArea = new TextArea();
        textArea.setMaxSize(400,300);
        loadArea.setMaxSize(400,300);
        textArea.setDisable(true);
        loadArea.setEditable(false);
        loadArea.setVisible(false);
        textArea.setVisible(false);
        textArea.setStyle("text-area-background: white;");
        displayButton = new Button("RUN");
        nextButton = new Button("Next Iteration");
        nextButton.setDisable(true);
        displayButton.setDisable(true);
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis,yAxis);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalZeroLineVisible(false);
        chart.setVerticalZeroLineVisible(false);
        chart.setTitle("Data Visualization");
        chart.setMaxSize(1000,1000);
        chart.setVisible(false);
        plotLabel = new Label("Plot: ");
        toggleLabel = new Label("Algorithm Type");
        toggleLabel.setVisible(false);
        clustering = new ToggleButton("Clustering");
        clustering.setVisible(false);
        classification = new ToggleButton("Classification");
        classification.setVisible(false);
        group = new ToggleGroup();
        clustering.setToggleGroup(group);
        classification.setToggleGroup(group);
        randomClustering = new RadioButton("Random Clustering");
        algorithmSet = new ToggleGroup();
        kMeansCLustering = new RadioButton("KMeans Clustering");
        randomClustering.setToggleGroup(algorithmSet);
        kMeansCLustering.setToggleGroup(algorithmSet);
        randomClassification = new RadioButton("Random Classification");
        randomClassification.setToggleGroup(algorithmSet);

        Button settingA = new Button("Setting");
        Button settingB = new Button("Setting");
        Button settingK = new Button("Setting");
        settingA.setOnAction(e->showClusteringSettingWindow(iterationText,intervalText,continuousBox,numberClustering));
        settingB.setOnAction(e->showSettingWindow(iterationText2,intervalText2,continuousBox2));
        settingK.setOnAction(e->showClusteringSettingWindow(iterationText3,intervalText3,continuousBox3,numberClusteringK));
        HBox radioClustering = new HBox(randomClustering,settingA);
        HBox radioClassification = new HBox(randomClassification,settingB);
        HBox kMeansClustering = new HBox(kMeansCLustering,settingK);
        PropertyManager manager = PropertyManager.getManager();
        group.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
            if(new_toggle!=null){
                if(new_toggle == clustering){
                    clustering.setDisable(true);
                    if(left.getChildren().contains(radioClassification)){
                        left.getChildren().remove(radioClassification);
                        classification.setDisable(false);
                    }
                    left.getChildren().addAll(radioClustering,kMeansClustering);
                }
                else{
                    classification.setDisable(true);
                    if(left.getChildren().contains(radioClustering)){
                        left.getChildren().removeAll(radioClustering,kMeansClustering);
                        clustering.setDisable(false);
                    }
                    left.getChildren().add(radioClassification);
                }
            }
        });
        algorithmSet.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
            if(new_toggle!=null){
                if(!left.getChildren().contains(displayButton)){
                    left.getChildren().addAll(displayButton,nextButton);
                }
            }
            if(new_toggle==randomClassification){
                algorithm = manager.getPropertyValue(RANDOM_CLASSIFICATION.name());
            }
            else if(new_toggle==randomClustering){
                algorithm = manager.getPropertyValue(RANDOM_CLUSTERING.name());
            }
            else{
                algorithm = manager.getPropertyValue(KMEANS_CLUSTERING.name());
            }
        });
        newButton.setDisable(false);
        editDone = new ToggleGroup();
        edit = new ToggleButton("Edit");
        edit.setToggleGroup(editDone);
        edit.setSelected(true);
        done = new ToggleButton("Done");
        done.setToggleGroup(editDone);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        PropertyManager manager = PropertyManager.getManager();
        String SEPARATOR = "/";
        String iconsPath = SEPARATOR+String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        scrnshotPath=String.join(SEPARATOR, iconsPath,manager.getPropertyValue(SCREENSHOT_ICON.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        PropertyManager manager = PropertyManager.getManager();
        newButton = new Button();
        saveButton = new Button();
        loadButton = new Button();
        exitButton = new Button();
        super.setToolBar(applicationTemplate);
        scrnshotButton = new Button();
        scrnshotButton = setToolbarButton(scrnshotPath,manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()),true);
        toolBar = new ToolBar(newButton ,loadButton ,saveButton , scrnshotButton, exitButton);

    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(appActions);
        newButton.setOnAction(e -> {
            HBox editDone = new HBox();
            editDone.getChildren().addAll(edit,done);
            left.getChildren().add(1,editDone);
            applicationTemplate.getActionComponent().handleNewRequest();
        });
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(e -> {
            try {
                ((AppActions) applicationTemplate.getActionComponent()).handleScreenshotRequest();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        appData.clear();
        chart.getData().clear();
        textArea.clear();
        scrnshotButton.setDisable(true);
    }

    private void layout() {
        getPrimaryScene().getStylesheets().add("/UI.css");
        VBox right = new VBox();
        HBox hbox = new HBox();
        hbox.getChildren().add(left);
        hbox.getChildren().add(right);
        hbox.setSpacing(20);
        appPane.getChildren().add(hbox);
        left.getChildren().add(textArea);
        left.getChildren().add(loadArea);
        left.getChildren().add(toggleLabel);
        left.getChildren().add(clustering);
        left.getChildren().add(classification);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPadding(new Insets(10, 10, 0, 10));
        right.getChildren().add(plotLabel);
        right.getChildren().add(getChart());
    }

    private synchronized void setWorkspaceActions() {
        displayButton.setOnAction((event) -> {
            isRunning = true;
            try {
                Class c = Class.forName(algorithm);
                Constructor cs = c.getDeclaredConstructor(DataSet.class,int.class,int.class,boolean.class,int.class,ApplicationTemplate.class);
                cs.setAccessible(true);
                dataSet = ((AppData)applicationTemplate.getDataComponent()).getDataSet();
                o = cs.newInstance(dataSet,iteration,interval,continuous,numberCluster,applicationTemplate);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //RandomClassifier classifier = new RandomClassifier(dataSet,iteration,interval,continuous,applicationTemplate);
            //Thread t = new Thread(classifier);
            //RandomClusterer clusterer = new RandomClusterer(dataSet,iteration,interval,numberCluster,applicationTemplate);
            //Thread t = new Thread(clusterer);
            //KMeansClusterer kMeansClusterer = new KMeansClusterer(dataSet,iteration,interval,numberCluster,applicationTemplate);
            //Thread t = new Thread(kMeansClusterer);
            //t.start();
            Thread t = new Thread((Runnable) o);
            t.start();
        });

    }
    private void showSettingWindow(TextField iterationText, TextField intervalText, CheckBox continuousBox){
        Stage stage = new Stage();
        Pane pane = new Pane();
        Scene scene = new Scene(pane,300,300);
        stage.setScene(scene);
        stage.setTitle("Algorithm Run Configuration");
        VBox vbox = new VBox();
        HBox iteration = new HBox();
        iteration.getChildren().addAll(iterationLabel,iterationText);
        HBox interval = new HBox();
        interval.getChildren().addAll(intervalLabel,intervalText);
        HBox continuous = new HBox();
        continuous.getChildren().addAll(continuousLabel,continuousBox);
        vbox.getChildren().addAll(iteration,interval,continuous);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(80,100,100,50));
        vbox.setSpacing(20);
        pane.getChildren().add(vbox);
        stage.setOnCloseRequest(e->{
            setContinuous(continuousBox.isSelected());
            if(iterationText.getText()!=null){
                try{
                    int i = Integer.parseInt(iterationText.getText());
                    setIteration(i);
                    if(i<=0){
                        throw new Exception();
                    }
                }
                catch(Exception es){
                    iterationText.setText("1");
                    setIteration(1);
                    ErrorDialog errorDialog = ErrorDialog.getDialog();
                    errorDialog.show("Invalid Input","The iteration value is invalid. The value is automatically changed to 1");
                }
                try{
                    int i = Integer.parseInt(intervalText.getText());
                    setInterval(i);
                    if(i<=0){
                        throw new Exception();
                    }
                } catch (Exception e1) {
                    intervalText.setText("1");
                    setInterval(1);
                    ErrorDialog errorDialog = ErrorDialog.getDialog();
                    errorDialog.show("Invalid Input","The interval value is invalid. The value is automatically changed to 1");
                }
            }
        });
        iterationText.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                int i = Integer.parseInt(newValue);
                if(i<=0){
                    throw new Exception();
                }
                int j = Integer.parseInt(intervalText.getText());
                if(j<=0){
                    throw new Exception();
                }
                displayButton.setDisable(false);
            }catch(Exception e){
                displayButton.setDisable(true);
            }
        });
        intervalText.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                int i = Integer.parseInt(newValue);
                if(i<=0){
                    throw new Exception();
                }
                int j = Integer.parseInt(iterationText.getText());
                if(j<=0){
                    throw new Exception();
                }
                displayButton.setDisable(false);
            }catch(Exception e){
                displayButton.setDisable(true);
            }
        });
        stage.show();
    }
    private void showClusteringSettingWindow(TextField iterationText, TextField intervalText, CheckBox continuousBox, TextField numberClustering){
        Stage stage = new Stage();
        Pane pane = new Pane();
        Scene scene = new Scene(pane,300,300);
        stage.setScene(scene);
        stage.setTitle("Algorithm Run Configuration");
        VBox vbox = new VBox();
        HBox iteration = new HBox();
        iteration.getChildren().addAll(iterationLabel,iterationText);
        HBox interval = new HBox();
        interval.getChildren().addAll(intervalLabel,intervalText);
        HBox continuous = new HBox();
        continuous.getChildren().addAll(continuousLabel,continuousBox);
        HBox number = new HBox();
        number.getChildren().addAll(clusteringLabel,numberClustering);
        vbox.getChildren().addAll(iteration,interval,continuous,number);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(80,100,100,50));
        vbox.setSpacing(20);
        pane.getChildren().add(vbox);
        stage.setOnCloseRequest(e->{
            setContinuous(continuousBox.isSelected());
            if(iterationText.getText()!=null){
                try{
                    int i = Integer.parseInt(iterationText.getText());
                    setIteration(i);
                    if(i<=0){
                        throw new Exception();
                    }
                }
                catch(Exception es){
                    iterationText.setText("1");
                    setIteration(1);
                    ErrorDialog errorDialog = ErrorDialog.getDialog();
                    errorDialog.show("Invalid Input","The iteration value is invalid. The value is automatically changed to 1");
                }
                try{
                    int i = Integer.parseInt(intervalText.getText());
                    setInterval(i);
                    if(i<=0){
                        throw new Exception();
                    }
                } catch (Exception e1) {
                    intervalText.setText("1");
                    setInterval(1);
                    ErrorDialog errorDialog = ErrorDialog.getDialog();
                    errorDialog.show("Invalid Input","The interval value is invalid. The value is automatically changed to 1");
                }
                try{
                    int c = Integer.parseInt(numberClustering.getText());
                    setNumberClustering(c);
                    if(c<=0){
                        throw new Exception();
                    }
                } catch (Exception e1) {
                    numberClustering.setText("1");
                    setInterval(1);
                    ErrorDialog errorDialog = ErrorDialog.getDialog();
                    errorDialog.show("Invalid Input","The cluster value is invalid. The value is automatically changed to 1");
                }
            }
        });
        iterationText.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                int i = Integer.parseInt(newValue);
                if(i<=0){
                    throw new Exception();
                }
                int j = Integer.parseInt(intervalText.getText());
                if(j<=0){
                    throw new Exception();
                }
                displayButton.setDisable(false);
            }catch(Exception e){
                displayButton.setDisable(true);
            }
        });
        intervalText.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                int i = Integer.parseInt(newValue);
                if(i<=0){
                    throw new Exception();
                }
                int j = Integer.parseInt(iterationText.getText());
                if(j<=0){
                    throw new Exception();
                }
                displayButton.setDisable(false);
            }catch(Exception e){
                displayButton.setDisable(true);
            }
        });
        stage.show();
    }
}
