package actions;

import dataprocessors.AppData;
import dataprocessors.DataSet;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ui.AppUI;
import ui.AppUI.*;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import javax.imageio.ImageIO;

import static settings.AppPropertyTypes.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.System.exit;
import static java.lang.System.getProperty;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;
    /** Path to the data file currently active. */
    Path dataFilePath = Paths.get("");
    ConfirmationDialog confirm = ConfirmationDialog.getDialog();
    File file = new File(dataFilePath.toString());
    boolean correct = true;
    public boolean getCorrect(){return correct;}
    public void setCorrect(boolean correct){this.correct=correct;}
    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }
    @Override
    public void handleNewRequest() {
        TextArea textArea = ((AppUI)applicationTemplate.getUIComponent()).getTextArea();
        textArea.setVisible(true);
        textArea.setDisable(false);
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                if(newValue!=oldValue){
                    ((AppUI)applicationTemplate.getUIComponent()).getSaveButton().setDisable(false);
                }
                if(newValue==null){
                    ((AppUI)applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
                }
            }
        });;
        ToggleGroup editDone = ((AppUI)applicationTemplate.getUIComponent()).getEditDone();
        editDone.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov,Toggle toggle, Toggle new_toggle){
                if(new_toggle == editDone.getToggles().get(1)){
                    ((AppData)applicationTemplate.getDataComponent()).loadData(textArea.getText());
                    if(correct){
                        ((AppUI) applicationTemplate.getUIComponent()).toggleGroupVisible();
                        textArea.setDisable(true);
                        editDone.getToggles().get(0).setSelected(false);
                        editDone.getToggles().get(1).setSelected(true);
                    }
                    else{
                        editDone.getToggles().get(0).setSelected(true);
                        editDone.getToggles().get(1).setSelected(false);
                    }
                }
                else{
                    textArea.setDisable(false);
                    editDone.getToggles().get(0).setSelected(true);
                    editDone.getToggles().get(1).setSelected(false);
                }
            }
        });
        /*confirm.show(applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()),applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
        ConfirmationDialog.Option option = confirm.getSelectedOption();
        if(option!=null) {
            if (option.equals(ConfirmationDialog.Option.YES)) {
                try {
                    promptToSave();
                } catch (IOException e) {

                }

            } else if (option.equals(ConfirmationDialog.Option.NO)) {
                applicationTemplate.getUIComponent().clear();
            } else {
                confirm.close();
            }
        }
        */
        //for homework 1
    }

    @Override
    public void handleSaveRequest() {
        applicationTemplate.getDataComponent().saveData(dataFilePath);
        dataFilePath = ((AppData)applicationTemplate.getDataComponent()).getPath();
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        applicationTemplate.getDataComponent().loadData(dataFilePath);
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        exit(0);
        //for homework 1
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        LineChart chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
        WritableImage image = chart.snapshot(new SnapshotParameters(),null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PNG","*.png");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        // TODO: NOT A PART OF HW 1
    }
    public void handleSettingRequest(){
        try{
            Stage stage = new Stage();
            Pane pane = new Pane();
            Scene scene = new Scene(pane,300,300);
            stage.setScene(scene);
            stage.setTitle("Algorithm Run Configuration");
            VBox vbox = new VBox();
            HBox iteration = new HBox();
            Label iterationLabel = new Label("Max.Iterations:");
            HBox interval = new HBox();
            HBox continuous = new HBox();
            vbox.getChildren().addAll(iteration,interval,continuous);
            pane.getChildren().add(vbox);
            stage.show();
        }catch(Exception ex){

        }
    }
    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name()));
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(((AppUI)applicationTemplate.getUIComponent()).getTextArea().getText());
        fileWriter.close();
        dataFilePath = file.toPath();
        ((AppUI)applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
        // for homework 1
        // remove the placeholder line below after you have implemented this method
        return false;
    }
}
