package com.github.sudobobo;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.calculations.Value;
import org.jblas.DoubleMatrix;

public class RKSolverBack implements Solver {

    private dUmethod dU_method;
    // for optimization needs
    private DoubleMatrix[] oldU;
    private DoubleMatrix[] newU;

    private DoubleMatrix[] k1;
    private DoubleMatrix[] k2;
    private DoubleMatrix[] k3;
    private DoubleMatrix[] k4;

    // TODO change add/mul for addi/muli to make use of inplace calculations (GC work can be really heavy)

    public RKSolverBack(dUmethod dU_method, int numberOfTriangles) {
        this.dU_method = dU_method;

        oldU = createZeroMatrices(numberOfTriangles);
        newU = createZeroMatrices(numberOfTriangles);

        k1 = createZeroMatrices(numberOfTriangles);
        k2 = createZeroMatrices(numberOfTriangles);
        k3 = createZeroMatrices(numberOfTriangles);
        k4 = createZeroMatrices(numberOfTriangles);
    }

    private DoubleMatrix[] createZeroMatrices(int numberOfTriangles) {
        DoubleMatrix[] res = new DoubleMatrix[numberOfTriangles];
        for (int i = 0; i < numberOfTriangles; i++) {
            res[i] = DoubleMatrix.zeros(1);
        }
        return res;
    }

    // 'values' (0) means that currently 'values' have values appropriate to the k-th step of RK method

    // first RK step        : values (0) -> calc -> bufferValues (1)
    // and so on

    // 1 : v(0) -> b(1)
    // 2 : b(1) -> v(2)
    // 3 : v(2) -> b(3)
    // 4 : b(3) -> v(4)
    @Override
    public void solveOneStep(Value[] values, Value[] bufferValues, double timeStep, Basis basis) {

        // todo lack of coordinate dx dy usage
        // todo don't forget about time
        // 1 RK step

        for (int v = 0; v < values.length; v++) {

            k1[v].copy(
                    dU_method.calcDU(values[v].getU(), values[v].getAssociatedTriangle(), basis)
            );
        }

        // 2 RK step

        for (int v = 0; v < values.length; v++) {
            bufferValues[v].getU().copy(
                    values[v].getU().add(k1[v].mul(timeStep / 2))
            );
        }

        for (int v = 0; v < values.length; v++) {
            k2[v].copy(
                    dU_method.calcDU(bufferValues[v].getU(), values[v].getAssociatedTriangle(), basis)
            );
        }

        // 3 RK step

        // TODO add -> addi (and swap positions, like new_object.addi(old_object)
        for (int v = 0; v < values.length; v++) {
            bufferValues[v].getU().copy(
                    values[v].getU().add(k2[v].mul(timeStep / 2))
            );
        }

        for (int v = 0; v < values.length; v++) {
            k3[v].copy(
                    dU_method.calcDU(bufferValues[v].getU(), bufferValues[v].getAssociatedTriangle(), basis)
            );
        }


        // 4 RK step
        for (int v = 0; v < values.length; v++) {
            bufferValues[v].getU().copy(
                    values[v].getU().add(k3[v].mul(timeStep))
            );
        }

        for (int v = 0; v < values.length; v++) {
            k4[v].copy(
                    dU_method.calcDU(bufferValues[v].getU(), bufferValues[v].getAssociatedTriangle(), basis)
            );

            values[v].getU().copy(
                    values[v].getU().add(
                            (k1[v].add(k2[v].mul(2)).add(k3[v].mul(2)).add(k4[v])).mul(timeStep / 6)
                    )
            );
        }


    }
}
