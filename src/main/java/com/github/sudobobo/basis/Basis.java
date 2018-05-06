package com.github.sudobobo.basis;

import com.github.sudobobo.calculations.InitialConditionPhase;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;

public interface Basis {

    int getNumberOfBasisFunctions();

    Function getFunction(int number);

    // pre-calculcated matrixes from
    // (24)
    DoubleMatrix M();

    // (25)
    DoubleMatrix F0(int j);

    // (26)
    // F[j][i]
    // j stands for border number in considered triangle
    // i stands for border number in neighbour triangle
    DoubleMatrix F(int j, int i);

    // (27)
    DoubleMatrix KKsi();

    // (28)
    DoubleMatrix KEta();

    // methods to calculate u[p][l] coefficinets from triangle geometry and function which return u[p] vector
    // with numerical values
    DoubleMatrix calcUCoeffs(InitialConditionPhase initialConditionPhase, DoubleMatrix initialConditionAmplitude,
                             Triangle t);

    // method to calculate u[p] from u[p][l]
    double[] calcUNumerical(DoubleMatrix UCoeffs, Triangle t);
}
