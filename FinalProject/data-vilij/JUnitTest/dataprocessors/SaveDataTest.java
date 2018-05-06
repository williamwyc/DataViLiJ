package dataprocessors;

import org.junit.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveDataTest {
    //Save to a existing file
    @Test
    public void saveData() throws DataSet.InvalidDataNameException {
        DataSet dataset = new DataSet();
        String data = "@a\ta\t1,1";
        dataset.addInstance(data);
        File file = new File("FinalProject/data-vilij/JUnitTest/dataprocessors/savedatatest.tsd");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {  }
    }
    //Save to a non-existing file
    @Test
    public void saveData2() throws DataSet.InvalidDataNameException {
        DataSet dataset = new DataSet();
        String data = "@a\ta\t1,1";
        dataset.addInstance(data);
        File file = new File("savedatatest.tsd");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {  }
    }
    //Invalid Data to save
    @Test(expected = DataSet.InvalidDataNameException.class)
    public void saveData3() throws DataSet.InvalidDataNameException {
        DataSet dataset = new DataSet();
        String data = "a\ta\t1,1";
        dataset.addInstance(data);
        File file = new File("FinalProject/data-vilij/JUnitTest/dataprocessors/savedatatest.tsd");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {  }
    }
}