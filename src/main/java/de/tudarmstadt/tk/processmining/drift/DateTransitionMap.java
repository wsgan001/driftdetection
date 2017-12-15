package de.tudarmstadt.tk.processmining.drift;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * @author Alexander Seeliger on 15.12.2017.
 */
public class DateTransitionMap {

    private Map<LocalDate, Set<Transition>> dateTransitionMap;

    private Set<Transition> transitions;

    public DateTransitionMap(Map<LocalDate, Set<Transition>> dateTransitionMap, Set<Transition> transitions) {
        this.setDateTransitionMap(dateTransitionMap);
        this.setTransitions(transitions);
    }

    public Map<LocalDate, Set<Transition>> getDateTransitionMap() {
        return dateTransitionMap;
    }

    public void setDateTransitionMap(Map<LocalDate, Set<Transition>> dateTransitionMap) {
        this.dateTransitionMap = dateTransitionMap;
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(Set<Transition> transitions) {
        this.transitions = transitions;
    }
}
