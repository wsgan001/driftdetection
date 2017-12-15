package de.tudarmstadt.tk.processmining.drift;

import java.util.List;

/**
 * @author Alexander Seeliger on 12.12.2017.
 */
public class Rule {

    private List<Transition> leftHandSide;

    private List<Transition> rightHandSide;

    private double support;

    private double confidence;


    public List<Transition> getLeftHandSide() {
        return leftHandSide;
    }

    public void setLeftHandSide(List<Transition> leftHandSide) {
        this.leftHandSide = leftHandSide;
    }

    public List<Transition> getRightHandSide() {
        return rightHandSide;
    }

    public void setRightHandSide(List<Transition> rightHandSide) {
        this.rightHandSide = rightHandSide;
    }

    public double getSupport() {
        return support;
    }

    public void setSupport(double support) {
        this.support = support;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
