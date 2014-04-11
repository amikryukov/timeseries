package ru.spbsu.amik.timeseries.draw;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.awt.image.BufferedImage;
import javax.swing.*;

public class GraphingData {

    public static void main(String[] args) {


        Curve curve = new TimeSeriesGenerator(99).generateRandomEqualStepSeries(
                "Title",
                200, // count
                100, // step
                0,   // start time
                555  // color
        );


        XYSeries series = translateToJFree(curve);

        XYDataset xyDataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                curve.getTitle(),
                "X-label",
                "Y-label",
                xyDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                true
        );


        XYPlot plot = chart.getXYPlot();
        plot.getRangeAxis().setLowerBound(series.getMinY());

        BufferedImage image = chart.createBufferedImage(1100,500);

        JLabel lblChart = new JLabel();
        lblChart.setIcon(new ImageIcon(image));

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(lblChart);
        f.setSize(1100, 550);
        f.setLocation(200,200);
        f.setVisible(true);
    }

    private static XYSeries translateToJFree(Curve curve) {

        XYSeries result = new XYSeries(curve.getTitle());
        for (Point point : curve.getPoints()) {
            result.add( point.getTime(), point.getValue());
        }
        return result;
    }
}