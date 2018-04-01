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
public class dUmethodBack {
    // expected not to change 'u' or 't'
    public DoubleMatrix calcDU(DoubleMatrix u, Triangle t, Basis basis) {
        DoubleMatrix first = DoubleMatrix.zeros(u.rows, u.columns);
        DoubleMatrix second = DoubleMatrix.zeros(u.rows, u.columns);

        // consider using mul for matrix * number, mmul form matrix * matrix
        for (int j = 0; j < 3; j++) {
            Border b = t.getBorders()[j];

//            DoubleMatrix temp = t.getT(j).mul(0.5).mmul(t.getAn().add(t.getAAbs())).
//                    mmul(t.getTInv(j)).mmul(u).mul(t.getS(j)).
//                    mmul(basis.F0(j));
//
            DoubleMatrix temp = t.getT(j).mul(0.5);
            //todo check An
            //todo maybe A ??? As we are in the right system already
            //todo An must be j dependent
            DoubleMatrix sum = t.getAn().add(t.getAAbs());
//            DoubleMatrix sum = t.calcAnj(j).add(t.getAAbs());
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
//            DoubleMatrix sub = t.calcAnj(j).sub(t.getAAbs());

            temp = temp.mmul(sub);
            temp = temp.mmul(t.getTInv(j));
            temp = temp.mmul(b.getNeighborTriangle().getValue().u);
            temp = temp.mul(t.getS(j));
            temp = temp.mmul(basis.F(j, i));
            second.addi(temp);


//                Border borb = t.getBorders()[j];
//                assert borb == null;
//
//                assert t.getBorders()[j].getNeighborTriangle() == null;
//                assert t.getBorders()[j].getNeighborTriangle().getValue() == null;
//                assert t.getBorders()[j].getNeighborTriangle().getValue().getU() == null;
//
//                DoubleMatrix Fji = basis.F(j, i);
//
//            temp = t.getT(j).mul(0.5).mmul((t.getAn().sub(t.getAAbs()))).
//                    mmul(t.getTInv(j)).mmul(t.getBorders()[j].getNeighborTriangle().
//                    getValue().getU()).mul(t.getS(j)).mmul(basis.F(j, i));

        }

        // TODO fix this it is only for l = 0 case !
        DoubleMatrix third = t.getAStr().mmul(u);
        third = third.mmul(basis.KKsi());
        third = third.mul(t.getJacobian());

//        third.addi(tr.AStr().mmul(u). mul(tr.jacobian()). mmul(tr.KKsi()));
//        third.addi(t.getAStr().mmul(u).mul(t.getJacobian()).mmul(basis.KKsi()));

        DoubleMatrix fourth = t.getBStr().mmul(u);
        fourth = fourth.mmul(basis.KEta());
        fourth = fourth.mmul(t.getJacobian());

//        fourth.addi(tr.BStr().mmul(u).mul(tr.jacobian()). mmul(tr.KMu()));
//        fourth.addi(t.getBStr().mmul(u).mul(t.getJacobian()).mmul(basis.KEta()));

        DoubleMatrix fifth = DoubleMatrix.zeros(u.rows, u.columns);

        // p * l (size)

        DoubleMatrix inversedM = Solve.pinv(basis.M());
        DoubleMatrix divider = inversedM.mul(1.0 / t.getJacobian());

//        DoubleMatrix dU = (third.add(fourth).sub(first).sub(second)).mmul(divider);
        DoubleMatrix dU = third.add(fourth);
//        dU = dU.sub(first);
        dU = dU.sub(second);
        dU = dU.mmul(divider);

        // todo note that previously there was an absJacobian, not jacobianDoubleMatrix dU = (third.add(fourth).sub(first).sub(second).div(basis.M().mul(t.getJacobian())));

        return dU;
    }
//
//    public DoubleMatrix calcDU_new(DoubleMatrix u, Triangle t, Basis b) {
//        DoubleMatrix third = t.getAStr().mmul(u);
//        third = third.mmul(b.KKsi());
//
//        DoubleMatrix fourth = t.getBStr().mmul(u);
//        fourth = fourth.mmul(b.KEta());
//
//        DoubleMatrix der = Solve.pinv(b.M()).mul(1/t.getJacobian());
//
//        DoubleMatrix first;
//        DoubleMatrix second;
//
//        for (int j = 0; j < 3; j++) {
//
//        }
//    }
}
