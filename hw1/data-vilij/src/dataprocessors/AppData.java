package dataprocessors;

import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.ErrorDialog;
import vilij.templates.ApplicationTemplate;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Scanner;

import static settings.AppPropertyTypes.DATA_FILE_EXT;
import static settings.AppPropertyTypes.DATA_FILE_EXT_DESC;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;
    public Path path = Paths.get("");

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        try {
            Hashtable label = new Hashtable();
            Scanner scanner = new Scanner(file);
            TextArea textarea = ((AppUI)applicationTemplate.getUIComponent()).getTextArea();
            int linenumber = 1;
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String newlabel = line.substring(0,line.indexOf("\t"));
                try{
                    processor.processString(line);
                } catch (Exception e) {
                    ErrorDialog error = ErrorDialog.getDialog();
                    error.show("Error","Line "+linenumber+" has wrong format of data");
                    textarea.clear();
                    break;
                }
                if(!label.containsKey(newlabel)){
                    label.put(newlabel,line);
                }
                else{
                    ErrorDialog error = ErrorDialog.getDialog();
                    error.show("Error","Line "+linenumber+" "+newlabel+" is a duplicate");
                    textarea.clear();
                    break;
                }
                if(linenumber<=10) {
                    linenumber++;
                    textarea.appendText(line + "\n");
                }
                else {
                    linenumber++;
                }
            }
            if(linenumber>10){
                ErrorDialog error = ErrorDialog.getDialog();
                error.show("Overloaded","Loaded data consists of "+linenumber+" lines. Showing only the first 10 in the text area.");
            }
        } catch (FileNotFoundException e) {

        }
        // TODO: NOT A PART OF HW 1
    }

    public void loadData(String dataString) throws Exception {
        Boolean correct = true;
        Hashtable label = new Hashtable();
        String[] data = dataString.split("\n");
        for(int i = 1;i<=data.length;i++){
            try{
                processor.processString(data[i-1]);
            } catch (Exception e) {
                ErrorDialog error = ErrorDialog.getDialog();
                error.show("Error","Line "+i+" has wrong format of data");
                throw new Exception();
            }
        }
        if(correct){
            for(int i = 1;i<=data.length;i++){
                String newlabel = data[i-1].substring(0,data[i-1].indexOf("\t"));
                if(!label.containsKey(newlabel)){
                    label.put(newlabel,data[i-1]);
                }
                else{
                    ErrorDialog error = ErrorDialog.getDialog();
                    error.show("Error",newlabel+" is a duplicate");
                    throw new TSDProcessor.InvalidDataNameException("");
                }
            }
        }
        // for homework 1
    }
    @Override
    public void saveData(Path dataFilePath) {
        Boolean correct = true;
        Hashtable label = new Hashtable();
        String[] data = ((AppUI)applicationTemplate.getUIComponent()).getTextArea().getText().split("\n");
        for(int i = 1;i<=data.length;i++){
            try{
                processor.processString(data[i-1]);
            } catch (Exception e) {
                ErrorDialog error = ErrorDialog.getDialog();
                error.show("Error","Line "+i+" has wrong format of data");
                correct = false;
                break;
            }
        }
        if(correct){
            for(int i = 1;i<=data.length;i++){
                String newlabel = data[i-1].substring(0,data[i-1].indexOf("\t"));
                if(!label.containsKey(newlabel)){
                    label.put(newlabel,data[i-1]);
                }
                else{
                    ErrorDialog error = ErrorDialog.getDialog();
                    error.show("Error",newlabel+" is a duplicate");
                    correct = false;
                    break;
                }
            }
        }
        if(correct){
            File file = new File(dataFilePath.toString());
            if(file.exists()){
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(((AppUI)applicationTemplate.getUIComponent()).getTextArea().getText());
                    fileWriter.close();
                } catch (IOException e) {  }
            }
            else{
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name()));
                fileChooser.getExtensionFilters().add(filter);
                file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                try {

                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(((AppUI)applicationTemplate.getUIComponent()).getTextArea().getText());
                    fileWriter.close();
                    path = Paths.get(file.getPath());
                } catch (IOException e) {

                }
            }
        }
        // TODO: NOT A PART OF HW 1
    }
    public Path getPath(){
        return path;
    }
    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
