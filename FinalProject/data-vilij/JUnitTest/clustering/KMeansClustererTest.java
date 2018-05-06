package clustering;

import com.sun.media.sound.InvalidDataException;
import dataprocessors.DataSet;
import org.junit.Test;

import static org.junit.Assert.*;

public class KMeansClustererTest {
    //Valid Data
    @Test
    public void KMeansTest() throws InvalidDataException {
        KMeansClusterer kMeansClusterer = new KMeansClusterer(new DataSet(),1,1,true,1,null);
        if(kMeansClusterer.getMaxIterations()<=0){
            throw new InvalidDataException();
        }
        if(kMeansClusterer.getUpdateInterval()<=0){
            throw new InvalidDataException();
        }
    }
    //Invalid iteration
    @Test(expected = InvalidDataException.class)
    public void KMeansTest2() throws InvalidDataException {
        KMeansClusterer kMeansClusterer = new KMeansClusterer(new DataSet(),-1,1,true,1,null);
        if(kMeansClusterer.getMaxIterations()<=0){
            throw new InvalidDataException();
        }
        if(kMeansClusterer.getUpdateInterval()<=0){
            throw new InvalidDataException();
        }
    }

}