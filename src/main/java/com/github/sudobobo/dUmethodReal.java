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
    private static int k;

    @Override
    // expected not to change 'u' or 't'
    public DoubleMatrix calcDU(DoubleMatrix u, Triangle t, Basis basis) {

        if (k == 421){
            System.out.println("heh");
            System.out.println("hoj");
        }
        k++;

        DoubleMatrix third = t.getAStr().mmul(u);
        third = third.mmul(basis.KKsi());

        DoubleMatrix fourth = t.getBStr().mmul(u);
        fourth = fourth.mmul(basis.KEta());

        DoubleMatrix first = DoubleMatrix.zeros(u.rows, u.columns);
        DoubleMatrix second = DoubleMatrix.zeros(u.rows, u.columns);

        // consider using mul for matrix * number, mmul form matrix * matrix
        for (int j = 0; j < 3; j++) {
            Border b = t.getBorders()[j];

            DoubleMatrix temp = t.getT(j).mul(0.5);

            DoubleMatrix sum = t.getAn().add(t.getAAbs());
            sum = sum.mmul(t.getTInv(j));

            temp = temp.mmul(sum);
            temp = temp.mmul(u);
            temp = temp.mul(t.getS(j));
            temp = temp.mmul(basis.F0(j));
            first = first.add(temp);

            //todo test this with series of manual tests
            int i = t.getIForFijFormula(j);

            temp = t.getT(j).mul(0.5);
            DoubleMatrix sub = t.getAn().sub(t.getAAbs());
            temp = temp.mmul(sub);
            temp = temp.mmul(t.getTInv(j));
            temp = temp.mmul(b.getNeighborTriangle().getValue().u);
            temp = temp.mul(t.getS(j));
            temp = temp.mmul(basis.F(j, i));
            second = second.add(temp);
        }

        first = first.mul(1 / t.getJacobian());
        second = second.mul(1 / t.getJacobian());

        DoubleMatrix dU = third.add(fourth);
        dU = dU.sub(first.add(second));


        DoubleMatrix inversedM = Solve.pinv(basis.M());
        dU = dU.mmul(inversedM);

        return dU;
    }
}
