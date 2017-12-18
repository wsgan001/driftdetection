package de.tudarmstadt.tk.processmining.drift.model;

import java.io.Serializable;

/**
 * @author Alexander Seeliger on 15.12.2017.
 */
public class Transition implements Serializable {

    private String transition;

    public Transition(String transition) {
        this.transition = transition;
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
        return transition;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }
}
