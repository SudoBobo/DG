package com.github.sudobobo.basis;
//
// F[j][i]
// j stands for border number in considered triangle
// i stands for border number in neighbour triangle
//

import com.github.sudobobo.calculations.InitialConditionPhase;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

import java.util.Arrays;

public class PreLinear2DBasis implements Basis {
    private Function zeroFunction;
    private Function[] basisFunctions;
    private Function[] squaredBasisFunctions;
    private int numberOfBasisFunctions;
    private double integrationStep;

    private DoubleMatrix M;
    private DoubleMatrix Minv;
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
            {0, 1.0 / 36.0, -(1.0 / 72.0)},
            {0, -(1.0 / 72.0), 1.0 / 36.0}
        });

        Minv = Solve.pinv(M);

        F0 = new DoubleMatrix[3];

        F0[0] = new DoubleMatrix(new double[][]{
            {1, 1.0f / 6.0f, -(1.0f / 3.0f)},
            {1.0f / 6.0f, 1.0f / 9.0f, -(1.0f / 18.0f)},
            {-(1.0f / 3.0f), -(1.0f / 18.0f), 1.0f / 9.0f}
        });

        // this is what I got from WolframAlpha
        F0[1] = new DoubleMatrix(new double[][]{
            {1,           (1.0 / 6.0),      (1.0/6.0)},
            {(1.0/6.0),  (1.0/9.0),        -(1.0 / 18.0)},
            {(1.0/6.0),  -(1.0 / 18.0),     (1.0/9.0)}
        });

//        F0[1] = F0[1].mul(Math.sqrt(2.0));

        F0[2] = new DoubleMatrix(new double[][]{
            {1, -(1.0f / 3.0f), 1.0f / 6.0f},
            {-(1.0f / 3.0f), 1.0f / 9.0f, -(1.0f / 18.0f)},
            {1.0f / 6.0f, -(1.0f / 18.0f), 1.0f / 9.0f}
        });

        F = new DoubleMatrix[3][3];

        //
        // F[j][i]
        // j stands for border number in considered triangle
        // i stands for border number in neighbour triangle
        //

        // j = 0

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

        // j = 1

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

        // j = 2

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

        // my
//        KKsi = new DoubleMatrix(new double[][]{
//            {0, 0, 0},
//            {1.0f / 2.0f, 0, 0},
//            {0, 0, 0}
//        });

        // denis
        KKsi = new DoubleMatrix(new double[][]{
            {0, 1.0f / 2.0f, 0},
            {0, 0, 0},
            {0, 0, 0}
        });

        //my
//        KEta = new DoubleMatrix(new double[][]{
//            {0, 0, 0},
//            {0, 0, 0},
//            {1.0f / 2.0f, 0, 0}
//        });

        // denis

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


    private static int k;

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



        // Old way

        //        DoubleMatrix u = new DoubleMatrix(initialConditionAmplitude.rows, numberOfBasisFunctions);


//        for (int numberOfCoeff = 0; numberOfCoeff < numberOfBasisFunctions; numberOfCoeff++) {
//
//            double upperIntegral = squareIntegral(initialConditionPhaseInInnerSystem, basisFunctions[numberOfCoeff], integrationStep);
//            double downIntegral = M.get(numberOfCoeff, numberOfCoeff);
//
//            DoubleMatrix column = initialConditionAmplitude.mul(upperIntegral / downIntegral);
//            u.putColumn(numberOfCoeff, column);
//        }


        // Denis way

        DoubleMatrix u = new DoubleMatrix(initialConditionAmplitude.rows, numberOfBasisFunctions);


        if (k == 421){
            System.out.println(421);
            System.out.println(t.getCenter().x);
            System.out.println(t.getCenter().y);
            System.out.println(u);
        }
        k++;


        assert (numberOfBasisFunctions == 3);

        double[] scalar = new double[numberOfBasisFunctions];
        scalar = squareIntegralForCoeffCalc(initialConditionPhaseInInnerSystem, integrationStep);
//        for (int numberOfCoeff = 0; numberOfCoeff < numberOfBasisFunctions; numberOfCoeff++) {
//            scalar[numberOfCoeff] =
//                squareIntegralForCoeffCalc(initialConditionPhaseInInnerSystem, basisFunctions[numberOfCoeff], integrationStep);
//        }

        double [] coeff = new double[numberOfBasisFunctions];

        for (int numberOfCoeff = 0; numberOfCoeff < numberOfBasisFunctions; numberOfCoeff++) {
            for (int s = 0; s < numberOfBasisFunctions; s++) {
                //todo check i - j
                coeff[numberOfCoeff] += scalar[s] * Minv.get(s, numberOfCoeff);
            }
        }

//        for (int fIndex = 0; fIndex < coeffsfi.length; fIndex++) {
//            for (int s = 0; s < coeffsfi.length; s++) {
//                coeffsfi[fIndex] += bf.grammInv()[fIndex][s] * scalarFfi[s];
//            }
//        }

        for (int p = 0; p < initialConditionAmplitude.rows; p++){
            for (int l = 0; l < numberOfBasisFunctions; l++){
                u.put(p, l, initialConditionAmplitude.get(p) * coeff[l]);
            }
        }

        return u;
    }

    private double[] squareIntegralForCoeffCalc(Function initialConditionPhaseInInnerSystem, double integrationStep) {

        double ordinary = 1.0;
        double hypotenuse = 0.5;
        double dl = integrationStep;

        double coeff = 1;

        double scalarFfi[] = new double[numberOfBasisFunctions];

        double [] ksi = new double[]{0, 0};
        for (ksi[0] = dl / 2; ksi[0] < 1.0; ksi[0] += dl) {
            for (ksi[1] = dl / 2; ksi[1] < (1.0 - ksi[0]); ksi[1] += dl) {
                if (ksi[1] + dl >= (1.0 - ksi[0])) {
                    coeff = hypotenuse;
                } else {
                    coeff = ordinary;
                }
                double v =  initialConditionPhaseInInnerSystem.getValue(ksi);

                for (int funcIndex = 0; funcIndex < numberOfBasisFunctions; funcIndex++) {
                    scalarFfi[funcIndex] +=  v * getFunction(funcIndex).getValue(ksi) * coeff;
                }
            }
        }
        for (int funcIndex = 0; funcIndex < numberOfBasisFunctions; funcIndex++) {
            scalarFfi[funcIndex] *= (dl * dl);
        }
        return scalarFfi;
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
//
//        // todo : don't create an array, use and re-write x`given instead
//
//        double ksi = t.getKsiInLocalSystem(t.getCenter().x, t.getCenter().y);
//        double eta = t.getEtaInLocalSystem(t.getCenter().x, t.getCenter().y);
//
//        double[] result = new double[UCoeffs.rows];
//        Arrays.fill(result, 0);
//
//        for (int value = 0; value < UCoeffs.rows; value++) {
//            for (int coeff = 0; coeff < UCoeffs.columns; coeff++) {
//
//                // todo should be discussed
//                // todo compare with Denis and check
//                result[value] += UCoeffs.get(value, coeff) * basisFunctions[coeff].getValue(new double[]{ksi, eta});
//            }
//        }
//
//        return result;
//    }

    @Override
    public double[] calcUNumericalInPoint(DoubleMatrix UCoeffs, Triangle t, double[] xy) {
        double ksi = t.getKsiInLocalSystem(xy[0], xy[1]);
        double eta = t.getEtaInLocalSystem(xy[0], xy[1]);

        double[] result = new double[UCoeffs.rows];
        Arrays.fill(result, 0);

        for (int value = 0; value < UCoeffs.rows; value++) {
            for (int coeff = 0; coeff < UCoeffs.columns; coeff++) {
                result[value] += UCoeffs.get(value, coeff) * basisFunctions[coeff].getValue(new double[]{ksi, eta});
            }
        }

        //

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