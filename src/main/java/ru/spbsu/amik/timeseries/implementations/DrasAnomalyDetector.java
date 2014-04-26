package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.AnomalyDetector;
import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Detector that implements DRAS algorithm of anomaly detection
 */
public class DrasAnomalyDetector implements AnomalyDetector {

    private double verticalBackgroundLevel;

    /** should be much more then localOverviewCount */
    private int globalOverviewCount;

    /** in interval (0, 1] */
    private double horizontalBackgroundLevel;

    public void setVerticalBackgroundLevel(double verticalBackgroundLevel) {
        this.verticalBackgroundLevel = verticalBackgroundLevel;
    }

    public void setGlobalOverviewCount(int globalOverviewCount) {
        this.globalOverviewCount = globalOverviewCount;
    }

    public void setHorizontalBackgroundLevel(double horizontalBackgroundLevel) {
        this.horizontalBackgroundLevel = horizontalBackgroundLevel;
    }

    @Override
    public List<Anomaly> detectAnomalies(Curve rectification) {

        List<Anomaly> anomalies = new ArrayList<Anomaly>();
        List<Point> points = rectification.getPoints();

        Point startAnomaly = null;
        Point endAnomaly = null;
        for (int i = 0 ; i < points.size(); i ++) {
            double leftMeasure = leftSideMeasure(verticalBackgroundLevel, globalOverviewCount, i, points);
            double rightMeasure = rightSideMeasure(verticalBackgroundLevel, globalOverviewCount, i, points);

            // potentially anomaly
            if (Math.min(leftMeasure, rightMeasure) < horizontalBackgroundLevel) {
                if (startAnomaly == null) {
                    startAnomaly = points.get(i);
                }
                endAnomaly = points.get(i);
            } else {
                if (startAnomaly != null)
                    anomalies.add(new Anomaly(startAnomaly.getTime(), endAnomaly.getTime(), Anomaly.AnomalyLevel.POTENTIAL));
                startAnomaly = null;
            }
        }

        return anomalies;
    }


    /** Measures ratio of rectifications witch level more then alpha on left of current point */
    private double leftSideMeasure(double alpha, int lyambda, int currentPoint, List<Point> rectification) {

        if (currentPoint < lyambda)
            lyambda = currentPoint;

        return intervalMeasure(alpha, lyambda, currentPoint, rectification, currentPoint - lyambda, currentPoint);
    }

    /** Measures ratio of rectifications witch level more then alpha on right of current point */
    private double rightSideMeasure(double alpha, int lyambda, int currentPoint, List<Point> rectification) {

        // ?? some errors can occur here
        if (rectification.size() - lyambda <= currentPoint)
            lyambda = rectification.size() - currentPoint - 1;

        return intervalMeasure(alpha, lyambda, currentPoint, rectification, currentPoint, currentPoint + lyambda);
    }

    /** Measures ratio of rectifications witch level more then alpha on interval */
    private double intervalMeasure(double alpha, int lyambda, int currentPoint, List<Point> rectification, int start, int end) {

        double numerator = 0;
        double denominator = 0;
        for (int i = start; i <= end; i++) {

            double weight = weightFunction(lyambda, currentPoint, i);
            if (rectification.get(i).getValue() <= alpha)
                numerator += weight;
            denominator += weight;
        }

        return denominator == 0 ? 0 : numerator / denominator;
    }

    /** used in one side measures */
    private double weightFunction (int globalOverviewCount, int currentPoint, int point) {

        return globalOverviewCount + 1 - Math.abs(point - currentPoint)
                      / (globalOverviewCount + 1);
    }
}
