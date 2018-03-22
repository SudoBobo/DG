package com.github.sudobobo.IO;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.calculations.Value;
import com.github.sudobobo.geometry.Point;
import lombok.Data;

@Data
public class ValuesToWrite {

    // should be stored sorted from left bottom to top right
    private ValueToWrite[] valuesToWrite;
    // we have 'basis' field here because it is the only place where we calculate numerical vector of values from the matrix of coefficients
    // in all other places we opearate only with the matrix of coefficients
    private Basis basis;

    public ValuesToWrite(Value[] associatedValues, double rectangleSideLength, double minTriangleSideLength, Point lt,
                         Point rb, Basis basis) {

        boolean isSquare = (Math.abs((rb.x - lt.x) - (lt.y - rb.y)) < 0.001);
        assert (isSquare) : "Mesh expected to be square. It is not";

        double meshSideLength = rb.x - lt.x;

        this.basis = basis;

        int numberOfRectanglesOnSide = (int) (meshSideLength / rectangleSideLength);
        int valuesToWriteSize = (int) (Math.pow(numberOfRectanglesOnSide - 1, 2));
        valuesToWrite = new ValueToWrite[valuesToWriteSize];

        double x = lt.x + (rectangleSideLength / 2);
        double y = rb.y + (rectangleSideLength / 2);
        int v = 0;

        for (int row = 0; row < numberOfRectanglesOnSide - 1; row++) {
            for (int column = 0; column < numberOfRectanglesOnSide - 1; column++) {
                Value associatedValue = FindAssociatedValue(x, y, associatedValues, v);

                if (associatedValue == null) {
                    throw new NullPointerException(
                            String.format("Can't find associated value for valueToWrite with coordinates x= %f , y= %f", x, y));
                }

                valuesToWrite[v] = new ValueToWrite(FindAssociatedValue(x, y, associatedValues, v));
                v++;
                x += rectangleSideLength;
            }
            y += rectangleSideLength;
            x = lt.x + (rectangleSideLength / 2);
        }
    }

    // for each triangle we translate a point (x, y) into a inner triangle coordinate system and see if
    // in this system point(ksi, eta) is inside the triangle
    private Value FindAssociatedValue(double x, double y, Value[] associatedValues, int valueNumber) {
        for (Value v : associatedValues) {
            if (v.getAssociatedTriangle().isInTriangle(x, y)) {
                return v;
            }
        }
        return null;
    }

    public Long[] getExtent(double rectangleSideLength, Point lt, Point rb) {

        // 0 100 0 100 0 1
        // expect array of 3 values [100, 100, 1]
        // these values are numbers of dots/rectangles in final vtk mesh

        boolean isSquare = (Math.abs((rb.x - lt.x) - (lt.y - rb.y)) < 0.001);
        assert (isSquare) : "Mesh expected to be square. It is not";

//        double meshSideLength = rb.x() - lt.x();
//
//        int numberOfRectanglesOnSide = (int) (meshSideLength / rectangleSideLength);

//        long xExtent = (long) numberOfRectanglesOnSide;
//        long yExtent = (long) numberOfRectanglesOnSide;
//        long zExtent = 1L;

        long extent = (long) Math.sqrt(valuesToWrite.length);
        long xExtent = extent;
        long yExtent = extent;
        long zExtent = 1L;

        return new Long[]{xExtent, yExtent, zExtent};

    }

    public double[] getRawValuesToWrite() {

        // todo rewrite with use of stream api (creating all these array are not nessesary)

        // paraview expect input data to be like:
        // u11 u12 u13 u14 u15 u21 u22 u23 u24 u25
        // where first index stays for point number
        //       second - for value in value vector

        int numberOfValuesInValueVector = valuesToWrite[0].associatedValue.u.getRows();
        assert (numberOfValuesInValueVector == 5);

        int rawSize = valuesToWrite.length * numberOfValuesInValueVector;

        double[] raw = new double[rawSize];


        for (int v = 0; v < valuesToWrite.length; v++) {
            if (valuesToWrite[v] == null){
                assert (false) : "valueToWrite " + Integer.toString(v) + " is null";
            }
            if (valuesToWrite[v].associatedValue == null){
                assert (false) : "one of values to write is not associated with any value";
            }

            if (valuesToWrite[v].associatedValue.getAssociatedTriangle() == null){
                assert (false) : "one of values is not associated with any triangle";
            }

            double[] u = basis.calcUNumerical(valuesToWrite[v].associatedValue.u, valuesToWrite[v].associatedValue.getAssociatedTriangle());

            for (int r = 0; r < numberOfValuesInValueVector; r++) {
                raw[numberOfValuesInValueVector * v + r] = u[r];
            }
        }

        return raw;
    }

    public double[] getRawTrianglesToDomains() {
        int rawSize = valuesToWrite.length;
        double[] raw = new double[rawSize];

        for (int v = 0; v < valuesToWrite.length; v++) {
            raw[v] = valuesToWrite[v].associatedValue.getAssociatedTriangle().
                getDomain().getIndex();
        }

        return raw;
    }
}
