package de.tudarmstadt.tk.processmining.drift.dtw;

import com.fastdtw.dtw.FastDTW;
import com.fastdtw.dtw.TimeWarpInfo;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesBase;
import com.fastdtw.timeseries.TimeSeriesItem;
import com.fastdtw.timeseries.TimeSeriesPoint;
import com.fastdtw.util.DistanceFunction;
import com.fastdtw.util.EuclideanDistance;
import de.tudarmstadt.tk.processmining.drift.model.DateTransitionMap;
import de.tudarmstadt.tk.processmining.drift.model.TesseractValue;
import de.tudarmstadt.tk.processmining.drift.model.Transition;

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

        LocalDate beginDate = Collections.min(dateTransitionMap.getDateTransitionMap().keySet());
        LocalDate endDate = Collections.max(dateTransitionMap.getDateTransitionMap().keySet());

        int length = transitions.size();

        for (int i = 0; i < length; i++) {
            for (int j = i; j < length; j++) {

                Transition t1 = transitions.get(i);
                Transition t2 = transitions.get(j);

                TimeWarpInfo timeWarpInfo = getDTW(dateTransitionMap.getDateTransitionMap(), beginDate, endDate, t1, t2);
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

        // do some very naive clustering :)
        List<List<Transition>> clusters = new ArrayList<>();
        List<Transition> visited = new ArrayList<>();

        for (int i = 0; i < distances.length; i++) {
            Transition t1 = transitions.get(i);

            if (visited.contains(t1)) {
                continue;
            }

            List<Transition> cluster = new ArrayList<>();
            for (int j = i + 1; j < distances.length; j++) {

                // TODO: Find appropriate threshold
                if (distances[i][j] < 10) {
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
    public TimeWarpInfo getDTW(Map<LocalDate, Map<Transition, TesseractValue>> dateTransitionMap,
                               LocalDate beginDate, LocalDate endDate,
                               Transition t1, Transition t2) {
        List<TimeSeriesItem> items1 = new ArrayList<>();
        List<TimeSeriesItem> items2 = new ArrayList<>();

        long days = ChronoUnit.DAYS.between(beginDate, endDate);
        Map<Transition, TesseractValue> emptySet = new HashMap<>();

        for (int i = 0; i <= days; i++) {
            LocalDate date = beginDate.plusDays(i);

            // t1
            TimeSeriesItem item1 = getTimeSeriesItem(dateTransitionMap.getOrDefault(date, emptySet), t1, i);
            items1.add(item1);

            // t2
            TimeSeriesItem item2 = getTimeSeriesItem(dateTransitionMap.getOrDefault(date, emptySet), t2, i);
            items2.add(item2);
        }

        TimeSeries timeSeries1 = new TimeSeriesBase(items1);
        TimeSeries timeSeries2 = new TimeSeriesBase(items2);

        DistanceFunction distanceFunction = new EuclideanDistance();

        return FastDTW.compare(timeSeries1, timeSeries2, distanceFunction);
    }

    private TimeSeriesItem getTimeSeriesItem(Map<Transition, TesseractValue> transitions, Transition t1, int i) {
        TesseractValue value = transitions.getOrDefault(t1, null);

        if (value != null) {
            // retrieve average tesseract value (I guess: Gantt-Value / Count / Count)
            TimeSeriesPoint point = new TimeSeriesPoint(new double[]{value.getValue() / value.getCount() / value.getCount()});
            TimeSeriesItem item = new TimeSeriesItem(i, point);

            return item;
        } else {
            TimeSeriesPoint point = new TimeSeriesPoint(new double[]{0});
            TimeSeriesItem item = new TimeSeriesItem(i, point);

            return item;
        }
    }
}
