package com.github.sudobobo;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Point;
import com.github.sudobobo.geometry.Triangle;
import lombok.Data;
import org.jblas.DoubleMatrix;

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

    public static Value[] makeValuesArray(Mesh mesh, Basis basis) {
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

            DoubleMatrix numericalU = calcInitialU(triangleCenter.x(), triangleCenter.y(), xWidth, yWidth, 1, R2, centerX, centerY);
            DoubleMatrix U = basis.calcUCoeffs(numericalU);

            values[t] = new Value(U, mesh.getTriangles()[t]);
        }

        return values;
    }

    public static DoubleMatrix calcInitialU(double x, double y,
                                            double xWidth, double yWidth, int amplitude, DoubleMatrix r2, double initialXCenter, double initialYCenter) {
        // TODO memorize this
        // TODO will not work on the border

        boolean is_x_inside = ((initialXCenter - xWidth) <= x) && (x <= (initialXCenter + xWidth));
        boolean is_y_inside = ((initialYCenter - yWidth) <= y) && (y <= (initialYCenter + yWidth));

        if (is_x_inside && is_y_inside) {
            DoubleMatrix centerVector = new DoubleMatrix(new double[]{
                    x / (2 * xWidth), y / (2 * yWidth)
            });

            DoubleMatrix k = new DoubleMatrix(new double[]{
                    Math.PI, 0
            });

            return r2.mmul(cos(k.dot(centerVector))).mul(-1);
        } else {
            return DoubleMatrix.zeros(r2.rows, r2.columns);
        }

    }
}
