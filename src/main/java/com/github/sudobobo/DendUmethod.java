package com.github.sudobobo;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Border;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;



public class DendUmethod implements dUmethod {

    private static void pr(DoubleMatrix m) {
        for (int r = 0; r < m.rows; r++){
            System.out.println(m.getRow(r));
        }
        System.out.println("\n");
    }

    private static void pr (double d) {
        System.out.println(d);
    }


    private static int k;

    @Override
    public DoubleMatrix calcDU(DoubleMatrix u, Triangle t, Basis basis) {

        // можно нахерачить вывод на каждом шаге по координате центра
        boolean toWrite = false;
        if (k == 421){
            System.out.println(421);
            System.out.println(t.getCenter().x);
            System.out.println(t.getCenter().y);
            System.out.println(u);
            toWrite = true;
        }
        k++;

        DoubleMatrix M_inv = Solve.pinv(basis.M());
        DoubleMatrix Dx = basis.KKsi();
        DoubleMatrix Dy = basis.KEta();

        DoubleMatrix Az = t.getAStr();
        DoubleMatrix Bz = t.getBStr();

        double J = t.getJacobian();

        if (toWrite){

            System.out.println("U");
            pr(u);
            System.out.println("M_inv");
            pr(M_inv);

            System.out.println("Dx");
            pr(Dx);

            System.out.println("Dy");
            pr(Dy);

            System.out.println("Az Astr");
            pr(Az);

            System.out.println("Bz");
            pr(Bz);

            System.out.println("J");
            pr(J);
        }

        DoubleMatrix AUDX = Az.mmul(u);

        if (toWrite){
            System.out.println("Az * U");
            pr(AUDX);
        }

        AUDX = AUDX.mmul(Dx);

        if (toWrite){
            System.out.println("Az * U * Dx = AUDX");
            pr(AUDX);
        }

        DoubleMatrix BUDY = Bz.mmul(u);

        if (toWrite){
            System.out.println("Bz * U");
            pr(BUDY);
        }

        BUDY = BUDY.mmul(Dy);

        if (toWrite){
            System.out.println("Bz * U * DY");
            pr(BUDY);
        }

        DoubleMatrix AplusA = t.getA().add(t.getAAbs());
        DoubleMatrix AminusA = t.getA().sub(t.getAAbs());
        if (toWrite){
            System.out.println("AplusA");
            pr(AplusA);
            System.out.println("AminusA");
            pr(AminusA);
        }

        DoubleMatrix [] edgeFlux = new DoubleMatrix[3];
        for (int j = 0; j < 3; j++) {

            // check this, should be the shame :)
            int i = t.getIForFijFormula(j);
            DoubleMatrix Fin = basis.F0(j);
            DoubleMatrix Fout = basis.F(j, i);

            if (toWrite){
                System.out.println("j, i");
                System.out.println(j);
                System.out.println(i);

                System.out.println("Fin = F0[j]");
                pr(Fin);

                System.out.println("Fout = F[j,i]");
                pr(Fout);
            }

            Border b = t.getBorders()[j];
            double Sj = b.getS();

            if (toWrite){
                System.out.println("Sj");
                System.out.println(Sj);
            }

            DoubleMatrix Tj = b.getT();
            DoubleMatrix T1j = b.getTInv();

            if (toWrite){
                System.out.println("Tj and T1J");
                pr(Tj);
                pr(T1j);
            }

            DoubleMatrix X1j = AplusA.mmul(T1j);

            if (toWrite){
                System.out.println("A+A * T1j");
                pr(X1j);
            }

            X1j = X1j.mmul(u);

            if (toWrite){
                System.out.println("A+A * T1j * u");
                pr(X1j);
            }

            DoubleMatrix out = X1j.mmul(Fin);

            if (toWrite){
                System.out.println("out");
                pr(out);
            }

            DoubleMatrix X2j = AminusA.mmul(T1j);

            if (toWrite){
                System.out.println("A-A * T1J");
                pr(X2j);
            }

            DoubleMatrix uJ = b.getNeighborTriangle().getValue().u;

            if (toWrite){
                System.out.println("uJ");
                pr(uJ);
            }

            X2j = X2j.mmul(uJ);

            if (toWrite){
                System.out.println("A-A * T1j * uJ");
                pr(X2j);
            }

            DoubleMatrix in = X2j.mmul(Fout);

            if (toWrite){
                System.out.println("in");
                pr(in);
            }

            // lol
//                in = in.mul(10);
            // lol
            edgeFlux[j] = in.add(out);
            if (toWrite){
                System.out.println("edge flux");
                pr(edgeFlux[j]);
            }

            edgeFlux[j] = (Tj.mul(-Sj / (2.0 * J))).mmul(edgeFlux[j]);

            if (toWrite){
                System.out.println("edge flux * 1/J and stuff");
                pr(edgeFlux[j]);
            }

        }

        DoubleMatrix sum = edgeFlux[0].add(edgeFlux[1]);

        if (toWrite){

        }

        sum = sum.add(edgeFlux[2]);

        if (toWrite){
            System.out.println("Sum as edge Fluxes");
            pr(sum);
        }

        sum = sum.add(AUDX);

        if (toWrite){
            System.out.println("Sum + AUDX");
            pr(sum);
        }

        sum = sum.add(BUDY);

        if (toWrite){
            System.out.println("Sum + BUDY");
            pr(sum);
        }
        sum = sum.mmul(M_inv);

        if (toWrite){
            System.out.println("Sum * M_inv - result");
            pr(sum);
        }

        toWrite = false;
        return sum;
    }
}
