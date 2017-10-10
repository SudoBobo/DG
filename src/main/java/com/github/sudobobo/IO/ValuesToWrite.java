package com.github.sudobobo.IO;

import com.github.sudobobo.Value;
import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Point;
import lombok.Builder;
import lombok.Data;

public class ValuesToWrite {

    // should be stored sorted from left bottom to top right
    private ValueToWrite[] valuesToWrite;
    // we have 'basis' field here because it is the only place where we calculate numerical vector of values from the matrix of coefficients
    // in all other places we opearate only with the matrix of coefficients
    private Basis basis;

    public ValuesToWrite(Value[] associatedValues, double rectangleSideLength, double minTriangleSideLength, Point lt,
                         Point rb, Basis basis) {

        boolean isSquare = (Math.abs((rb.x() - lt.x()) - (lt.y() - rb.y())) < 0.001);
        assert (isSquare) : "Mesh expected to be square. It is not";

        double meshSideLength = rb.x() - lt.x();

        this.basis = basis;

        int numberOfRectanglesOnSide = (int) (meshSideLength / rectangleSideLength);
        int valuesToWriteSize = (int) Math.pow(numberOfRectanglesOnSide, 2);
        valuesToWrite = new ValueToWrite[valuesToWriteSize];

        double x = 0;
        double y = 0;
        int v = 0;

        for (int column = 0; column < numberOfRectanglesOnSide; column++) {

            y = (rectangleSideLength / 2) * (column + 1);

            for (int row = 0; row < numberOfRectanglesOnSide; row++) {

                x = (rectangleSideLength / 2) * (row + 1);

                valuesToWrite[v] = new ValueToWrite(FindAssociatedValue(x, y, associatedValues));
                v++;
            }
        }

    }

    private Value FindAssociatedValue(double x, double y, Value[] associatedValues) {
        for (Value v : associatedValues) {
            if (v.getAssociatedTriangle().isInTriangle(x, y)) {
                return v;
            }
        }


        assert (false) : "Failed to find triangle for this point";
        return null;
    }


    public Long[] getExtent(double rectangleSideLength, Point lt, Point rb) {

        // 0 100 0 100 0 1
        // expect array of 3 values [100, 100, 1]
        // these values are numbers of dots/rectangles in final vtk mesh

        boolean isSquare = (Math.abs((rb.x() - lt.x()) - (lt.y() - rb.y())) < 0.001);
        assert (isSquare) : "Mesh expected to be square. It is not";

        double meshSideLength = rb.x() - lt.x();

        int numberOfRectanglesOnSide = (int) (meshSideLength / rectangleSideLength);

        long xExtent = (long) numberOfRectanglesOnSide;
        long yExtent = (long) numberOfRectanglesOnSide;
        long zExtent = 1L;

        return new Long[]{xExtent, yExtent, zExtent};

    }

    public double[] getRawValuesToWrite() {

        // todo rewrite with use of stream api

        // expect them to be in order of creation in createEmptyValuesToWrite()

        // paraview expect input data to be like:
        // u11 u12 u13 u14 u15 u21 u22 u23 u24 u25
        // where first index stays for point number
        //       second - for value in value vector

        int numberOfValuesInValueVector = valuesToWrite[0].associatedValue.u.getRows();
        assert (numberOfValuesInValueVector == 5);
        int rawSize = valuesToWrite.length * numberOfValuesInValueVector;

        double[] raw = new double[rawSize];

        for (int v = 0; v < valuesToWrite.length; v++) {

            double[] u = basis.calcUNumerical(valuesToWrite[v].associatedValue.u, valuesToWrite[v].valueRectangleCenter);

            for (int r = 0; r < numberOfValuesInValueVector; r++) {
                raw[numberOfValuesInValueVector * v + r] = u[r];
            }
        }

        return raw;
    }

    private
    @Data
    @Builder
    class ValueToWrite {
        Value associatedValue;
    }

}

