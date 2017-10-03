package com.github.sudobobo.geometry;

import lombok.Builder;
import lombok.Data;
import org.jblas.DoubleMatrix;

public
@Data
@Builder
class Triangle {
    private final int number;
    private int[] pointsId;

    private DoubleMatrix A;
    private DoubleMatrix B;

    private DoubleMatrix AAbs;
    private DoubleMatrix AStr;
    private DoubleMatrix BStr;

    private double[] S;
    private double jacobian;

    private DoubleMatrix M;
    private DoubleMatrix[] F0;
    private DoubleMatrix KKsi;
    private DoubleMatrix KMu;
    private DoubleMatrix[][] F;

    private DoubleMatrix[] T;
    private DoubleMatrix[] TInv;

    private DoubleMatrix An;

    private Triangle[] uNeib;
    private int domain;
}
