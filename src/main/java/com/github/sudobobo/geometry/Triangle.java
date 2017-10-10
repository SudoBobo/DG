package com.github.sudobobo.geometry;

import lombok.Builder;
import lombok.Data;
import org.jblas.DoubleMatrix;

public
@Data
@Builder
class Triangle {
    private final int number;
    private Point[] points;

    private DoubleMatrix A;
    private DoubleMatrix B;

    private DoubleMatrix AAbs;
    private DoubleMatrix AStr;
    private DoubleMatrix BStr;

    private double[] S;
    private double jacobian;

    private DoubleMatrix[] T;
    private DoubleMatrix[] TInv;
    private DoubleMatrix Rpqn;

    private DoubleMatrix An;

    private Border[] borders;
    private int domain;

    public Point getPoint(int p) {
        return points[p];
    }

    public void setPoint(int p, Point newPoint){
        points[p] = newPoint;
    }

    public Point getCenter() {

        double x = 0;
        double y = 0;

        for (Point p : points){
            x += p.x();
            y += p.y();
        }

        x /= 3;
        y /= 3;

        return new Point(-1, new double[]{x,y});
    }

    public double getKsiInLocalSystem(double x, double y) {
        return
                ((points[2].x() * points[0].y()  -  points[0].x() * points[2].y()) +
                x * (points[2].y() - points[0].y()) +
                y * (points[0].x() - points[2].x())) / jacobian;
    }

    public double getEtaInLocalSystem(double x, double y) {

        return
                ((points[0].x() * points[1].y()  -  points[1].x() * points[0].y()) +
                        x * (points[0].y() - points[1].y()) +
                        y * (points[1].x() - points[0].x())) / jacobian;

    }

    public double getX(double ksi, double eta) {
        return points[0].x() + (points[1].x() - points[0].x()) * ksi
                + (points[2].x() - points[0].x()) * eta;
    }

    public double getY(double ksi, double eta){
        return points[0].y() + (points[1].y() - points[0].y()) * ksi
                + (points[2].y() - points[0].y()) * eta;
    }
}
