package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.Rectifier;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.List;

/**
 * Should be used only with the same time steps for all values
 */
public class FragmentEnergyRectifier implements Rectifier {

    private long stepLength;
    private long surveyLength;

    public FragmentEnergyRectifier(long stepLength, long surveyLength) {
        this.stepLength = stepLength;
        this.surveyLength = surveyLength;
    }

    @Override
    public double rectify(List<Point> survey) {

        double weightedSum = getWeightedSum(survey);
        double result = 0;
        for (Point point : survey) {
            result += Math.pow(point.getValue() - weightedSum, 2);
        }

        return result;
    }

    private double getWeightedSum(List<Point> survey) {

        double sum = 0;
        for (Point point : survey) {
            sum += point.getValue();
        }

        return stepLength / (stepLength + 2 * surveyLength) * sum;
    }
}
