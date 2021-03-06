package com.github.sudobobo.geometry;

import com.github.sudobobo.calculations.Value;
import lombok.Builder;
import lombok.Data;
import org.jblas.DoubleMatrix;

import java.util.ArrayList;
import java.util.List;

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
    private DoubleMatrix[] RpqnJ;

    private DoubleMatrix An;
//    private DoubleMatrix[] Anj;
//    private DoubleMatrix[] AAbsJ;

    private Border[] borders;
    private Domain domain;
    private List<PointSource> pointSources;

    // borders of triangle are indexed by j {0, 1, 2}
    // and each of this border contact with neighbour triangle's border
    // which also has an index in {0, 1, 2} - i
    // I[j] is the index of neighbour triangle's border which contact with j's border of the current triangle
    private int[] I;

    private double x1_x0;
    private double x2_x0;

    private double y1_y0;
    private double y2_y0;

    private double y2_y1;
    private double x2_x1;

    private double ksiTranslationCoef;
    private double etaTranslationCoef;

    private Point center;

    public Point getPoint(int p) {
        return points[p];
    }

    private Point p(int p) {
        return points[p];
    }

    public void setPoint(int p, Point newPoint) {
        points[p] = newPoint;
    }

    public void setTranslationCoefs(){
        x1_x0 = points[1].x - points[0].x;
        x2_x0 = points[2].x - points[0].x;
        x2_x1 = points[2].x - points[1].x;

        y1_y0 = points[1].y - points[0].y;
        y2_y0 = points[2].y - points[0].y;
        y2_y1 = points[2].y - points[1].y;

        ksiTranslationCoef = points[2].x * points[0].y - points[0].x * points[2].y;
        etaTranslationCoef = points[0].x * points[1].y - points[1].x * points[0].y;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter() {
        double x = 0;
        double y = 0;
        double z = 0;

        for (Point p : points) {
            x += p.x;
            y += p.y;
            z += p.z;
        }

        x /= 3;
        y /= 3;
        z /= 3;

        center = new Point(-1, new double[]{x, y, z}, -1);
    }

    public double getKsiInLocalSystem(double x, double y) {
//        return
//                ((points[2].x * points[0].y - points[0].x * points[2].y) +
//                        x * (points[2].y - points[0].y) +
//                        y * (points[0].x - points[2].x)) / jacobian;

                return
                ((ksiTranslationCoef) +
                        x * (y2_y0) +
                        y * (-x2_x0)) / jacobian;

    }

    public double getEtaInLocalSystem(double x, double y) {

//        return
//                ((points[0].x * points[1].y - points[1].x * points[0].y) +
//                        x * (points[0].y - points[1].y) +
//                        y * (points[1].x - points[0].x)) / jacobian;

        return
                ((etaTranslationCoef) +
                        x * (-y1_y0) +
                        y * (x1_x0)) / jacobian;

    }

    // return points in global system
    public double getX(double ksi, double eta) {
//        return points[0].x + (points[1].x - points[0].x) * ksi
//                + (points[2].x - points[0].x) * eta;
        return points[0].x + x1_x0 * ksi
                + x2_x0 * eta;
    }

    public double getY(double ksi, double eta) {
//        return points[0].y + (points[1].y - points[0].y) * ksi
//                + (points[2].y - points[0].y) * eta;

        return points[0].y + y1_y0 * ksi
                + y2_y0 * eta;
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

        double f = (x0() - x) * (y1_y0) - (x1_x0) * (y0() - y);
        double s = (x1() - x) * (y2_y1) - (x2_x1) * (y1() - y);
        double t = (x2() - x) * (-y2_y0) - (-x2_x0) * (y2() - y);

        // if the point (x, y) is inside, than f, s, t should have the same sign
        // (or one of them should be nought, it is the case when point(x,y) lies on the edge
        double fine = 0.00000000000000000001;

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

    public void addPointSource(PointSource s) {
        if (pointSources == null) {
            pointSources = new ArrayList<PointSource>();
        }
        pointSources.add(s);
    }

    public boolean hasStaticSource() {
        return (pointSources != null);
    }

    public PointSource[] getStaticPointSources() {
        return (PointSource[]) pointSources.toArray(new PointSource[pointSources.size()]);

    }
}
