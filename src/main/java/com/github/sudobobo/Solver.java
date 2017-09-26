package com.github.sudobobo;

interface Solver {
    void solveOneStep(Mesh previousCondition, Mesh newCondition, double timeStep);
}
