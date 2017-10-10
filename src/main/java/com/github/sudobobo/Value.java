package com.github.sudobobo;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Point;
import com.github.sudobobo.geometry.Triangle;
import lombok.Data;
import org.jblas.DoubleMatrix;

import java.util.function.BiFunction;

import static java.lang.Math.cos;

public
@Data
class Value {
    public DoubleMatrix u;
    private Triangle associatedTriangle;

    public Value(DoubleMatrix u, Triangle associatedTriangle) {
        this.u = u;
        this.associatedTriangle = associatedTriangle;
    }

    public static Value[] makeValuesArray(Mesh mesh, BiFunction initialCondition, Basis basis) {
        // for all triangles produce associated u matrixes

        Value[] values = new Value[mesh.getTriangles().length];

        Point lt = mesh.getLTPoint();
        Point rb = mesh.getRBPoint();

        double centerX = (lt.x() + rb.x()) / 2;
        double centerY = (lt.y() + rb.y()) / 2;

        // 0.5 - will make initial condition all along the axis
        // 0.25 - on a half
        // 0.125 - on quarter
        // 0.0625 - 1/8

        double xWidthCoef = 0.00625;
        double xWidth = (rb.x() - lt.x()) * xWidthCoef;

        double yWidthCoef = 0.5;
        double yWidth = (lt.y() - rb.y()) * yWidthCoef;

        DoubleMatrix R2 = mesh.getTriangles()[0].getRpqn().getColumn(1);

        for (int t = 0; t < mesh.getTriangles().length; t++) {

            Point triangleCenter = mesh.getTriangles()[t].getCenter();

            // this part should be re-writed
//            DoubleMatrix numericalU = calcInitialU(triangleCenter.x(), triangleCenter.y(), xWidth, yWidth, 1, R2, centerX, centerY);
//            DoubleMatrix U = basis.calcUCoeffs(numericalU, mesh.getTriangles()[t]);

            DoubleMatrix u = basis.calcUCoeffs(initialCondition, t);
            values[t] = new Value(u, mesh.getTriangles()[t]);
        }

        return values;
    }

    public static void copyU(Value [] from, Value [] to){

        assert (from.length == to.length);
        for (int v = 0; v < from.length; v++){

        }
    }
}
