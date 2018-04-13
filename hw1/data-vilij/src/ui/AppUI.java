package ui;

import actions.AppActions;
import dataprocessors.AppData;
import dataprocessors.DataSet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.ArrayList;

import static settings.AppPropertyTypes.SCREENSHOT_ICON;
import static settings.AppPropertyTypes.SCREENSHOT_TOOLTIP;
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
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
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
    private RadioButton                 randomClassification;
    private ToggleGroup                 clusteringSet;
    private ToggleGroup                 classificationSet;
    private AppData appData;
    private AppActions appActions;
    private DataSet dataSet;

    private Label iterationLabel = new Label("Max.Iterations:");
    private TextField iterationText = new TextField();
    private TextField iterationText2 = new TextField();
    private Label intervalLabel = new Label("Update Interval:");
    private TextField intervalText = new TextField();
    private TextField intervalText2 = new TextField();
    private Label continuousLabel = new Label("Continuous Run?");
    private CheckBox continuousBox = new CheckBox();
    private CheckBox continuousBox2 = new CheckBox();


    protected  String scrnshotPath = new String("");
    public LineChart<Number, Number> getChart() { return chart; }
    public TextArea getTextArea(){return textArea;}
    public TextArea getLoadArea(){return loadArea;}
    public Button getSaveButton(){return saveButton;}
    public Button getScrnshotButton(){return scrnshotButton;}
    public ToggleGroup getEditDone(){return editDone;}
    public ToggleButton getClassification(){return classification;}
    public void toggleGroupVisible(){
        toggleLabel.setVisible(true);
        clustering.setVisible(true);
        classification.setVisible(true);
    }
    public DataSet getDataSet(){return dataSet;}
    public void setDataSet(DataSet dataSet){
        this.dataSet = dataSet;
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
        displayButton.setDisable(true);
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis,yAxis);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
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
        clusteringSet = new ToggleGroup();
        randomClustering.setToggleGroup(clusteringSet);
        randomClassification = new RadioButton("Random Classification");
        classificationSet = new ToggleGroup();
        randomClassification.setToggleGroup(classificationSet);
        Button settingA = new Button("Setting");
        Button settingB = new Button("Setting");
        settingA.setOnAction(e->showClusteringSettingWindow(iterationText,intervalText,continuousBox));
        settingB.setOnAction(e->showSettingWindow(iterationText2,intervalText2,continuousBox2));
        HBox radioClustering = new HBox(randomClustering,settingA);
        HBox radioClassification = new HBox(randomClassification,settingB);
        group.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
            if(new_toggle!=null){
                if(new_toggle == clustering){
                    clustering.setDisable(true);
                    if(left.getChildren().contains(radioClassification)){
                        left.getChildren().remove(radioClassification);
                        classification.setDisable(false);
                    }
                    left.getChildren().add(radioClustering);
                }
                else{
                    classification.setDisable(true);
                    if(left.getChildren().contains(radioClustering)){
                        left.getChildren().remove(radioClustering);
                        clustering.setDisable(false);
                    }
                    left.getChildren().add(radioClassification);
                }
            }
        });
        clusteringSet.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
            if(new_toggle!=null){
                left.getChildren().add(displayButton);
            }
        });
        classificationSet.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
            if(new_toggle!=null){
                left.getChildren().add(displayButton);
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
        // for homework 1

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

    private void setWorkspaceActions() {
        displayButton.setOnAction((event) -> {

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
            if(iterationText.getText()!=null){
                try{
                    int i = Integer.parseInt(iterationText.getText());
                    if(i<=0){
                        throw new Exception();
                    }
                }
                catch(Exception es){
                    iterationText.setText("1");
                    ErrorDialog errorDialog = ErrorDialog.getDialog();
                    errorDialog.show("Invalid Input","The iteration value is invalid. The value is automatically changed to 1");
                }
                try{
                    int i = Integer.parseInt(intervalText.getText());
                    if(i<=0){
                        throw new Exception();
                    }
                } catch (Exception e1) {
                    intervalText.setText("1");
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
    private void showClusteringSettingWindow(TextField iterationText, TextField intervalText, CheckBox continuousBox){
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
        ArrayList<String> label = ((AppData)applicationTemplate.getDataComponent()).getLabelList();
        int num = label.size();
        Label information = new Label("The number of cluster:"+num);
        vbox.getChildren().add(information);
        stage.setOnCloseRequest(e->{
            if(iterationText.getText()!=null){
                try{
                    int i = Integer.parseInt(iterationText.getText());
                    if(i<=0){
                        throw new Exception();
                    }
                }
                catch(Exception es){
                    iterationText.setText("1");
                    ErrorDialog errorDialog = ErrorDialog.getDialog();
                    errorDialog.show("Invalid Input","The iteration value is invalid. The value is automatically changed to 1");
                }
                try{
                    int i = Integer.parseInt(intervalText.getText());
                    if(i<=0){
                        throw new Exception();
                    }
                } catch (Exception e1) {
                    intervalText.setText("1");
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
}
