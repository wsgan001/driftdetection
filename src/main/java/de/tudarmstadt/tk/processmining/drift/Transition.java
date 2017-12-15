package de.tudarmstadt.tk.processmining.drift;

/**
 * @author Alexander Seeliger on 15.12.2017.
 */
public class Transition {

    private String transition;

    private double value;

    public Transition(String transition, double value) {
        this.transition = transition;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Transition) {
            return transition.equals(((Transition) obj).transition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return transition.hashCode();
    }

    @Override
    public String toString() {
        return transition + " (" + value + ")";
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }
}
