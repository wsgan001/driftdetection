package de.tudarmstadt.tk.processmining.drift;

import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.Database;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;
import java.util.logging.Logger;

public class ExperimentsMain {

    private static Logger logger = Logger.getLogger(ExperimentsMain.class.getName());

    public static void main(String[] args) throws JsonProcessingException {
        PatternMining patternMining = new PatternMining();

        // convert gantt to date transition map
        DateTransitionMap dateTransitionMap = patternMining.getDateTransitionMap("gantt.txt");

//        DTW.generateDTWMap(dateTransitionMap);
        DTW.clusterDTW(dateTransitionMap);

        // generate association rules
        Map<Transition, Integer> indexMap = new HashMap<>();

        Database database = patternMining.getDatabase(dateTransitionMap.getDateTransitionMap(), indexMap);

        if(false) {
            List<Rule> rules = patternMining.getTopKRules(database, indexMap, 100, 0.5);

            System.out.println("Association Rules:");
            System.out.println("==================");
            for (Rule rule : rules) {
                System.out.println(String.format("%f %f %s => %s", rule.getConfidence(), rule.getSupport(), rule.getLeftHandSide(), rule.getRightHandSide()));
            }
            System.out.println();
        }

        if(false) {
            // FPclose
            List<FrequentItemset> frequentItems = patternMining.getClosedItemsets(database, indexMap, 0.2);

            System.out.println("Frequent Itemsets:");
            System.out.println("==================");
            for (FrequentItemset itemset : frequentItems) {
                System.out.println(String.format("%d %s", itemset.getSupport(), itemset));
            }
            System.out.println();
        }
    }

}
