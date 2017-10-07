package com.github.sudobobo.geometry;

import lombok.Builder;
import lombok.Data;
import org.jblas.DoubleMatrix;

public
@Data
@Builder
class Triangle {
    private final int number;
    private Point[] points;

    private DoubleMatrix A;
    private DoubleMatrix B;

    private DoubleMatrix AAbs;
    private DoubleMatrix AStr;
    private DoubleMatrix BStr;

    private double[] S;
    private double jacobian;

    private DoubleMatrix[] T;
    private DoubleMatrix[] TInv;

    private DoubleMatrix An;

    private Border[] borders;
    private int domain;
}
