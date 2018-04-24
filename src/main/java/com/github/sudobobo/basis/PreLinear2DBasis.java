package com.github.sudobobo.basis;

import com.github.sudobobo.calculations.InitialConditionPhase;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;

import java.util.Arrays;

public class PreLinear2DBasis implements Basis {

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

    public PreLinear2DBasis(double integrationStep) {
        this.integrationStep = integrationStep;
        initBasisFunctions();
        numberOfBasisFunctions = 3;

        M = new DoubleMatrix(new double[][]{
            {1.0 / 2.0, 0, 0},
            {0, 1.0f / 36.0, -(1.0 / 72.0)},
            {0, -(1.0f / 72.0), 1.0 / 36.0}
        });

        F0 = new DoubleMatrix[3];

        F0[0] = new DoubleMatrix(new double[][]{
            {1, 1.0f / 6.0f, -(1.0f / 3.0f)},
            {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)},
            {-(1.0f / 3.0f), -(1.0f / 18.0f), 1.0f / 9.0f}
        });

        F0[1] = new DoubleMatrix(new double[][]{
            {1, 1.0f / 6.0f, 1.0f / 6.0f},
            {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)},
            {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f}
        });

        F0[2] = new DoubleMatrix(new double[][]{
            {1, -(1.0f / 3.0f), 1.0f / 6.0f},
            {-(1.0f / 3.0f), 1.0f / 9.0f, -(1.0f / 18.0f)},
            {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f}
        });

        F = new DoubleMatrix[3][3];

        F[0][0] = new DoubleMatrix(new double[][]{
            {1, 1.0f / 6.0f, -(1.0f / 3.0f)},
            {1.0f / 6.0f, -(1.0f / 18.0f), -(1.0f / 18.0f)}, {-(1.0f / 3.0f),
            -(1.0f / 18.0f), 1.0f / 9.0f}
        });

        F[0][1] = new DoubleMatrix(new double[][]{
            {1, 1.0f / 6.0f, -(1.0f / 3.0f)},
            {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)},
            {1.0f / 6.0f, -(1.0f / 18.0f), -(1.0f / 18.0f)}
        });

        F[0][2] = new DoubleMatrix(new double[][]{
            {1, 1.0f / 6.0f, -(1.0f / 3.0f)},
            {-(1.0f / 3.0f), -(1.0f / 18.0f), 1.0f / 9.0f},
            {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)}
        });

        F[1][0] = new DoubleMatrix(new double[][]{
            {1, 1.0f / 6.0f, 1.0f / 6.0f},
            {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)},
            {-(1.0f / 3.0f), -(1.0f / 18.0f), -(1.0f / 18.0f)}
        });

        F[1][1] = new DoubleMatrix(new double[][]{
            {1, 1.0f / 6.0f, 1.0f / 6.0f},
            {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f},
            {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)}
        });

        F[1][2] = new DoubleMatrix(new double[][]{
            {1, 1.0f / 6.0f, 1.0f / 6.0f},
            {-(1.0f / 3.0f), -(1.0f / 18.0f), -(1.0f / 18.0f)},
            {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f}

        });

        F[2][0] = new DoubleMatrix(new double[][]{
            {1, -(1.0f / 3.0f), 1.0f / 6.0f},
            {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f},
            {-(1.0f / 3.0f), 1.0f / 9.0f, -(1.0f / 18.0f)}

        });

        F[2][1] = new DoubleMatrix(new double[][]{
            {1, -(1.0f / 3.0f), 1.0f / 6.0f},
            {1.0f / 6.0f, -(1.0f / 18.0f), -(1.0f / 18.0f)},
            {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f}
        });

        F[2][2] = new DoubleMatrix(new double[][]{
            {1, -(1.0f / 3.0f), 1.0f / 6.0f},
            {-(1.0f / 3.0f), 1.0f / 9.0f, -(1.0f / 18.0f)},
            {1.0f / 6.0f, -(1.0f / 18.0f), -(1.0f / 18.0f)}
        });

        KKsi = new DoubleMatrix(new double[][]{
            {0, 1.0f / 2.0f, 0},
            {0, 0, 0},
            {0, 0, 0}
        });

        KEta = new DoubleMatrix(new double[][]{
            {0, 0, 1.0f / 2.0f},
            {0, 0, 0},
            {0, 0, 0}
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
            // x-1/3
            @Override
            public double getValue(double[] x) {
                return x[0] - 0.333333;
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                if (xOrder == 0 && yOrder == 0) {
                    return this;
                }
                if (xOrder == 1 && yOrder == 0) {
                    return basisFunctions[0];
                }
                return zeroFunction;
            }
        };

        squaredBasisFunctions[1] = new Function() {
            @Override
            public double getValue(double[] x) {
                return Math.pow(x[0], 2) - (2 / 3) * x[0] + 1 / 9;
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                assert (false) : "not implemented yet!";
                return null;
            }

        };


        basisFunctions[2] = new Function() {
            // y-1/3
            @Override
            public double getValue(double[] x) {
                return x[1] - 0.333333;
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                if (xOrder == 0 && yOrder == 0) {
                    return this;
                }
                if (xOrder == 0 && yOrder == 1) {
                    return basisFunctions[0];
                }
                return zeroFunction;
            }

        };

        squaredBasisFunctions[2] = new Function() {
            @Override
            public double getValue(double[] x) {
                return Math.pow(x[1], 2) - (2 / 3) * x[1] + 1 / 9;
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
        return F[i][j];
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
                    t.getX(x[0], x[1]), t.getY(x[0], x[1])
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

    private double linearIntegral(int j, int i, Function k, Function l, double integrationStep) {
        double dl = integrationStep;
        double numericalValue = 0;
        if (j < 0 || j > 2) {
            throw new RuntimeException("not implemented yet for j=" + j);
        }
        if (i < -1 || i > 2) {
            throw new RuntimeException("not implemented yet for i=" + i);
        }
        double[] x1 = new double[]{0, 0};
        double[] x2 = new double[]{0, 0};

        for (double t = 0; t < 1; t += dl) {
            switch (j) {
                case 0: {
                    x1[0] = t;
                    x1[1] = 0;
                    break;
                }
                case 1: {
                    x1[0] = 1 - t;
                    x1[1] = t;
                    break;
                }
                case 2: {
                    x1[0] = 0;
                    x1[1] = 1 - t;
                    break;
                }
            }
            switch (i) {
                case -1: {
                    x2[0] = x1[0];
                    x2[1] = x1[1];
                    break;
                }
                case 0: {
                    x2[0] = 1 - t;
                    x2[1] = 0;
                    break;
                }
                case 1: {
                    x2[0] = t;
                    x2[1] = 1 - t;
                    break;
                }
                case 2: {
                    x2[0] = 0;
                    x2[1] = t;
                    break;
                }
            }
            numericalValue += k.getValue(x1) * l.getValue(x2);
        }

        numericalValue *= dl;

        if (Math.abs(numericalValue) < integrationStep) {
            numericalValue = 0.0f;
        }
        return numericalValue;
    }

    @Override
    public double[] calcUNumerical(DoubleMatrix UCoeffs, Triangle t) {

        // todo : don't create an array, use and re-write x`given instead

        double ksi = t.getKsiInLocalSystem(t.getCenter().x, t.getCenter().y);
        double eta = t.getEtaInLocalSystem(t.getCenter().x, t.getCenter().y);

        double[] result = new double[UCoeffs.rows];
        Arrays.fill(result, 0);

        for (int value = 0; value < UCoeffs.rows; value++) {
            for (int coeff = 0; coeff < UCoeffs.columns; coeff++) {

                // todo should be discussed
                result[value] += UCoeffs.get(value, coeff) * basisFunctions[coeff].getValue(new double[]{ksi, eta});
            }
        }

        return result;
    }
}

//
//        Mkl = new float[][]{
//            {1.0f / 2.0f, 0, 0},
//            {0, 1.0f / 36.0f, -(1.0f / 72.0f)},
//            {0, -(1.0f / 72.0f), 1.0f / 36.0f}
//            };
//            Kksikl = new float[][]{{0, 1.0f / 2.0f, 0}, {0, 0, 0}, {0, 0, 0}};
//            Ketakl = new float[][]{{0, 0, 1.0f / 2.0f}, {0, 0, 0}, {0, 0, 0}};
//            Fj0kl = new float[][][]{
//            {{1, 1.0f / 6.0f, -(1.0f / 3.0f)}, {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)}, {-(1.0f / 3.0f), -(1.0f / 18.0f), 1.0f / 9.0f}},
//            {{1, 1.0f / 6.0f, 1.0f / 6.0f}, {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)}, {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f}},
//            {{1, -(1.0f / 3.0f), 1.0f / 6.0f}, {-(1.0f / 3.0f), 1.0f / 9.0f, -(1.0f / 18.0f)}, {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f}}
//            };
//            Fjikl = new float[][][][][] {{
//            {
//            {{1, 1.0f / 6.0f, -(1.0f / 3.0f)}, {1.0f / 6.0f, -(1.0f / 18.0f), -(1.0f / 18.0f)}, {-(1.0f / 3.0f), -(1.0f / 18.0f), 1.0f / 9.0f}},
//            {{1, 1.0f / 6.0f, -(1.0f / 3.0f)}, {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)}, {1.0f / 6.0f, -(1.0f / 18.0f), -(1.0f / 18.0f)}},
//            {{1, 1.0f / 6.0f, -(1.0f / 3.0f)}, {-(1.0f / 3.0f), -(1.0f / 18.0f), 1.0f / 9.0f}, {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)}},}, {
//
// {{1, 1.0f / 6.0f, 1.0f / 6.0f}, {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)}, {-(1.0f / 3.0f), -(1.0f / 18.0f), -(1.0f / 18.0f)}},
//            {{1, 1.0f / 6.0f, 1.0f / 6.0f}, {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f}, {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)}},
//            {{1, 1.0f / 6.0f, 1.0f / 6.0f}, {-(1.0f / 3.0f), -(1.0f / 18.0f), -(1.0f / 18.0f)}, {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f}},}, {
//
// {{1, -(1.0f / 3.0f), 1.0f / 6.0f}, {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f}, {-(1.0f / 3.0f), 1.0f / 9.0f, -(1.0f / 18.0f)}},
//            {{1, -(1.0f / 3.0f), 1.0f / 6.0f}, {1.0f / 6.0f, -(1.0f / 18.0f), -(1.0f / 18.0f)}, {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f}},
//            {{1, -(1.0f / 3.0f), 1.0f / 6.0f}, {-(1.0f / 3.0f), 1.0f / 9.0f, -(1.0f / 18.0f)}, {1.0f / 6.0f, -(1.0f / 18.0f), -(1.0f / 18.0f)}}
//            }
//            }};