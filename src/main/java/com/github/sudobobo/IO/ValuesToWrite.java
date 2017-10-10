package com.github.sudobobo.IO;

import com.github.sudobobo.Value;
import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Point;
import lombok.Builder;
import lombok.Data;

public class ValuesToWrite {

    // should be stored sorted from left bottom to top right
    private ValueToWrite [] valuesToWrite;
    private Basis basis;

    public ValuesToWrite (Value [] associatedValues, double rectangleSideLength, double minTriangleSideLength, Point lt,
                          Point rb, Basis basis){

        this.basis = basis;
        valuesToWrite = createEmptyValuesToWrite(rectangleSideLength, lt, rb);
        connectValuesToWriteWithValues(valuesToWrite, associatedValues);
    }

    private ValueToWrite[] createEmptyValuesToWrite(double rectangleSideLength, Point lt, Point rb) {

        // assume square mesh
        boolean isSquare = (Math.abs((rb.x() - lt.x()) - (lt.y() - rb.y())) < 0.001);
        assert (isSquare) : "Mesh expected to be square. It is not";

        double meshSideLength = rb.x() - lt.x();

        int numberOfRectanglesOnSide = (int) (meshSideLength / rectangleSideLength);

        int numberOfValuesToWrite = numberOfRectanglesOnSide * numberOfRectanglesOnSide;
        valuesToWrite = new ValueToWrite[numberOfValuesToWrite];

        double x = 0;
        double y = 0;
        int v = 0;

        for (int column = 0; column < numberOfRectanglesOnSide; column++){

            y = (rectangleSideLength / 2) * (column + 1);

            for (int row = 0; row < numberOfRectanglesOnSide; row++){

                x = (rectangleSideLength / 2) * (row + 1);

                Point valueRectangleCenter = new Point(-1, new double[]{x, y});
                valuesToWrite[v] = ValueToWrite.builder().valueRectangleCenter(valueRectangleCenter).build();
                v++;
            }
        }

        return valuesToWrite;
    }

    private void connectValuesToWriteWithValues(ValueToWrite[] valuesToWrite, Value[] associatedValues) {


        assert (false) : "Not implemented yet!";
        //todo rewrite this O(m*n) algorithm (Also may work wrong)

        for (ValueToWrite vtw : valuesToWrite){

            for (Value asv : associatedValues){

                // ???
                if (true){
                    break;
                }
            }
        }

    }

    public Long [] getExtent(double rectangleSideLength, Point lt, Point rb){

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

    public double [] getRawValuesToWrite(){

        // todo rewrite with use of stream api

        // expect them to be in order of creation in createEmptyValuesToWrite()

        // paraview expect input data to be like:
        // u11 u12 u13 u14 u15 u21 u22 u23 u24 u25
        // where first index stays for point number
        //       second - for value in value vector

        int numberOfValuesInValueVector = valuesToWrite[0].associatedValue.u.getRows();
        assert (numberOfValuesInValueVector == 5);
        int rawSize = valuesToWrite.length * numberOfValuesInValueVector;

        double [] raw = new double[rawSize];

        for (int v = 0; v < valuesToWrite.length; v++) {

            double [] u = basis.calcUNumerical(valuesToWrite[v].associatedValue.u, valuesToWrite[v].valueRectangleCenter);

            for (int r = 0; r < numberOfValuesInValueVector; r++ ){
                raw[ numberOfValuesInValueVector * v + r ] = u[r];
            }
        }

        return raw;
    }

    private @Data @Builder class ValueToWrite {
        Value associatedValue;
        Point valueRectangleCenter;
    }

}

