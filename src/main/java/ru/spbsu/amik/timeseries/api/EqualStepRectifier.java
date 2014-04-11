package ru.spbsu.amik.timeseries.api;

import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * To rectify all curve to it rectification.
 */
public class EqualStepRectifier implements GlobalRectifier {

    private Rectifier localRectifier;
    private int position;

    public void setLocalRectifier(Rectifier localRectifier) {
        this.localRectifier = localRectifier;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public Curve rectify(Curve curve) {

        Curve result = new Curve(curve.getTitle(), curve.getColor());
        int count = curve.getPoints().size();
        if (count < 2 * position + 1) {
            throw new IllegalArgumentException("not enough points " + count + " for rectifying with " + this.toString());
        }

        List<Point> fragment = new ArrayList<Point>(position * 2 + 1);

        for (int i = position; i < count - position; i ++) {
            for (int j = 0; j < 2 * position + 1; j ++) {
                fragment.add(j, curve.getPoints().get(i - position + j));
            }
            long currentTime = curve.getPoints().get(i).getTime();
            result.addPoint(new Point(currentTime, localRectifier.rectify(fragment)));
            fragment.clear();
        }

        return result;
    }

    @Override
    public String toString() {
        return "EqualStepRectifier{" +
                "localRectifier=" + localRectifier +
                ", position=" + position +
                '}';
    }
}
