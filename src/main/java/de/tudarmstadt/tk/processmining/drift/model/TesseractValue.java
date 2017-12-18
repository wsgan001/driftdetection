package de.tudarmstadt.tk.processmining.drift.model;

/**
 * @author Alexander Seeliger on 18.12.2017.
 */
public class TesseractValue {

    private double value;

    private double count;

    public TesseractValue(double value, double count) {
        this.value = value;
        this.count = count;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }
}
