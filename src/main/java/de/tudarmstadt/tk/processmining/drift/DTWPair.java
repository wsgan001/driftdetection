package de.tudarmstadt.tk.processmining.drift;

import java.util.Objects;

/**
 * @author Alexander Seeliger on 18.12.2017.
 */
public class DTWPair {

    private Transition t1;

    private Transition t2;

    public DTWPair(Transition t1, Transition t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public int hashCode() {
        return t1.hashCode() * t2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DTWPair) {
            DTWPair other = (DTWPair)obj;
            return other.t1.equals(t1) && other.t2.equals(t2) || other.t1.equals(t2) && other.t2.equals(t1);
        }
        return false;
    }

    public Transition getT1() {
        return t1;
    }

    public void setT1(Transition t1) {
        this.t1 = t1;
    }

    public Transition getT2() {
        return t2;
    }

    public void setT2(Transition t2) {
        this.t2 = t2;
    }
}
