package ui;

import actions.AppActions;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vilij.components.ConfirmationDialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;

import static settings.AppPropertyTypes.DATA_RESOURCE_PATH;
import static settings.AppPropertyTypes.SCREENSHOT_ICON;
import static settings.AppPropertyTypes.SCREENSHOT_TOOLTIP;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.NEW_TOOLTIP;

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
    private Label                        label;
    private CheckBox                     checkbox;
    private AppData appData;
    private AppActions appActions;
    protected  String scrnshotPath = new String("");
    public LineChart<Number, Number> getChart() { return chart; }
    public TextArea getTextArea(){return textArea;}
    public Button getSaveButton(){return saveButton;}
    public Button getScrnshotButton(){return scrnshotButton;}

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        appData = new AppData(applicationTemplate);
        appActions = new AppActions(applicationTemplate);
        textArea = new TextArea();
        textArea.setMaxSize(400,300);
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.equals("")){
                    hasNewText = false;
                    newButton.setDisable(!hasNewText);
                    saveButton.setDisable(!hasNewText);}

                else{
                    hasNewText = true;
                    newButton.setDisable(false);
                    saveButton.setDisable(false); }
                if(!oldValue.equals(newValue)){
                    saveButton.setDisable(false);
                }
            }

        });
        displayButton = new Button();
        displayButton.setText("Display");
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis,yAxis);
        
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setTitle("Data Visualization");
        chart.setMaxSize(1000,1000);
        label = new Label();
        label.setText("Data File");
        checkbox = new CheckBox();
        checkbox.setText("Read Only");
        checkbox.setOnAction(e->{
            if(checkbox.isSelected()){
                textArea.setEditable(false);
                textArea.setDisable(true);
            }
            else{
                textArea.setEditable(true);
                textArea.setDisable(false);
            }
        });
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
        printButton = new Button();
        exitButton = new Button();
        super.setToolBar(applicationTemplate);
        scrnshotButton = new Button();
        scrnshotButton = setToolbarButton(scrnshotPath,manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()),true);
        toolBar = new ToolBar(newButton, saveButton, loadButton, printButton, exitButton,scrnshotButton);
        // for homework 1

    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(appActions);
        newButton.setOnAction(e -> {
            applicationTemplate.getActionComponent().handleNewRequest();
        });
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(e -> {
            try {
                ((AppActions)applicationTemplate.getActionComponent()).handleScreenshotRequest();
            } catch (IOException e1) {

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
        // TODO for homework 1
    }

    private void layout() {
        getPrimaryScene().getStylesheets().add("/UI.css");
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        HBox labelBox = new HBox();
        hbox.getChildren().add(vbox);
        vbox.getChildren().add(labelBox);
        appPane.getChildren().add(hbox);
        labelBox.getChildren().add(label);
        vbox.getChildren().add(textArea);
        vbox.getChildren().add(displayButton);
        vbox.getChildren().add(checkbox);
        hbox.getChildren().add(getChart());
        labelBox.setAlignment(Pos.CENTER);
        // TODO for homework 1
    }

    private void setWorkspaceActions() {
        displayButton.setOnAction((event) -> {
            try {
                appData.clear();
                chart.getData().clear();
                appData.loadData(textArea.getText());
                appData.displayData();
                scrnshotButton.setDisable(false);
            }
            catch(TSDProcessor.InvalidDataNameException e){
            }
            catch(Exception e){

            }
        });
        // TODO for homework 1
    }

}
