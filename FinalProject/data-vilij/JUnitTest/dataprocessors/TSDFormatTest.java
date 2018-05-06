package dataprocessors;

import org.junit.Test;

import static org.junit.Assert.*;

public class TSDFormatTest {
    //The TSD String with a wrong format of instance name.
    @Test(expected = DataSet.InvalidDataNameException.class)
    public void addInstance1() throws DataSet.InvalidDataNameException {
        DataSet dataSet = new DataSet();
        dataSet.addInstance("a\ta\t1,1");
    }
    //Wrong format of data point
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void addInstance2() throws DataSet.InvalidDataNameException {
        DataSet dataSet = new DataSet();
        dataSet.addInstance("@a a   1 1");
    }
    //Valid TSD String
    @Test
    public void addInstance3() throws DataSet.InvalidDataNameException {
        DataSet dataSet = new DataSet();
        dataSet.addInstance("@a\ta\t1,1");
    }
}