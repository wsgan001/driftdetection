package de.tudarmstadt.tk.processmining.drift;

import com.fastdtw.dtw.FastDTW;
import com.fastdtw.dtw.TimeWarpInfo;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesBase;
import com.fastdtw.timeseries.TimeSeriesItem;
import com.fastdtw.timeseries.TimeSeriesPoint;
import com.fastdtw.util.DistanceFunction;
import com.fastdtw.util.EuclideanDistance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.SingleLinkage;
import smile.clustering.linkage.WardLinkage;
import sun.nio.cs.StandardCharsets;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Alexander Seeliger on 15.12.2017.
 */
public class DTW {

    public void exportDTWMap(Map<String, Double> distances, String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(fileName), distances);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> generateDTWMap(DateTransitionMap dateTransitionMap) {
        Map<String, Double> distances = new HashMap<>();
        List<Transition> transitions = new ArrayList<>(dateTransitionMap.getTransitions());

        for(int i = 0; i < transitions.size(); i++) {
            for(int j = i; j < transitions.size(); j++) {

                Transition t1 = transitions.get(i);
                Transition t2 = transitions.get(j);

                TimeWarpInfo timeWarpInfo = getDTW(dateTransitionMap.getDateTransitionMap(), t1, t2);
                distances.put(t1.getTransition() + " :: " + t2.getTransition(), timeWarpInfo.getDistance());

            }
        }

        return distances;
    }

    public void clusterDTW(DateTransitionMap dateTransitionMap) {
        List<Transition> transitions = new ArrayList<>(dateTransitionMap.getTransitions());

        int length = 500; // transitions.size();

        double[][] distances = new double[length][length];
        String[] strings = new String[length];

        for(int i = 0; i < length; i++) {
            strings[i] = transitions.get(i).getTransition();

            for(int j = i; j < length; j++) {

                Transition t1 = transitions.get(i);
                Transition t2 = transitions.get(j);

                TimeWarpInfo timeWarpInfo = getDTW(dateTransitionMap.getDateTransitionMap(), t1, t2);
                distances[i][j] = timeWarpInfo.getDistance();
                distances[j][i] = distances[i][j];
            }
        }

//        HierarchicalClustering algorithm = new HierarchicalClustering(new SingleLinkage(distances));
//        int[] clusterMap = algorithm.partition(4d);
//
//        Map<Integer, Set<Transition>> clusters = new HashMap<>();
//        for(int i = 0; i < length; i++) {
//            Set<Transition> transitionSet = clusters.getOrDefault(clusterMap[i], new HashSet<>());
//            transitionSet.add(transitions.get(i));
//            clusters.put(clusterMap[i], transitionSet);
//        }
//
//        int k = 0;
//        for(Integer i : clusters.keySet()) {
//            if(clusters.get(i).size() > 1) {
//                k++;
//            }
//        }

    }

    public TimeWarpInfo getDTW(Map<LocalDate, Set<Transition>> dateTransitionMap, Transition t1, Transition t2) {
        List<TimeSeriesItem> items1 = new ArrayList<>();
        List<TimeSeriesItem> items2 = new ArrayList<>();

        LocalDate beginDate = LocalDate.of(2016, 1, 1);
        LocalDate endDate = LocalDate.of(2017, 1, 31);

        long days = ChronoUnit.DAYS.between(beginDate, endDate);
        Set<Transition> emptySet = new HashSet<>();

        for(int i = 0; i <= days; i++) {
            LocalDate date = beginDate.plusDays(i);

            // t1
            if(dateTransitionMap.getOrDefault(date, emptySet).contains(t1)) {
                TimeSeriesPoint point = new TimeSeriesPoint(new double[] { 1 });
                TimeSeriesItem item = new TimeSeriesItem(i, point);

                items1.add(item);
            } else {
                TimeSeriesPoint point = new TimeSeriesPoint(new double[] { 0 });
                TimeSeriesItem item = new TimeSeriesItem(i, point);

                items1.add(item);
            }

            // t2
            if(dateTransitionMap.getOrDefault(date, emptySet).contains(t2)) {
                TimeSeriesPoint point = new TimeSeriesPoint(new double[] { 1 });
                TimeSeriesItem item = new TimeSeriesItem(i, point);

                items2.add(item);
            } else {
                TimeSeriesPoint point = new TimeSeriesPoint(new double[] { 0 });
                TimeSeriesItem item = new TimeSeriesItem(i, point);

                items2.add(item);
            }
        }

        TimeSeries timeSeries1 = new TimeSeriesBase(items1);
        TimeSeries timeSeries2 = new TimeSeriesBase(items2);

        DistanceFunction distanceFunction = new EuclideanDistance();

        return FastDTW.compare(timeSeries1, timeSeries2, distanceFunction);
    }
}
