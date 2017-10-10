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

    private Point p(int p) {
        return points[p];
    }

    public void setPoint(int p, Point newPoint) {
        points[p] = newPoint;
    }

    public Point getCenter() {

        double x = 0;
        double y = 0;

        for (Point p : points) {
            x += p.x();
            y += p.y();
        }

        x /= 3;
        y /= 3;

        return new Point(-1, new double[]{x, y});
    }

    public double getKsiInLocalSystem(double x, double y) {
        return
                ((points[2].x() * points[0].y() - points[0].x() * points[2].y()) +
                        x * (points[2].y() - points[0].y()) +
                        y * (points[0].x() - points[2].x())) / jacobian;
    }

    public double getEtaInLocalSystem(double x, double y) {

        return
                ((points[0].x() * points[1].y() - points[1].x() * points[0].y()) +
                        x * (points[0].y() - points[1].y()) +
                        y * (points[1].x() - points[0].x())) / jacobian;

    }

    public double getX(double ksi, double eta) {
        return points[0].x() + (points[1].x() - points[0].x()) * ksi
                + (points[2].x() - points[0].x()) * eta;
    }

    public double getY(double ksi, double eta) {
        return points[0].y() + (points[1].y() - points[0].y()) * ksi
                + (points[2].y() - points[0].y()) * eta;
    }

    public double x0(){ return p(0).x();}
    public double x1(){ return p(1).x();}
    public double x2(){ return p(2).x();}

    public double y0() { return p(0).y();}
    public double y1() { return p(1).y();}
    public double y2() { return p(2).y();}

    // todo add test
    public boolean isInTriangle(double x, double y) {

        double f = (x0() - x) * (y1() - y0()) - (x1() - x0()) * (y0() - y);
        double s = (x1() - x) * (y2() - y1()) - (x2() - x1()) * (y1() - y);
        double t = (x2() - x) * (y0() - y2()) - (x0() - x2()) * (y2() - y);

        // if the point (x, y) is inside, than f, s, t should have the same sign
        // (or one of them should be nought, it is the case when point(x,y) lies on the edge
        double fine = 0.000001;

        // one of them is nought
        if ((f < fine) || (s < fine) || (t < fine)) return true;

        // all of them have the same sign
        if ((f > 0 && s > 0 && t > 0) || (f < 0 && s < 0 && t < 0)) return true;

        // point is outside
        return false;
    }

}
