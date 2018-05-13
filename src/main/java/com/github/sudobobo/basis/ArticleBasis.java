package com.github.sudobobo.basis;
//
// F[j][i]
// j stands for border number in considered triangle
// i stands for border number in neighbour triangle
//

import com.github.sudobobo.calculations.InitialConditionPhase;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;

public class ArticleBasis implements Basis {
    private Function zeroFunction;
    private Function[] basisFunctions;
    private Function[] squaredBasisFunctions;
    private int numberOfBasisFunctions;
    private double integrationStep;

    private DoubleMatrix M;
    private DoubleMatrix[] F0;
    private DoubleMatrix[][] F;
    private DoubleMatrix KKsi;
    private DoubleMatrix KEta;

    public ArticleBasis(double integrationStep) {
        this.integrationStep = integrationStep;
        initBasisFunctions();
        numberOfBasisFunctions = 3;

        M = new DoubleMatrix(new double[][]{
            {1.0 / 2.0, 0,          0},
            {0,         1.0 / 12.0, 0},
            {0,         0,          1.0 / 4.0}
        });

        F0 = new DoubleMatrix[3];

        F0[0] = new DoubleMatrix(new double[][]{
            { 1, 0,        -1},
            { 0, 1.0/3.0,   0},
            {-1, 0,         1}
        });

        F0[1] = new DoubleMatrix(new double[][]{
            {1,           1.0/2.0,      1.0/2.0},
            {1.0/2.0,     1.0/3.0,      0},
            {1.0/2.0,     0,            1}
        });

        F0[2] = new DoubleMatrix(new double[][]{
            {  1,             -(1.0 / 2.0),    1.0 / 2.0},
            {-(1.0 / 2.0),      1.0 / 3.0,     0},
            {  1.0 / 2.0,       0,             1}
        });

        F = new DoubleMatrix[3][3];

        //
        // F[j][i]
        // j stands for border number in considered triangle
        // i stands for border number in neighbour triangle
        //

        // j = 0

        F[0][0] = new DoubleMatrix(new double[][]{
            { 1.0,   0,       -1.0},
            { 0,   -(1.0/3.0), 0},
            {-1.0,   0,        1}
        });

        F[0][1] = new DoubleMatrix(new double[][]{
            { 1.0,     1.0/2.0,       1.0/2.0},
            { 0,      (1.0/6.0),    -(1.0/2.0)},
            {-1.0,   -(1.0/2.0),    -(1.0/2.0)}
        });

        F[0][2] = new DoubleMatrix(new double[][]{
            { 1.0,   -(1.0/2.0),       1.0/2.0},
            { 0,      (1.0/6.0),       1.0/2.0},
            {-1.0,     1.0/2.0,      -(1.0/2.0)}
        });

        ///

        F[1][0] = new DoubleMatrix(new double[][]{
            { 1.0,        0,         -1.0},
            { 1.0/2.0,   (1.0/6.0), -(1.0/2.0)},
            { 1.0/2.0,  -(1.0/2.0), -(1.0/2.0)}
        });

        F[1][1] = new DoubleMatrix(new double[][]{
            { 1.0,         1.0/2.0,   1.0/2.0},
            { 1.0/2.0,     1.0/6.0,   1.0/2.0},
            { 1.0/2.0,     1.0/2.0, -(1.0/2.0)}
        });

        F[1][2] = new DoubleMatrix(new double[][]{
            { 1.0,       -(1.0/2.0), 1.0/2.0},
            { 1.0/2.0,   -(1.0/3.0), 0},
            { 1.0/2.0,     0,        1}
        });

        ///

        F[2][0] = new DoubleMatrix(new double[][]{
            {   1.0,        0,       -1.0},
            { -(1.0/2.0),   1.0/6.0,  1.0/2.0},
            {   1.0/2.0,    1.0/2.0, -1.0/2.0}
        });

        F[2][1] = new DoubleMatrix(new double[][]{
            {   1.0,          1.0/2.0,  1.0/2.0},
            { -(1.0/2.0),   -(1.0/3.0), 0},
            {   1.0/2.0,      0,        1}
        });

        F[2][2] = new DoubleMatrix(new double[][]{
            {   1.0,       -(1.0/2.0),   1.0/2.0},
            { -(1.0/2.0),    1.0/6.0,  -(1.0/2.0)},
            {   1.0/2.0,   -(1.0/2.0), -(1.0/2.0)}
        });

        ///

        KKsi = new DoubleMatrix(new double[][]{
            {0, 0, 0},
            {1.0, 0, 0},
            {0, 0, 0}
        });

        KEta = new DoubleMatrix(new double[][]{
            {0, 0, 0},
            {1.0/2.0, 0, 0},
            {3.0/2.0, 0, 0}
        });
    }


    private void initBasisFunctions() {

        basisFunctions = new Function[3];
        squaredBasisFunctions = new Function[3];

        zeroFunction = new Function() {
            @Override
            public double getValue(double[] x) {
                return 0;
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                return this;
            }
        };


        basisFunctions[0] = new Function() {
            // 1
            @Override
            public double getValue(double[] x) {
                return 1.0;
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                if (xOrder == 0 && yOrder == 0) {
                    return this;
                }
                return zeroFunction;
            }
        };

        squaredBasisFunctions[0] = new Function() {
            @Override
            public double getValue(double[] x) {
                return 1.0;
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                if (xOrder == 0 && yOrder == 0) {
                    return this;
                }
                return zeroFunction;
            }
        };

        basisFunctions[1] = new Function() {
            // 2x + y -1
            @Override
            public double getValue(double[] x) {
                return 2.0 * x[0] + x[1] - 1.0;
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                assert (false) : "not implemented yet!";
                return null;
            }
        };

        squaredBasisFunctions[1] = new Function() {
            @Override
            public double getValue(double[] x) {
                // 4 x^2 + 4 x y - 4 x + y^2 - 2 y + 1
                return
                    4.0*x[0]*x[0] + 4.0*x[0]*x[1] - 4.0*x[0] + x[1]*x[1] - 2.0*x[1] + 1.0;
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                assert (false) : "not implemented yet!";
                return null;
            }

        };


        basisFunctions[2] = new Function() {
            // 3y - 1
            @Override
            public double getValue(double[] x) {
                return 3.0 * x[1] - 1.0;
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                assert (false) : "not implemented yet!";
                return null;
            }

        };

        squaredBasisFunctions[2] = new Function() {
            @Override
            public double getValue(double[] x) {
                return 9.0 *x[1]*x[1] - 6.0*x[1] + 1.0;
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                assert (false) : "not implemented yet!";
                return null;
            }
        };
    }

    @Override
    public int getNumberOfBasisFunctions() {
        return 3;
    }

    @Override
    public Function getFunction(int number) {
        return null;
    }

    @Override
    public DoubleMatrix M() {
        return M;
    }


    @Override
    public DoubleMatrix F0(int j) {
        return F0[j];
    }

    @Override
    public DoubleMatrix F(int j, int i) {
        return F[j][i];
    }

    @Override
    public DoubleMatrix KKsi() {
        return KKsi;
    }

    @Override
    public DoubleMatrix KEta() {
        return KEta;
    }

    @Override
    public DoubleMatrix calcUCoeffs(InitialConditionPhase initialConditionPhase, DoubleMatrix initialConditionAmplitude, Triangle t) {

        final Function initialConditionPhaseInInnerSystem = new Function() {
            @Override
            // todo this change of variables should be discused
            public double getValue(double[] x) {
                return initialConditionPhase.calc(
                    t.getX(x[0], x[1]), t.getY(x[0], x[1]), 0
                );
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                return null;
            }
        };

        DoubleMatrix u = new DoubleMatrix(initialConditionAmplitude.rows, numberOfBasisFunctions);

        for (int numberOfCoeff = 0; numberOfCoeff < numberOfBasisFunctions; numberOfCoeff++) {

            double upperIntegral = squareIntegral(initialConditionPhaseInInnerSystem, basisFunctions[numberOfCoeff], integrationStep);
            double downIntegral = M.get(numberOfCoeff, numberOfCoeff);

            DoubleMatrix column = initialConditionAmplitude.mul(upperIntegral / downIntegral);
            u.putColumn(numberOfCoeff, column);
        }

        return u;
    }

    private double squareIntegral(Function f1, Function f2, double integrationStep) {
        double dl = integrationStep;
        double numericalValue = 0;

        double[] x = new double[]{0, 0};
        for (x[0] = 0; x[0] < 1; x[0] += dl) {
            for (x[1] = 0; x[1] < (1 - x[0]); x[1] += dl) {
                numericalValue += f1.getValue(x) * f2.getValue(x);
            }
        }
        numericalValue *= dl * dl;

        if (Math.abs(numericalValue) < integrationStep) {
            numericalValue = 0.0f;
        }

        return numericalValue;
    }

//    @Override
//    public double[] calcUNumerical(DoubleMatrix UCoeffs, Triangle t) {
//        // TODO check this seriously
//        double ksi = t.getKsiInLocalSystem(t.getCenter().x, t.getCenter().y);
//        double eta = t.getEtaInLocalSystem(t.getCenter().x, t.getCenter().y);
//
//        double[] result = new double[UCoeffs.rows];
//        Arrays.fill(result, 0);
//
//        for (int value = 0; value < UCoeffs.rows; value++) {
//            for (int coeff = 0; coeff < UCoeffs.columns; coeff++) {
//                // todo should be discussed
//                // todo compare with Denis and check
//                result[value] += UCoeffs.get(value, coeff) * basisFunctions[coeff].getValue(new double[]{ksi, eta});
//            }
//        }
//
//        return result;
//    }

    @Override
    public double[] calcUNumericalInPoint(DoubleMatrix u, Triangle t, double[] xy) {
        assert false;
        return null;
    }
}