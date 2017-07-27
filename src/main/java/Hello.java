package main.java;

import main.java.utils.MatrixRepresenter;
import org.jblas.DoubleMatrix;

public class Hello {
    public static void main(String[] args) {
        DoubleMatrix A = new DoubleMatrix(new double[][]{
                {100000.0, 200000.0, 3000000.0},
                {4.0, 5.5656565656, 6.5656565656},
                {7.565656565657, 8.0, 9.0}
        });


        MatrixRepresenter.print(A);

    }
}