package com.github.sudobobo.basis;

import org.jblas.DoubleMatrix;

public class Linear2DBasis implements Basis {

    private Function zeroFunction;
    private Function[] basisFunctions;
    private int numberOfBasisFunctions;

    private double integrationStep;

    DoubleMatrix M;
    DoubleMatrix[] F0;
    DoubleMatrix[][] F;
    DoubleMatrix KKsi;
    DoubleMatrix KEta;
    double[] D;


    public Linear2DBasis(double integrationStep) {
        this.integrationStep = integrationStep;

        numberOfBasisFunctions = 3;
        initBasisFunctions();

        M = calcM();

        F0 = calcF0();
        F = caclF();

        KKsi = calcKKsi();
        KEta = calcKEta();

        // D[l] where l is the number of basis function
        D = calcD(M);
    }

    private double[] calcD(DoubleMatrix M) {
        double [] D = new double[numberOfBasisFunctions];

        // TODO may be totally wrong
        // TODO only for basis {1, x - 1/3, y - 1/3}
        D[0] = 1;
        D[1] = 0;
        D[2] = 0;

        return D;
    }

    private DoubleMatrix[][] caclF() {
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

    // TODO make Basis an abstract class with this method as it implements the same logic for every basis
    @Override
    public DoubleMatrix calcUCoeffs(DoubleMatrix numericalUColumn) {
        DoubleMatrix u = new DoubleMatrix(numericalUColumn.rows, numberOfBasisFunctions);

        for (int numberOfVariable = 0; numberOfVariable < numericalUColumn.rows; numberOfVariable++) {
            for (int numberOfCoeff = 0; numberOfCoeff < numberOfBasisFunctions; numberOfCoeff++) {

                double value = numericalUColumn.get(numberOfVariable) * D(numberOfCoeff) / M.get(numberOfCoeff, numberOfCoeff);
                u.put(numberOfVariable, numberOfCoeff, value);

            }
        }

        return u;
    }

    // description of linear2D basis {1; x - 1/3; y - 1/3}
    private void initBasisFunctions() {

        basisFunctions = new Function[3];

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

    @Override
    public Function getFunction(int number) {
        return basisFunctions[number];
    }

    @Override
    public DoubleMatrix M() {
        return M;
    }

    @Override
    public DoubleMatrix[] F0() {
        return F0;
    }

    @Override
    public DoubleMatrix[][] F() {
        return F;
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
    public double D(int l) {
        return D[l];
    }
}
