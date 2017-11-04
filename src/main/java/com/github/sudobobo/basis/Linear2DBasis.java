package com.github.sudobobo.basis;

import com.github.sudobobo.calculations.InitialConditionPhase;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;

import java.util.Arrays;

public class Linear2DBasis implements Basis {

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

    public Linear2DBasis(double integrationStep) {
        this.integrationStep = integrationStep;

        numberOfBasisFunctions = 3;
        initBasisFunctions();

        M = calcM();

        F0 = calcF0();
        F = calcF();

        KKsi = calcKKsi();
        KEta = calcKEta();
    }


    // description of linear2D basis {1; x - 1/3; y - 1/3}

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


    // TODO make Basis an abstract class with this method as it implements the same logic for every basis
    @Override
    public DoubleMatrix calcUCoeffs(InitialConditionPhase initialConditionPhase, DoubleMatrix initialConditionAmplitude, Triangle t) {

        Function initialConditionPhaseInInnerSystem = new Function() {
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

        for (int numberOfVariable = 0; numberOfVariable < u.rows; numberOfVariable++) {
            for (int numberOfCoeff = 0; numberOfCoeff < u.columns; numberOfCoeff++) {

                // todo using squareIntegral should be discussed
//                double upperIntegral = squareIntegral(initialConditionPhaseInInnerSystem, basisFunctions[numberOfCoeff], integrationStep);
//                double downIntegral = M.get(numberOfCoeff, numberOfCoeff);
//                double value = initialConditionAmplitude.get(numberOfVariable) * (upperIntegral / downIntegral);

                double upperIntegral = squareIntegral(initialConditionPhaseInInnerSystem, basisFunctions[numberOfCoeff], integrationStep);
                double downIntegral = M.get(numberOfCoeff, numberOfCoeff);
                double value = initialConditionAmplitude.get(numberOfVariable) * (upperIntegral / downIntegral);

                u.put(numberOfVariable, numberOfCoeff, value);
            }
        }
        return u;
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

    private DoubleMatrix calcM() {
        // tested
        DoubleMatrix M = new DoubleMatrix(numberOfBasisFunctions, numberOfBasisFunctions);

        for (int i = 0; i < numberOfBasisFunctions; i++) {
            for (int j = 0; j < numberOfBasisFunctions; j++) {

                M.put(i, j,
                        squareIntegral(basisFunctions[i], basisFunctions[j], integrationStep));
            }
        }

        return M;
    }

    private DoubleMatrix calcKKsi() {
        // tested
        DoubleMatrix KKsi = new DoubleMatrix(numberOfBasisFunctions, numberOfBasisFunctions);

        for (int k = 0; k < numberOfBasisFunctions; k++) {
            for (int l = 0; l < numberOfBasisFunctions; l++) {
                KKsi.put(k, l,
                        squareIntegral(basisFunctions[k].getDerivative(1, 0, 0), basisFunctions[l], integrationStep));
            }
        }

        return KKsi;
    }

    private DoubleMatrix calcKEta() {
        // tested
        DoubleMatrix KEta = new DoubleMatrix(numberOfBasisFunctions, numberOfBasisFunctions);

        for (int k = 0; k < numberOfBasisFunctions; k++) {
            for (int l = 0; l < numberOfBasisFunctions; l++) {
                KEta.put(k, l,
                        squareIntegral(basisFunctions[k].getDerivative(0, 1, 0), basisFunctions[l], integrationStep));
            }
        }

        return KEta;
    }

    private DoubleMatrix[][] calcF() {
        int numberOfTriangleSides = 3;
        DoubleMatrix[][] F = new DoubleMatrix[numberOfTriangleSides][numberOfTriangleSides];

        for (int i = 0; i < numberOfTriangleSides; i++) {
            for (int j = 0; j < numberOfTriangleSides; j++) {

                DoubleMatrix Fij = new DoubleMatrix(numberOfBasisFunctions, numberOfBasisFunctions);

                for (int k = 0; k < numberOfBasisFunctions; k++) {
                    for (int l = 0; l < numberOfBasisFunctions; l++) {
                        Fij.put(k, l,
                                linearIntegral(j, i, basisFunctions[k], basisFunctions[l], integrationStep));
                    }
                }

                F[i][j] = Fij;
            }
        }

        return F;
    }

    private DoubleMatrix[] calcF0() {
        int numberOfTriangleSides = 3;
        DoubleMatrix[] F0 = new DoubleMatrix[numberOfTriangleSides];

        for (int j = 0; j < numberOfTriangleSides; j++) {

            DoubleMatrix Fj = new DoubleMatrix(numberOfBasisFunctions, numberOfBasisFunctions);

            for (int k = 0; k < numberOfBasisFunctions; k++) {
                for (int l = 0; l < numberOfBasisFunctions; l++) {
                    Fj.put(k, l,
                            linearIntegral(j, -1, basisFunctions[k], basisFunctions[l], integrationStep));
                }
            }

            F0[j] = Fj;
        }

        return F0;
    }

    @Override
    public int getNumberOfBasisFunctions() {
        return numberOfBasisFunctions;
    }

    @Override
    public Function getFunction(int number) {
        return basisFunctions[number];
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
}