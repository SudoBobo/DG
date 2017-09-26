package com.github.sudobobo;

import org.jblas.DoubleMatrix;

public class RKSolver implements Solver {

    private dUmethod dU_method;
    // for optimization needs
    private DoubleMatrix [] oldU;
    private DoubleMatrix [] newU;

    private DoubleMatrix [] k1;
    private DoubleMatrix [] k2;
    private DoubleMatrix [] k3;
    private DoubleMatrix [] k4;

    // TODO change add/mul for addi/muli to make use of inplace calculations (GC work can be really heavy)

    public RKSolver(dUmethod dU_method, int numberOfTriangles) {
        this.dU_method = dU_method;

        oldU = createZeroMatrices(numberOfTriangles);
        newU = createZeroMatrices(numberOfTriangles);

        k1 = createZeroMatrices(numberOfTriangles);
        k2 = createZeroMatrices(numberOfTriangles);
        k3 = createZeroMatrices(numberOfTriangles);
        k4 = createZeroMatrices(numberOfTriangles);
    }

    private DoubleMatrix[] createZeroMatrices(int numberOfTriangles) {
        DoubleMatrix [] res = new DoubleMatrix[numberOfTriangles];
        for (int i = 0; i < numberOfTriangles; i++){
            res[i] = DoubleMatrix.zeros(1);
        }
        return res;
    }

    @Override
    public void solveOneStep(Mesh previousCondition, Mesh newCondition, double timeStep) {

        // 1 RK step

        for (int t = 0; t < previousCondition.size(); t++) {
            oldU[t].copy(previousCondition.triangles.get(t).u);
            
            k1[t].copy(
                    dU_method.calcDU(previousCondition.triangles.get(t))
            );
        }

        // 2 RK step

        for (int t = 0; t < previousCondition.size(); t++) {
            previousCondition.triangles.get(t).u.copy(
                    oldU[t].add(k1[t].mul(timeStep/2))
            );
        }

        for (int t = 0; t < previousCondition.size(); t++) {
            k2[t].copy(
                    dU_method.calcDU(previousCondition.triangles.get(t))
            );
        }

        // 3 RK step

        // TODO add -> addi (and swap positions, like new_object.addi(old_object)
        for (int t = 0; t < previousCondition.size(); t++) {
            previousCondition.triangles.get(t).u.copy(
                    oldU[t].add(k2[t].mul(timeStep/2))
            );
        }

        for (int t = 0; t < previousCondition.size(); t++) {
            k3[t].copy(
                    dU_method.calcDU(previousCondition.triangles.get(t))
            );
        }


        // 4 RK step
        for (int t = 0; t < previousCondition.size(); t++) {
            previousCondition.triangles.get(t).u.copy(
                    oldU[t].add(k3[t].mul(timeStep))
            );
        }

        for (int t = 0; t < previousCondition.size(); t++) {
            k4[t].copy(
                    dU_method.calcDU(previousCondition.triangles.get(t))
            );
            
            newCondition.triangles.get(t).u.copy(
                    oldU[t].add(
                            (k1[t].add(k2[t].mul(2)).add(k3[t].mul(2)).add(k4[t])).mul(timeStep/6)
                    )
            );
        }
        
        
    }
}
