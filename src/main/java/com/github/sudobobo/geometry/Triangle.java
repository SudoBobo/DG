package com.github.sudobobo.geometry;

import com.github.sudobobo.calculations.Value;
import lombok.Builder;
import lombok.Data;
import org.jblas.DoubleMatrix;

public
@Data
@Builder
class Triangle {

    private Value value;

    //    private final int number;
    private Point[] points;

    private DoubleMatrix A;
    private DoubleMatrix B;

    private DoubleMatrix AAbs;
    private DoubleMatrix AStr;
    private DoubleMatrix BStr;

    //    private double[] S;
    private double jacobian;

    //    private DoubleMatrix[] T;
//    private DoubleMatrix[] TInv;
    private DoubleMatrix Rpqn;

    private DoubleMatrix An;

    private Border[] borders;
    private int domain;

    // borders of triangle are indexed by j {0, 1, 2}
    // and each of this border contact with neighbour triangle's border
    // which also has an index in {0, 1, 2} - i
    // I[j] is the index of neighbour triangle's border which contact with j's border of the current triangle
    private int[] I;

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
            x += p.x;
            y += p.y;
        }

        x /= 3;
        y /= 3;

        return new Point(-1, new double[]{x, y});
    }

    public double getKsiInLocalSystem(double x, double y) {
        return
                ((points[2].x * points[0].y - points[0].x * points[2].y) +
                        x * (points[2].y - points[0].y) +
                        y * (points[0].x - points[2].x)) / jacobian;
    }

    public double getEtaInLocalSystem(double x, double y) {

        return
                ((points[0].x * points[1].y - points[1].x * points[0].y) +
                        x * (points[0].y - points[1].y) +
                        y * (points[1].x - points[0].x)) / jacobian;

    }

    // return points in global system
    public double getX(double ksi, double eta) {
        return points[0].x + (points[1].x - points[0].x) * ksi
                + (points[2].x - points[0].x) * eta;
    }

    public double getY(double ksi, double eta) {
        return points[0].y + (points[1].y - points[0].y) * ksi
                + (points[2].y - points[0].y) * eta;
    }

    public double x0() {
        return p(0).x;
    }

    public double x1() {
        return p(1).x;
    }

    public double x2() {
        return p(2).x;
    }

    public double y0() {
        return p(0).y;
    }

    public double y1() {
        return p(1).y;
    }

    public double y2() {
        return p(2).y;
    }

    // todo add test
    // todo to discus : why should we check if the triangle is inside IN INNER
    // triangle system, while I can be done in lab system as well ?


    // Translate a point (x, y) into a inner triangle coordinate system and see if
    // in this system the point(ksi, eta) is inside the triangle
    public boolean isInTriangle(double x, double y) {

        double f = (x0() - x) * (y1() - y0()) - (x1() - x0()) * (y0() - y);
        double s = (x1() - x) * (y2() - y1()) - (x2() - x1()) * (y1() - y);
        double t = (x2() - x) * (y0() - y2()) - (x0() - x2()) * (y2() - y);

        // if the point (x, y) is inside, than f, s, t should have the same sign
        // (or one of them should be nought, it is the case when point(x,y) lies on the edge
        double fine = 0.000001;

        // one of them is nought
        if ((Math.abs(f) < fine) || (Math.abs(s) < fine) || (Math.abs(t) < fine)) return true;

        // all of them have the same sign
        if ((f > 0 && s > 0 && t > 0) || (f < 0 && s < 0 && t < 0)) return true;

        // point is outside
        return false;
    }

    public int getIForFijFormula(int j) {
        return I[j];
    }

    public void setIJ() {
        I = new int[3];

        for (int j = 0; j < 3; j++) {

            Border neighborBorder = borders[j].getNeighborBorder();

            if (neighborBorder == null) {
                I[j] = -1;
            } else {
                I[j] = neighborBorder.getBorderNumber();
            }

        }
    }

    public DoubleMatrix getT(int j) {
        return borders[j].getT();
    }

    public DoubleMatrix getTInv(int j) {
        return borders[j].getTInv();
    }

    public double getS(int j) {
        return borders[j].getS();
    }
}
