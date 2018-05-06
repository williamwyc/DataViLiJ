package classification;

import com.sun.media.sound.InvalidDataException;
import dataprocessors.DataSet;
import org.junit.Test;

import static org.junit.Assert.*;

public class RandomClassifierTest {
    //Valid Data
    @Test
    public void ClassifierTest() throws InvalidDataException {
        RandomClassifier classifier = new RandomClassifier(new DataSet(),1,1,true,1,null);
        if(classifier.getMaxIterations()<=0){
            throw new InvalidDataException();
        }
        if(classifier.getUpdateInterval()<=0){
            throw new InvalidDataException();
        }
    }
    //Invalid iteration
    @Test(expected = InvalidDataException.class)
    public void ClassifierTest2() throws InvalidDataException {
        RandomClassifier classifier = new RandomClassifier(new DataSet(),-1,1,true,1,null);
        if(classifier.getMaxIterations()<=0){
            throw new InvalidDataException();
        }
        if(classifier.getUpdateInterval()<=0){
            throw new InvalidDataException();
        }
    }
}