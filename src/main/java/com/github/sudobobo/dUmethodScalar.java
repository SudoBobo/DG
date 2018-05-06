package com.github.sudobobo;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Border;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

public class dUmethodScalar implements dUmethod {
    @Override
    public DoubleMatrix calcDU(DoubleMatrix u, Triangle t, Basis basis) {
        DoubleMatrix first = DoubleMatrix.zeros(u.rows, u.columns);
        DoubleMatrix second = DoubleMatrix.zeros(u.rows, u.columns);

        for (int j = 0; j < 3; j++) {
            Border b = t.getBorders()[j];
            DoubleMatrix temp = t.getT(j).mul(0.5);
            DoubleMatrix sum = t.getAn().add(t.getAAbs());
            temp = temp.mmul(sum);
            temp = temp.mmul(t.getTInv(j));
            temp = temp.mmul(u);
            temp = temp.mul(t.getS(j));
            temp = temp.mmul(basis.F0(j));
            first.addi(temp);

            //todo test this with series of manual tests
            int i = t.getIForFijFormula(j);
            temp = t.getT(j).mul(0.5);
            DoubleMatrix sub = t.getAn().sub(t.getAAbs());
            temp = temp.mmul(sub);
            temp = temp.mmul(t.getTInv(j));
            temp = temp.mmul(b.getNeighborTriangle().getValue().u);
            temp = temp.mul(t.getS(j));
            temp = temp.mmul(basis.F(j, i));
            second.addi(temp);
        }

        DoubleMatrix third = t.getAStr().mmul(u);
        third = third.mmul(basis.KKsi());
        third = third.mul(t.getJacobian());

        DoubleMatrix fourth = t.getBStr().mmul(u);
        fourth = fourth.mmul(basis.KEta());
        fourth = fourth.mul(t.getJacobian());

        DoubleMatrix fifth = DoubleMatrix.zeros(u.rows, u.columns);

        // todo check if this operations produce wrong results (inversing and divieder)
        DoubleMatrix inversedM = Solve.pinv(basis.M());
        DoubleMatrix divider = inversedM.mul(1.0 / t.getJacobian());

        DoubleMatrix dU = third.add(fourth);

        // todo check all values in (0,0) by conditonal debug syop point
//        if ((Math.abs(t.getCenter().x) < 5) && (Math.abs(t.getCenter().y) < 5)) {
//            System.out.println("");
//        }

        dU = dU.sub(first);
        dU = dU.sub(second);
        dU = dU.mmul(divider);

        return dU;
    }
}
