package de.tudarmstadt.tk.processmining.drift.pattern;

import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.AlgoTopKRules;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.Database;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPClose;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import de.tudarmstadt.tk.processmining.drift.*;
import de.tudarmstadt.tk.processmining.drift.model.DateTransitionMap;
import de.tudarmstadt.tk.processmining.drift.model.TesseractValue;
import de.tudarmstadt.tk.processmining.drift.model.Transition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static de.tudarmstadt.tk.processmining.drift.Utils.parseDate;

/**
 * @author Alexander Seeliger on 12.12.2017.
 */
public class PatternMining {

    private static Logger logger = Logger.getLogger(PatternMining.class.getName());

    /**
     * Converts the given dateTransitionMap into a database which can be used for itemset mining
     *
     * @param dateTransitionMap
     * @param indexMap
     * @return
     */
    public Database getDatabase(Map<LocalDate, Map<Transition, TesseractValue>> dateTransitionMap, Map<Transition, Integer> indexMap) {
        Database database = new Database();

        for (Map<Transition, TesseractValue> transitions : dateTransitionMap.values()) {
            List<String> items = new ArrayList<>();
            for (Transition transition : transitions.keySet()) {
                // convert transition to index
                Integer index = indexMap.getOrDefault(transition, indexMap.size());
                indexMap.put(transition, index);

                items.add(index.toString());
            }
            database.addTransaction(items.toArray(new String[0]));
        }

        return database;
    }

    /**
     * Extracts close itemsets from the given database with a support of at least minSupport.
     *
     * @param database
     * @param indexMap
     * @param minSupport
     * @return
     */
    public List<FrequentItemset> getClosedItemsets(Database database, Map<Transition, Integer> indexMap, double minSupport) {
        List<FrequentItemset> frequentItemsets = new ArrayList<>();

        // store database to disk :(
        Utils.saveDatabase(database, "patterns.txt");

        // generate reverse index map
        Map<Integer, Transition> mapInversed =
                indexMap.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        // extract patterns
        AlgoFPClose fpClose = new AlgoFPClose();
        try {
            Itemsets itemsets = fpClose.runAlgorithm("patterns.txt", null, minSupport);

            for (List<Itemset> itemset : itemsets.getLevels()) {
                for (Itemset item : itemset) {
                    FrequentItemset frequentItemset = new FrequentItemset(Arrays.stream(item.getItems())
                            .mapToObj(mapInversed::get)
                            .collect(Collectors.toList()));
                    frequentItemset.setSupport(item.getAbsoluteSupport());

                    frequentItemsets.add(frequentItemset);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return frequentItemsets;
    }

    /**
     * Extracts the top k association rules from the given database with a confidence of at least minConfidence.
     *
     * @param database
     * @param indexMap
     * @param k
     * @param minConfidence
     * @return
     */
    public List<Rule> getTopKRules(Database database, Map<Transition, Integer> indexMap, int k, double minConfidence) {
        List<Rule> rules = new ArrayList<>();

        // execute the top-k rules algorithm and export to file
        AlgoTopKRules topKRules = new AlgoTopKRules();
        topKRules.runAlgorithm(k, minConfidence, database);
        try {
            topKRules.writeResultTofile("output.txt");
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        // generate reverse index map
        Map<Integer, Transition> mapInversed =
                indexMap.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        // read output file
        try (FileReader fr = new FileReader("output.txt")) {
            try (BufferedReader br = new BufferedReader(fr)) {

                // read all lines of the file
                String line = null;

                while ((line = br.readLine()) != null) {
                    String[] parsedLine = line.split("#");

                    String[] ruleString = parsedLine[0].split(" ==> ");
                    double support = Double.parseDouble(parsedLine[1].replace("SUP: ", "").trim());
                    double confidence = Double.parseDouble(parsedLine[2].replace("CONF: ", "").trim());

                    String[] leftHandSide = ruleString[0].split(" ");
                    String[] rightHandSide = ruleString[1].split(" ");

                    Rule rule = new Rule();
                    rule.setLeftHandSide(Arrays.stream(leftHandSide).map(Integer::valueOf).map(mapInversed::get).collect(Collectors.toList()));
                    rule.setRightHandSide(Arrays.stream(rightHandSide).map(Integer::valueOf).map(mapInversed::get).collect(Collectors.toList()));
                    rule.setConfidence(confidence);
                    rule.setSupport(support);

                    rules.add(rule);
                }

            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return rules;
    }

    /**
     * Converts the given gantt file into a map which stores the when each drift pair occurred.
     *
     * @param fileName
     * @return
     */
    public DateTransitionMap getDateTransitionMap(String fileName) {
        HashMap<LocalDate, Map<Transition, TesseractValue>> dateTransitionMap = new HashMap<>();
        Set<Transition> transitionSet = new HashSet<>();
        Set<Transition> multipleOccurrenceTransitionSet  = new HashSet<>();

        // read gantt file
        try (FileReader fr = new FileReader(fileName)) {
            try (BufferedReader br = new BufferedReader(fr)) {

                // read all lines of the file
                String line = null;

                while ((line = br.readLine()) != null) {
                    String[] parsedLine = line.split(",");

                    String transition = parsedLine[0];
                    LocalDate beginDate = parseDate(parsedLine[1]);
                    LocalDate endDate = parseDate(parsedLine[2]);
                    Double value = Double.parseDouble(parsedLine[3]);
                    Double count = Double.parseDouble(parsedLine[4]);

                    Transition tr = new Transition(transition);
                    if(transitionSet.contains(tr)) {
                        multipleOccurrenceTransitionSet.add(tr);
                    }
                    transitionSet.add(tr);

                    // for each date: store the transitions having a drift
                    long days = ChronoUnit.DAYS.between(beginDate, endDate);

                    for (int i = 0; i <= days; i++) {
                        LocalDate date = beginDate.plusDays(i);

                        Map<Transition, TesseractValue> transitions = dateTransitionMap.getOrDefault(date, new HashMap<>());
                        transitions.put(tr, new TesseractValue(value, count));

                        dateTransitionMap.put(date, transitions);
                    }
                }

            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        DateTransitionMap result = new DateTransitionMap(dateTransitionMap, transitionSet);
        result.setMultipleOccurrenceTransitionSet(multipleOccurrenceTransitionSet);

        return result;
    }

}
