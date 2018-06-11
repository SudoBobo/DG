package com.github.sudobobo.geometry;

import com.github.sudobobo.meshconstruction.SourceConfig;
import org.jblas.DoubleMatrix;

public class PointSource {
    protected double amplitude;
    protected double omega;
    protected double[] point;

    // values needed for calculations
    public PointSource(SourceConfig s) {
        this.amplitude = s.getAmplitude();
        this.omega = s.getOmega();
        this.point = new double[3];
        this.point[0] = s.getPoint()[0];
        this.point[1] = s.getPoint()[1];
    }

    // mind inner triangle system
    public DoubleMatrix integrateOverTriangle(Triangle triangle, double t, double dt){
        return null;
    }
}
