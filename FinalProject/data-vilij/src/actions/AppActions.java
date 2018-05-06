package actions;

import dataprocessors.AppData;
import dataprocessors.DataSet;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.text.Text;
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
    private boolean correct = true;
    public boolean getCorrect(){return correct;}
    public void setCorrect(boolean correct){this.correct=correct;}
    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }
    @Override
    public void handleNewRequest() {
        TextArea textArea = ((AppUI)applicationTemplate.getUIComponent()).getTextArea();
        TextArea loadArea = ((AppUI)applicationTemplate.getUIComponent()).getLoadArea();
        textArea.setVisible(true);
        textArea.clear();
        loadArea.clear();
        textArea.setDisable(false);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=oldValue){
                ((AppUI)applicationTemplate.getUIComponent()).getSaveButton().setDisable(false);
            }
            if(newValue==null){
                ((AppUI)applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            }
        });
        ToggleGroup editDone = ((AppUI)applicationTemplate.getUIComponent()).getEditDone();
        editDone.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
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
        });

    }

    @Override
    public void handleSaveRequest() {
        applicationTemplate.getDataComponent().saveData(dataFilePath);
        dataFilePath = ((AppData)applicationTemplate.getDataComponent()).getPath();
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        ((AppUI)applicationTemplate.getUIComponent()).getTextArea().setDisable(true);
        applicationTemplate.getDataComponent().loadData(dataFilePath);
    }

    @Override
    public void handleExitRequest() {
        Button saveButton = ((AppUI)applicationTemplate.getUIComponent()).getSaveButton();
        if(!saveButton.isDisabled()){
            confirm.show("Unsaved Data","Are you sure you want to exit without saving unsaved data?");
            ConfirmationDialog.Option option = confirm.getSelectedOption();
            if(option!=null) {
                if (option.equals(ConfirmationDialog.Option.YES)) {
                    try {
                        promptToSave();
                    } catch (IOException e) {

                    }

                } else if (option.equals(ConfirmationDialog.Option.NO)) {
                    exit(0);
                } else {
                    confirm.close();
                }
            }
        }

        else if(((AppUI)applicationTemplate.getUIComponent()).getRunning()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("An Algorithm is Running");
            alert.setHeaderText("An Algorithm is Running");
            alert.setContentText("Are you sure you want to exit when an algorithm is running?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                exit(0);
            } else {
                alert.close();
            }
        }
        else{
            exit(0);
        }

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
    }

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
        return false;
    }
}
