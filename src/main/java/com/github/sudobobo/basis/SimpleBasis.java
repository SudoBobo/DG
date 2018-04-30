package com.github.sudobobo.basis;

import com.github.sudobobo.calculations.InitialConditionPhase;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;

import java.util.Arrays;

// Simple basis {1}
public class SimpleBasis implements Basis {
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

    public SimpleBasis(double integrationStep) {
        numberOfBasisFunctions = 1;
        initBasisFunctions();

        this.integrationStep = integrationStep;

        M = new DoubleMatrix(new double[]{1.0});
        F0 = new DoubleMatrix[3];
        // F[0..2] are the same for simplicity
        // according to the article they are slightly different
        // Also note that transformation (18)  convert certain
        // triangle sides in global cartesian system (x0x1, x1x2, x2x0) to
        // certain triangle sides in local system (j = 0;1;2;)
        F0[0] = new DoubleMatrix(new double[]{1.0});
        F0[1] = new DoubleMatrix(new double[]{1.0});
        F0[2] = new DoubleMatrix(new double[]{1.0});

        //Again there should be 9 different variants
        F = new DoubleMatrix[3][1];
        F[0][0] = new DoubleMatrix(new double[]{1.0});
        F[1][0] = new DoubleMatrix(new double[]{1.0});
        F[2][0] = new DoubleMatrix(new double[]{1.0});

        DoubleMatrix KKsi = new DoubleMatrix(new double[]{0.0});
        DoubleMatrix KEta = new DoubleMatrix(new double[]{0.0});
    }

    private void initBasisFunctions() {
        basisFunctions = new Function[1];
        squaredBasisFunctions = new Function[1];

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
    }


    @Override
    public int getNumberOfBasisFunctions() {
        return 1;
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
        // note the order - now it is random!
        // TODO should be fixed with respect to article
        return F[0][0];
    }

    @Override
    public DoubleMatrix KKsi() {
        return KKsi;
    }

    @Override
    public DoubleMatrix KEta() {
        return KEta;
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

//        for (int numberOfVariable = 0; numberOfVariable < u.rows; numberOfVariable++) {
//            for (int numberOfCoeff = 0; numberOfCoeff < u.columns; numberOfCoeff++) {
//
//                // todo using squareIntegral should be discussed
//
//                double upperIntegral = squareIntegral(initialConditionPhaseInInnerSystem, basisFunctions[numberOfCoeff], integrationStep);
//                double downIntegral = M.get(numberOfCoeff, numberOfCoeff);
//                double value = initialConditionAmplitude.get(numberOfVariable) * (upperIntegral / downIntegral);
//
//                u.put(numberOfVariable, numberOfCoeff, value);
//            }
//        }
//
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
}
