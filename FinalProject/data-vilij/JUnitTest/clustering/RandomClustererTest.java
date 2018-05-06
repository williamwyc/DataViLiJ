package clustering;

import com.sun.media.sound.InvalidDataException;
import dataprocessors.DataSet;
import org.junit.Test;

import static org.junit.Assert.*;

public class RandomClustererTest {
    //Valid Data
    @Test
    public void KMeansTest() throws InvalidDataException {
        RandomClusterer randomClusterer = new RandomClusterer(new DataSet(),1,1,true,1,null);
        if(randomClusterer.getMaxIterations()<=0){
            throw new InvalidDataException();
        }
        if(randomClusterer.getUpdateInterval()<=0){
            throw new InvalidDataException();
        }
    }
    //Invalid iteration
    @Test(expected = InvalidDataException.class)
    public void KMeansTest2() throws InvalidDataException {
        RandomClusterer randomClusterer = new RandomClusterer(new DataSet(),-1,1,true,1,null);
        if(randomClusterer.getMaxIterations()<=0){
            throw new InvalidDataException();
        }
        if(randomClusterer.getUpdateInterval()<=0){
            throw new InvalidDataException();
        }
    }
}