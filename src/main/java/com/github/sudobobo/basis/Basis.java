package com.github.sudobobo.basis;

import com.github.sudobobo.calculations.InitialConditionPhase;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;

public interface Basis {

    // getFunctions[]
    Function getFunction(int number);

    // pre-calculcated matrixes from
    // (24)
    DoubleMatrix M();
    // (25)
    DoubleMatrix F0(int j);
    // (26)
    DoubleMatrix F(int j, int i);
    // (27)
    DoubleMatrix KKsi();
    // (28)
    DoubleMatrix KEta();

    // methods to calculate u[p][l] coefficinets from single numerical initial value of u
    DoubleMatrix calcUCoeffs(InitialConditionPhase initialConditionPhase, DoubleMatrix initialConditionAmplitude,
                             Triangle t);


    // method to calculate u from u[p][l] and spatial coordinate
    double [] calcUNumerical(DoubleMatrix UCoeffs, Triangle t);
}
