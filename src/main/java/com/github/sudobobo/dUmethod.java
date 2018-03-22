package com.github.sudobobo;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Border;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;

//         (as mentioned in the article) u_p_l in each triangle
//         p - stands for index of variable (sigma x, sigma y, etc)
//         l - stands for index of time-dependent coefficient
//         expected to be p x l matrics (p - rows, l - columns)
public class dUmethod {
    // expected not to change 'u' or 't'
    public DoubleMatrix calcDU(DoubleMatrix u, Triangle t, Basis basis) {
        DoubleMatrix first = DoubleMatrix.zeros(u.rows, u.columns);
        DoubleMatrix second = DoubleMatrix.zeros(u.rows, u.columns);

        // consider using mul for matrix * number, mmul form matrix * matrix
        for (int j = 0; j < 3; j++) {
            Border b = t.getBorders()[j];

            if (b.isEdgeOfMesh() == false) {
                DoubleMatrix temp = t.getT(j).mul(0.5).mmul(t.getAn().add(t.getAAbs())).
                        mmul(t.getTInv(j)).mmul(u).mul(t.getS(j)).
                        mmul(basis.F0(j));
                first.addi(temp);

                int i = t.getIForFijFormula(j);

//                Border borb = t.getBorders()[j];
//                assert borb == null;
//
//                assert t.getBorders()[j].getNeighborTriangle() == null;
//                assert t.getBorders()[j].getNeighborTriangle().getValue() == null;
//                assert t.getBorders()[j].getNeighborTriangle().getValue().getU() == null;
//
//                DoubleMatrix Fji = basis.F(j, i);
//
                temp = t.getT(j).mul(0.5).mmul((t.getAn().sub(t.getAAbs()))).
                        mmul(t.getTInv(j)).mmul(t.getBorders()[j].getNeighborTriangle().
                        getValue().getU()).mul(t.getS(j)).mmul(basis.F(j, i));
                second.addi(temp);
            }

            if (b.isEdgeOfMesh() == true) {
//                if b.getDomain
            }

        }

        // TODO fix this it is only for l = 0 case !
        DoubleMatrix third = DoubleMatrix.zeros(u.rows, u.columns);
//        third.addi(tr.AStr().mmul(u). mul(tr.jacobian()). mmul(tr.KKsi()));
        third.addi(t.getAStr().mmul(u).mul(t.getJacobian()).mmul(basis.KKsi()));

        DoubleMatrix fourth = DoubleMatrix.zeros(u.rows, u.columns);
//        fourth.addi(tr.BStr().mmul(u).mul(tr.jacobian()). mmul(tr.KMu()));
        fourth.addi(t.getBStr().mmul(u).mul(t.getJacobian()).mmul(basis.KEta()));

        DoubleMatrix fifth = DoubleMatrix.zeros(u.rows, u.columns);


        // p * l (size)

//        DoubleMatrix dU = (third.add(fourth).sub(first).sub(second).div(t.Mkl().mul(tr.absJacobian())));

        // todo note that previously there was an absJacobian, not jacobian
        DoubleMatrix dU = (third.add(fourth).sub(first).sub(second).div(basis.M().mul(t.getJacobian())));

        return dU;
    }
}
