package com.github.sudobobo.calculations;

import org.jblas.DoubleMatrix;

public class SinStaticSource implements StaticSource {
    DoubleMatrix spacePart;

    public SinStaticSource(){
        // get params
        // calculate spacePart
    }
    @Override
    public DoubleMatrix getValue(double t, double dt) {
        // integrate time part
        // mmul with space part
        // return
        return null;
    }
}
