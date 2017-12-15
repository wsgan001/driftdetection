package de.tudarmstadt.tk.processmining.drift;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Alexander Seeliger on 12.12.2017.
 */
public class FrequentItemset extends ArrayList<Transition> {

    private int support;

    public FrequentItemset(Collection<Transition> items) {
        super(items);
    }


    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }
}
