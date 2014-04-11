package ru.spbsu.amik.timeseries.draw;

import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.Random;

public class TimeSeriesGenerator {

    private Random random;

    public TimeSeriesGenerator(long randomSeed) {
        random = new Random(randomSeed);
    }

    public TimeSeriesGenerator() {
        random = new Random();
    }


    public Curve generateRandomEqualStepSeries (String title, int count, long step, long startTime, int color) {
        Curve curve = new Curve(title, color);

        for (int i = 0; i < count; i++) {
            double value = random.nextDouble();
            if (value < 0.05)
                value += 5 * random.nextDouble();
            Point point = new Point(startTime + i * step, value);
            curve.addPoint(point);
        }
        return curve;
    }
}
