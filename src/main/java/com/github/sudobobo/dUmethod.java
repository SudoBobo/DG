package com.github.sudobobo;

import org.jblas.DoubleMatrix;

public class dUmethod {
    public DoubleMatrix calcDU(Triangle previousCondition) {

        // (as mentioned in the article) u_p_l in each triangle
        // p - stands for index of variable (sigma x, sigma y, etc)
        // l - stands for index of time-dependent coefficient
        // expected to be p x l matrics (p - rows, l - columns)

        Triangle tr = previousCondition;
        DoubleMatrix u = previousCondition.u;

        DoubleMatrix first = new DoubleMatrix(u.rows, u.columns);
        DoubleMatrix second = new DoubleMatrix(u.rows, u.columns);

        // consider using mul for matrix * number, not mmul
        for (int j = 0; j < 3; j++) {
            first.addi(tr.T(j).mul(0.5).mmul(tr.An().add(tr.AAbs())).
                    mmul(tr.TInv(j)).mmul(u).mul(tr.S(j)).
                    mmul(tr.Fkl()));


            second.addi(tr.T(j).mul(0.5).mmul(tr.An().sub(tr.AAbs())).
                    mmul(tr.TInv(j)).mmul(tr.uNeib(j).u).mul(tr.S(j)).
                    mmul(tr.Fkl(j)));

        }

        // TODO fix this it is only for l = 0 case !
//        DoubleMatrix third = new DoubleMatrix(u.rows, u.columns);
//        third.addi(tr.AStr().mmul(u). mul(tr.jacobian()). mmul(tr.KKsi()));
//
//        DoubleMatrix fourth = new DoubleMatrix(u.rows, u.columns);
//        fourth.addi(tr.BStr().mmul(u).mul(tr.jacobian()). mmul(tr.KMu()));
//
//        DoubleMatrix fifth = new DoubleMatrix(u.rows, u.columns);

        DoubleMatrix third = DoubleMatrix.zeros(u.rows, u.columns);
        DoubleMatrix fourth = DoubleMatrix.zeros(u.rows, u.columns);

        // p * l (size)

        DoubleMatrix dU = (third.add(fourth).sub(first).sub(second).div(tr.Mkl().mul(tr.absJacobian())));

        return dU;
    }
}
