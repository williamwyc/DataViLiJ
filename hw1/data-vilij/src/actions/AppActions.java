package actions;

import dataprocessors.AppData;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
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
    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }
    @Override
    public void handleNewRequest() {
        confirm.show(applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()),applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
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
        //for homework 1
    }

    @Override
    public void handleSaveRequest() {
        applicationTemplate.getDataComponent().saveData(dataFilePath);
        dataFilePath = ((AppData)applicationTemplate.getDataComponent()).getPath();
        ((AppUI)applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
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
