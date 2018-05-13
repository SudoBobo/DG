package com.github.sudobobo.calculations;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Mesh;
import com.github.sudobobo.geometry.Triangle;
import com.github.sudobobo.meshconstruction.InitialConditionConfig;
import com.github.sudobobo.meshconstruction.PhysicalAttributesMatrixes;
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

    // 'initialCondition' is expected to be a simple f(x,y,z) 'physical' function
    public static Value[] makeValuesArray(Mesh mesh, InitialConditionConfig initialCondition, Basis basis) {
        // for all triangles produce associated 'U' (u_p_l) matrices
        Value[] values = new Value[mesh.getTriangles().length];

        // 0.5 - will make initial condition all along the axis
        // 0.25 - on a half
        // 0.125 - on quarter
        // 0.0625 - 1/8

//        double xWidthCoef = 0.125;
//        double yWidthCoef = 0.5;

        assert (initialCondition.equals("cos"));
//        InitialConditionPhase initialConditionPhase =
//            buildSampleInitialConditionPhaseFunction(mesh.getLTPoint(),
//                mesh.getRBPoint(), xWidthCoef, yWidthCoef);
        InitialConditionPhase initialConditionPhase =
            buildInitialConditionPhaseFunction(initialCondition);

        // initialConditionAmplitude
//        DoubleMatrix R2 = mesh.getTriangles()[0].getRpqn().getColumn(1);
//        // todo
////        double mu = 1.0;
////        double cS = 1.0;
//        R2 = new DoubleMatrix(new double[][]{
//            {4},
//            {2},
//            {0},
//            {2},
//            {0}
//        });


        // u_p = R2_p * initialConditionPhase(x, y)
        // where initialConditionPhase is scalar function
        for (int t = 0; t < mesh.getTriangles().length; t++) {
            // todo easy to make more efficien
            DoubleMatrix PWaveAmpl =
                PhysicalAttributesMatrixes.calcRpqn(
                    mesh.getTriangles()[t].getDomain().getLambda(),
                    mesh.getTriangles()[t].getDomain().getMu(),
                    mesh.getTriangles()[t].getDomain().getCp(),
                    mesh.getTriangles()[t].getDomain().getCs(),
                    initialCondition.getDX(),
                    initialCondition.getDY()
                    ).getColumn(0)
                ;
            PWaveAmpl = PWaveAmpl.mul(initialCondition.getAmplitude());

                DoubleMatrix u = basis.calcUCoeffs(initialConditionPhase, PWaveAmpl, mesh.getTriangles()[t]);
            values[t] = new Value(u, mesh.getTriangles()[t]);
            mesh.getTriangles()[t].setValue(values[t]);
        }

        return values;
    }

    private static InitialConditionPhase buildInitialConditionPhaseFunction(InitialConditionConfig initialConditionConfig) {
        switch (initialConditionConfig.getProfile()) {
            case "cos":
                return new CosInitialConditionPhase(initialConditionConfig);
            default:
                throw new RuntimeException("Unknown initial condition profile, " +
                    "check config.yml");
        }
    }


    public static Value[] makeBufferValuesArray(Mesh mesh, Basis basis) {

        int rows = mesh.getTriangles()[0].getRpqn().rows;
        int columns = basis.getNumberOfBasisFunctions();

        Value[] values = new Value[mesh.getTriangles().length];
        for (int t = 0; t < mesh.getTriangles().length; t++) {

            DoubleMatrix u = DoubleMatrix.zeros(rows, columns);
            values[t] = new Value(u, mesh.getTriangles()[t]);
        }

        return values;

    }

//    private static InitialConditionPhase buildSampleInitialConditionPhaseFunction(Point lt, Point rb, double xWidthCoef,
//                                                                                  double yWidthCoef) {
//
//        // todo remove hardcode : f(x,y) = cos(a * x + phi)
//        // where f(x=xCenter) = 1
//        //       f(x=xCenter +/- xWidth = 0
//        double centerX = (lt.x + rb.x) / 2;
//        double centerY = (lt.y + rb.y) / 2;
//
//        double xWidth = (rb.x - lt.x) * xWidthCoef;
//        double yWidth = (lt.y - rb.y) * yWidthCoef;
//
//
//        double a = Math.PI / (2.0 * xWidth);
//        double phi = -(Math.PI / 2) * (centerX / xWidth);
////
////      return Math.cos(a * x + phi);
//        return new CosInitialConditionPhase(a, phi, xWidth, yWidth, centerX, centerY);
//    }

    // method expects 'to' to have 'U' DoubleMatrixes
    public static void copyU(Value [] from, Value [] to){

        assert (from.length == to.length);
        for (int v = 0; v < from.length; v++){

            to[v].getU().copy(
                    from[v].getU()
            );
        }
    }
}
