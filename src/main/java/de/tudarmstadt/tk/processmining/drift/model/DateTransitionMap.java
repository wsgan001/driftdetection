package de.tudarmstadt.tk.processmining.drift.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * @author Alexander Seeliger on 15.12.2017.
 */
public class DateTransitionMap {

    private Map<LocalDate, Map<Transition, TesseractValue>> dateTransitionMap;

    private Set<Transition> transitions;

    private Set<Transition> multipleOccurrenceTransitionSet;

    public DateTransitionMap(Map<LocalDate, Map<Transition, TesseractValue>> dateTransitionMap, Set<Transition> transitions) {
        this.setDateTransitionMap(dateTransitionMap);
        this.setTransitions(transitions);
    }

    public Map<LocalDate, Map<Transition, TesseractValue>> getDateTransitionMap() {
        return dateTransitionMap;
    }

    public void setDateTransitionMap(Map<LocalDate, Map<Transition, TesseractValue>> dateTransitionMap) {
        this.dateTransitionMap = dateTransitionMap;
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(Set<Transition> transitions) {
        this.transitions = transitions;
    }

    public Set<Transition> getMultipleOccurrenceTransitionSet() {
        return multipleOccurrenceTransitionSet;
    }

    public void setMultipleOccurrenceTransitionSet(Set<Transition> multipleOccurrenceTransitionSet) {
        this.multipleOccurrenceTransitionSet = multipleOccurrenceTransitionSet;
    }
}
