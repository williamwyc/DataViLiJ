package algorithm;
import algorithm.Algorithm;

import java.util.List;

public abstract class Classifier implements Algorithm {
    protected List<Integer> output;

    public List<Integer> getOutput() { return output; }
}
