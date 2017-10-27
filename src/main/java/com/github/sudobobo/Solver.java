package com.github.sudobobo;

import com.github.sudobobo.basis.Basis;

interface Solver {

    // Result of the calculations is placed into the 'values'
    // 'bufferValues' is used as buffer

    void solveOneStep(Value[] values, Value[] bufferValues, double timeStep, Basis basis);
}
