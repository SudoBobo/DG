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
}
