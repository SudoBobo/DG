package com.github.sudobobo;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;

public interface dUmethod {

    DoubleMatrix calcDU(DoubleMatrix u, Triangle t, Basis basis);
}
