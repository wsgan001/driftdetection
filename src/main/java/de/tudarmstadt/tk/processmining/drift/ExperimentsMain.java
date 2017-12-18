package de.tudarmstadt.tk.processmining.drift;

import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.Database;
import de.tudarmstadt.tk.processmining.drift.dtw.DTW;
import de.tudarmstadt.tk.processmining.drift.dtw.DTWMap;
import de.tudarmstadt.tk.processmining.drift.model.DateTransitionMap;
import de.tudarmstadt.tk.processmining.drift.model.Transition;
import de.tudarmstadt.tk.processmining.drift.pattern.FrequentItemset;
import de.tudarmstadt.tk.processmining.drift.pattern.PatternMining;
import de.tudarmstadt.tk.processmining.drift.pattern.Rule;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ExperimentsMain {

    private static Logger logger = Logger.getLogger(ExperimentsMain.class.getName());

    public static void main(String[] args) {
        PatternMining patternMining = new PatternMining();

        // convert gantt to date transition map
        // HINT: the gantt chart needs a fours value (the count)!!
        DateTransitionMap dateTransitionMap = patternMining.getDateTransitionMap("gantt-bpic2017.txt");

        DTW dtw = new DTW();
        DTWMap dtwMap = dtw.generateDTWMap(dateTransitionMap);
        //        Persistence.saveToDisk(dtwMap, "dtwmap-bpic2017.dat");

        //        DTWMap dtwMap = Persistence.loadFromDisk("dtwmap-bpic2017.dat", DTWMap.class);
        dtw.clusterDTW(dateTransitionMap, dtwMap);


        //        // association rule mining / frequent itemset mining
        //        Map<Transition, Integer> indexMap = new HashMap<>();
        //        Database database = patternMining.getDatabase(dateTransitionMap.getDateTransitionMap(), indexMap);
        //
        //        // generate association rules
        //        printAssociationRules(database, indexMap);
        //
        //        // generate frequent itemsets
        //        printFrequentItemsets(database, indexMap);
    }

    private static void printAssociationRules(Database database, Map<Transition, Integer> indexMap) {
        PatternMining patternMining = new PatternMining();

        List<Rule> rules = patternMining.getTopKRules(database, indexMap, 100, 0.5);

        System.out.println("Association Rules:");
        System.out.println("==================");
        for (Rule rule : rules) {
            System.out.println(String.format("%f %f %s => %s", rule.getConfidence(), rule.getSupport(), rule.getLeftHandSide(), rule.getRightHandSide()));
        }
        System.out.println();
    }

    private static void printFrequentItemsets(Database database, Map<Transition, Integer> indexMap) {
        PatternMining patternMining = new PatternMining();

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
