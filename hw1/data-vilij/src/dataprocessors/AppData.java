package dataprocessors;

import actions.AppActions;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.ArrayList;
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
    private ArrayList           list;
    private ArrayList<String>   label;
    public Path path = Paths.get("");

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
        this.list = new ArrayList();
        this.label = new ArrayList<>();
    }
    public ArrayList<String> getLabelList(){
        return label;
    }
    @Override
    public void loadData(Path dataFilePath) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        ((AppActions)applicationTemplate.getActionComponent()).setCorrect(true);
        try {
            Hashtable instance = new Hashtable();
            Scanner scanner = new Scanner(file);
            boolean correct = true;
            TextArea textarea = ((AppUI)applicationTemplate.getUIComponent()).getTextArea();
            TextArea loadarea = ((AppUI)applicationTemplate.getUIComponent()).getLoadArea();
            clear();
            ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
            int linenumber = 0;
            int numlabel = 0;
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String newinstance;
                String newlabel;
                try{
                    newinstance = line.substring(0,line.indexOf("\t"));
                    newlabel = line.substring(line.indexOf("\t")+1);
                    newlabel = newlabel.substring(0,newlabel.indexOf("\t"));
                    processor.processString(line);
                } catch (Exception e) {
                    clear();
                    correct = false;
                    ErrorDialog error = ErrorDialog.getDialog();
                    error.show("Error","The file has wrong format of data");
                    break;
                }
                if(!instance.containsKey(newinstance)){
                    instance.put(newinstance,line);
                }
                else{
                    ErrorDialog error = ErrorDialog.getDialog();
                    error.show("Error","The file includes a duplicate instance: "+newinstance);
                    clear();
                    correct=false;
                    break;
                }
                if(!label.contains(newlabel)){
                    label.add(newlabel);
                    numlabel++;
                }
                if(linenumber<10) {
                    linenumber++;
                    textarea.appendText(line + "\n");
                }
                else {
                    linenumber++;
                    list.add(line + "\n");
                }
            }
            if(correct) {
                textarea.setVisible(true);
                loadarea.setVisible(true);
                loadarea.appendText(linenumber + " instances with " + numlabel + " labels loaded from:\n" + file.getPath() + "\nThe labels are:\n");
                for (String label : label) {
                    loadarea.appendText("-" + label + "\n");
                }
                ((AppUI) applicationTemplate.getUIComponent()).toggleGroupVisible();
                if(label.size()==2&&!label.contains("")){
                    ((AppUI) applicationTemplate.getUIComponent()).getClassification().setDisable(false);
                }
                else if(label.size()==3&&label.contains("")){
                    ((AppUI) applicationTemplate.getUIComponent()).getClassification().setDisable(false);
                }
                else{
                    ((AppUI) applicationTemplate.getUIComponent()).getClassification().setDisable(true);
                }
                try{
                    DataSet dataSet = DataSet.fromTSDFile(dataFilePath);
                    ((AppUI) applicationTemplate.getUIComponent()).setDataSet(dataSet);
                }catch (Exception e){

                }

            }

        } catch (Exception e) {

        }
        // TODO: NOT A PART OF HW 1
    }

    public void loadData(String dataString) {
        Hashtable instance = new Hashtable();
        String[] data = dataString.split("\n");
        String newinstance;
        String newlabel;
        TextArea textarea = ((AppUI)applicationTemplate.getUIComponent()).getTextArea();
        TextArea loadarea = ((AppUI)applicationTemplate.getUIComponent()).getLoadArea();
        int numlabel = 0;
        loadarea.clear();
        label.clear();
        ((AppActions)applicationTemplate.getActionComponent()).setCorrect(true);
        for(int i = 1;i<=data.length;i++){
            try{
                newinstance = data[i-1].substring(0,data[i-1].indexOf("\t"));
                newlabel = data[i-1].substring(data[i-1].indexOf("\t")+1);
                newlabel = newlabel.substring(0,newlabel.indexOf("\t"));
                processor.processString(data[i-1]);
            } catch (Exception e) {
                ErrorDialog error = ErrorDialog.getDialog();
                error.show("Error","The file has wrong format of data");
                ((AppActions)applicationTemplate.getActionComponent()).setCorrect(false);
                break;
            }
            if(!instance.containsKey(newinstance)){
                instance.put(newinstance,data[i-1]);
            }
            else{
                ErrorDialog error = ErrorDialog.getDialog();
                error.show("Error","The file includes a duplicate instance: "+newinstance);
                ((AppActions)applicationTemplate.getActionComponent()).setCorrect(false);
                loadarea.clear();
                break;
            }
            if(!label.contains(newlabel)){
                label.add(newlabel);
                numlabel++;
            }

        }
        if(((AppActions)applicationTemplate.getActionComponent()).getCorrect()) {
            textarea.setVisible(true);
            loadarea.setVisible(true);
            DataSet dataSet = new DataSet();
            for(int i = 1;i<=data.length;i++){
                try {
                    dataSet.addInstance(data[i-1]);
                } catch (Exception e){

                }
            }
            ((AppUI) applicationTemplate.getUIComponent()).setDataSet(dataSet);
            loadarea.appendText(data.length + " instances with " + numlabel + " labels.\nThe labels are:\n");
            for (String label : label) {
                loadarea.appendText("-" + label + "\n");
            }
            ((AppUI) applicationTemplate.getUIComponent()).toggleGroupVisible();
            if(label.size()==2&&!label.contains("")){
                ((AppUI) applicationTemplate.getUIComponent()).getClassification().setDisable(false);
            }
            else if(label.size()==3&&label.contains("")){
                ((AppUI) applicationTemplate.getUIComponent()).getClassification().setDisable(false);
            }
            else{
                ((AppUI) applicationTemplate.getUIComponent()).getClassification().setDisable(true);
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
                if(file!=null) {
                    try {

                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText());
                        fileWriter.close();
                        path = Paths.get(file.getPath());
                        ((AppUI)applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
                    } catch (IOException e) {

                    }
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
        TextArea textarea = ((AppUI)applicationTemplate.getUIComponent()).getTextArea();
        TextArea loadarea = ((AppUI)applicationTemplate.getUIComponent()).getLoadArea();
        textarea.clear();
        loadarea.clear();
        textarea.setVisible(false);
        loadarea.setVisible(false);
        ((AppUI) applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
        list.clear();
        label.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
