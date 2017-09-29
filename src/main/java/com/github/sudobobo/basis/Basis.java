package com.github.sudobobo.basis;

import org.jblas.DoubleMatrix;

public interface Basis {

    // getFunctions[]
    Function getFunction(int number);

    // pre-calculcated matrixes from 24-28
    // (24)
    DoubleMatrix M();
    // (25)
    DoubleMatrix[] F0();
    // (26)
    DoubleMatrix[][] F(int i);
    // (27)
    DoubleMatrix KKsi();
    // (28)
    DoubleMatrix KEta();
    //
    double D(int l);


    // methods to calculate u[p][l] coefficinets from single numerical initial value of u
    DoubleMatrix calcUCoeffs(DoubleMatrix numericalUColumn);
    //
    //

    // method to calculate u from u[p][l] and spatial coordinate
    //
    //
    //
}
