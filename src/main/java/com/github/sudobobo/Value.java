package com.github.sudobobo;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.calculations.InitialConditionPhase;
import com.github.sudobobo.calculations.SinInitialConditionPhase;
import com.github.sudobobo.geometry.Point;
import com.github.sudobobo.geometry.Triangle;
import lombok.Data;
import org.jblas.DoubleMatrix;

public
@Data
class Value {
    public DoubleMatrix u;
    private Triangle associatedTriangle;

    public Value(DoubleMatrix u, Triangle associatedTriangle) {
        this.u = u;
        this.associatedTriangle = associatedTriangle;
    }

    // This is the actual place, were we apply 'initialCondition' function and 'basis'
    // to calculate initial coefficients of all cells (triangles)

    // 'initialCondition' is expected to be a simple f(x,y) 'physics' function
    public static Value[] makeValuesArray(Mesh mesh, String initialCondition, Basis basis) {
        // for all triangles produce associated 'U' matrices
        Value[] values = new Value[mesh.getTriangles().length];

        // 0.5 - will make initial condition all along the axis
        // 0.25 - on a half
        // 0.125 - on quarter
        // 0.0625 - 1/8

        double xWidthCoef = 0.00625;
        double yWidthCoef = 0.5;

        assert (initialCondition.equals("sin"));
        InitialConditionPhase initialConditionPhase =
                buildSampleInitialConditionPhaseFunction(mesh.getLTPoint(), mesh.getRBPoint(), xWidthCoef, yWidthCoef);

        // initialConditionAmplitude
        DoubleMatrix R2 = mesh.getTriangles()[0].getRpqn().getColumn(1);

        // u_p = R2_p * initialConditionPhase(x, y)
        // where initialConditionPhase is scalar function
        for (int t = 0; t < mesh.getTriangles().length; t++) {

            DoubleMatrix u = basis.calcUCoeffs(initialConditionPhase, R2, mesh.getTriangles()[t]);
            values[t] = new Value(u, mesh.getTriangles()[t]);
        }

        return values;
    }

    private static InitialConditionPhase buildSampleInitialConditionPhaseFunction(Point lt, Point rb, double xWidthCoef,
                                                                                  double yWidthCoef) {

        // todo remove hardcode : f(x,y) = cos(a * x + phi)
        // where f(x=xCenter) = 1
        //       f(x=xCenter +/- xWidth = 0
        double centerX = (lt.x() + rb.x()) / 2;
        double centerY = (lt.y() + rb.y()) / 2;

        double xWidth = (rb.x() - lt.x()) * xWidthCoef;
        double yWidth = (lt.y() - rb.y()) * yWidthCoef;


        double a = Math.PI / (2.0 * xWidth);
        double phi = -(Math.PI / 2) * (centerX / xWidth);
//
//      return Math.cos(a * x + phi);
        return new SinInitialConditionPhase(a, phi, xWidth, yWidth, centerX, centerY);
    }

//    public static void copyU(Value [] from, Value [] to){
//
//        assert (from.length == to.length);
//        for (int v = 0; v < from.length; v++){
//
//        }
//    }
}
