package com.github.sudobobo;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Border;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

//         (as mentioned in the article) u_p_l in each triangle
//         p - stands for index of variable (sigma x, sigma y, etc)
//         l - stands for index of time-dependent coefficient
//         expected to be p x l matrics (p - rows, l - columns)
public class dUmethodReal implements dUmethod {
    @Override
    // expected not to change 'u' or 't'
    public DoubleMatrix calcDU(DoubleMatrix u, Triangle t, Basis basis) {
        DoubleMatrix first = DoubleMatrix.zeros(u.rows, u.columns);
        DoubleMatrix second = DoubleMatrix.zeros(u.rows, u.columns);

        // consider using mul for matrix * number, mmul form matrix * matrix
        for (int j = 0; j < 3; j++) {
            Border b = t.getBorders()[j];
//
            DoubleMatrix temp = t.getT(j).mul(0.5);
            //todo check An
            //todo maybe A ??? As we are in the right system already
            //todo An must be j dependent
            DoubleMatrix sum = t.getAn().add(t.getAAbs());
//            DoubleMatrix sum = t.calcAnj(j).add(t.calcAAbsJ(j));
            temp = temp.mmul(sum);
            temp = temp.mmul(t.getTInv(j));
            temp = temp.mmul(u);
            temp = temp.mul(t.getS(j));
            temp = temp.mmul(basis.F0(j));
            first.addi(temp);

            int i = t.getIForFijFormula(j);

            temp = t.getT(j).mul(0.5);
            //todo check An
            DoubleMatrix sub = t.getAn().sub(t.getAAbs());
//            DoubleMatrix sub = t.calcAnj(j).sub(t.calcAAbsJ(j));

            temp = temp.mmul(sub);
            temp = temp.mmul(t.getTInv(j));
            temp = temp.mmul(b.getNeighborTriangle().getValue().u);
            temp = temp.mul(t.getS(j));
            temp = temp.mmul(basis.F(j, i));
            second.addi(temp);

        }

        // TODO fix this it is only for l = 0 case !
        DoubleMatrix third = DoubleMatrix.zeros(u.rows, u.columns);
        DoubleMatrix fourth = DoubleMatrix.zeros(u.rows, u.columns);

//        DoubleMatrix third = t.getAStr().mmul(u);
//        third = third.mmul(basis.KKsi());
//        third = third.mul(t.getJacobian());
//
//        DoubleMatrix fourth = t.getBStr().mmul(u);
//        fourth = fourth.mmul(basis.KEta());
//        fourth = fourth.mmul(t.getJacobian());

        DoubleMatrix fifth = DoubleMatrix.zeros(u.rows, u.columns);

        DoubleMatrix inversedM = Solve.pinv(basis.M());
        DoubleMatrix divider = inversedM.mul(1.0 / t.getJacobian());

        DoubleMatrix dU = third.add(fourth);

        dU = dU.sub(first);
        dU = dU.sub(second);
        dU = dU.mmul(divider);

        return dU;
    }
}
