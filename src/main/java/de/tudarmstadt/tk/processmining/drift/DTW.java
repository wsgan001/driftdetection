package de.tudarmstadt.tk.processmining.drift;

import com.fastdtw.dtw.FastDTW;
import com.fastdtw.dtw.TimeWarpInfo;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesBase;
import com.fastdtw.timeseries.TimeSeriesItem;
import com.fastdtw.timeseries.TimeSeriesPoint;
import com.fastdtw.util.DistanceFunction;
import com.fastdtw.util.EuclideanDistance;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Alexander Seeliger on 15.12.2017.
 */
public class DTW {

    /**
     * Calculate the dynamic time warping map for each pair of multiple occurring transitions.
     *
     * @param dateTransitionMap
     * @return
     */
    public DTWMap generateDTWMap(DateTransitionMap dateTransitionMap) {
        DTWMap distances = new DTWMap();
        List<Transition> transitions = new ArrayList<>(dateTransitionMap.getMultipleOccurrenceTransitionSet());

        int length = transitions.size();

        for (int i = 0; i < length; i++) {
            for (int j = i; j < length; j++) {

                Transition t1 = transitions.get(i);
                Transition t2 = transitions.get(j);

                TimeWarpInfo timeWarpInfo = getDTW(dateTransitionMap.getDateTransitionMap(), t1, t2);
                distances.put(new DTWPair(t1, t2), timeWarpInfo.getDistance());

            }
        }

        return distances;
    }

    /**
     * Very naive way to cluster same occurring patterns.
     *
     * @param transitionMap
     * @param dtwMap
     */
    public void clusterDTW(DateTransitionMap transitionMap, DTWMap dtwMap) {
        List<Transition> transitions = new ArrayList<>(transitionMap.getMultipleOccurrenceTransitionSet());

        int length = transitions.size();

        double[][] distances = new double[length][length];

        for (int i = 0; i < length; i++) {
            for (int j = i; j < length; j++) {

                Transition t1 = transitions.get(i);
                Transition t2 = transitions.get(j);

                double distance = dtwMap.get(new DTWPair(t1, t2));
                distances[i][j] = distance;
                distances[j][i] = distances[i][j];
            }
        }

        List<List<Transition>> clusters = new ArrayList<>();
        List<Transition> visited = new ArrayList<>();

        for (int i = 0; i < distances.length; i++) {
            Transition t1 = transitions.get(i);

            if (visited.contains(t1)) {
                continue;
            }

            List<Transition> cluster = new ArrayList<>();
            for (int j = i + 1; j < distances.length; j++) {

                if (distances[i][j] == 0) {
                    Transition t2 = transitions.get(j);

                    if (!visited.contains(t2)) {
                        cluster.add(t2);
                        visited.add(t2);
                    }
                }

            }

            if (cluster.size() > 0) {
                cluster.add(t1);
                visited.add(t1);

                clusters.add(cluster);
            }
        }
    }

    /**
     * Returns the dynamic time warping info for a given pair of transitions.
     *
     * @param dateTransitionMap
     * @param t1
     * @param t2
     * @return
     */
    public TimeWarpInfo getDTW(Map<LocalDate, Set<Transition>> dateTransitionMap, Transition t1, Transition t2) {
        List<TimeSeriesItem> items1 = new ArrayList<>();
        List<TimeSeriesItem> items2 = new ArrayList<>();

        LocalDate beginDate = LocalDate.of(2012, 10, 1);
        LocalDate endDate = LocalDate.of(2015, 10, 31);

        long days = ChronoUnit.DAYS.between(beginDate, endDate);
        Set<Transition> emptySet = new HashSet<>();

        for (int i = 0; i <= days; i++) {
            LocalDate date = beginDate.plusDays(i);

            // t1
            if (dateTransitionMap.getOrDefault(date, emptySet).contains(t1)) {
                TimeSeriesPoint point = new TimeSeriesPoint(new double[]{1});
                TimeSeriesItem item = new TimeSeriesItem(i, point);

                items1.add(item);
            } else {
                TimeSeriesPoint point = new TimeSeriesPoint(new double[]{0});
                TimeSeriesItem item = new TimeSeriesItem(i, point);

                items1.add(item);
            }

            // t2
            if (dateTransitionMap.getOrDefault(date, emptySet).contains(t2)) {
                TimeSeriesPoint point = new TimeSeriesPoint(new double[]{1});
                TimeSeriesItem item = new TimeSeriesItem(i, point);

                items2.add(item);
            } else {
                TimeSeriesPoint point = new TimeSeriesPoint(new double[]{0});
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
