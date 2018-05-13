package com.github.sudobobo.calculations;

import org.jblas.DoubleMatrix;

interface StaticSource {
    public DoubleMatrix getValue(double t, double dt);
}
