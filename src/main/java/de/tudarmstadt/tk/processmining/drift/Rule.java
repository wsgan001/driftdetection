package de.tudarmstadt.tk.processmining.drift;

import java.util.List;

/**
 * @author Alexander Seeliger on 12.12.2017.
 */
public class Rule {

    private List<String> leftHandSide;

    private List<String> rightHandSide;

    private double support;

    private double confidence;


    public List<String> getLeftHandSide() {
        return leftHandSide;
    }

    public void setLeftHandSide(List<String> leftHandSide) {
        this.leftHandSide = leftHandSide;
    }

    public List<String> getRightHandSide() {
        return rightHandSide;
    }

    public void setRightHandSide(List<String> rightHandSide) {
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
