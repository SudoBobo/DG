package com.github.sudobobo;

import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Border;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

public class DendUmethodWithNoDebug implements dUmethod {

    private static final DoubleMatrix G =
        DoubleMatrix.diag(new DoubleMatrix(new double[]{-1.0, 1, -1.0, 1.0, 1.0}));

    @Override
    public DoubleMatrix calcDU(DoubleMatrix u, Triangle t, Basis basis) {
        DoubleMatrix M_inv = Solve.pinv(basis.M());
        DoubleMatrix Dx = basis.KKsi();
        DoubleMatrix Dy = basis.KEta();

        DoubleMatrix Az = t.getAStr();
        DoubleMatrix Bz = t.getBStr();

        double J = t.getJacobian();

        DoubleMatrix AUDX = Az.mmul(u);
        AUDX = AUDX.mmul(Dx);

        DoubleMatrix BUDY = Bz.mmul(u);
        BUDY = BUDY.mmul(Dy);

        DoubleMatrix AplusA = t.getA().add(t.getAAbs());
        DoubleMatrix AminusA = t.getA().sub(t.getAAbs());

        DoubleMatrix [] edgeFlux = new DoubleMatrix[3];
        for (int j = 0; j < 3; j++) {
            DoubleMatrix Fin = basis.F0(j);

            Border b = t.getBorders()[j];
            double Sj = b.getS();

            DoubleMatrix Tj = b.getT();
            DoubleMatrix T1j = b.getTInv();

            DoubleMatrix X1j = AplusA.mmul(T1j);
            X1j = X1j.mmul(u);
            DoubleMatrix out = X1j.mmul(Fin);

            DoubleMatrix in = null;
            // should distinguish between normal border and absorbing border
            if(!b.isEdgeOfMesh() || (b.isEdgeOfMesh() && b.getBorderType().equals("enclosed"))) {
                int i = t.getIForFijFormula(j);
                DoubleMatrix Fout = basis.F(j, i);

                DoubleMatrix X2j = AminusA.mmul(T1j);
                DoubleMatrix uJ = b.getNeighborTriangle().getValue().u;
                X2j = X2j.mmul(uJ);
                in = X2j.mmul(Fout);
            }

            if (b.isEdgeOfMesh() && b.getBorderType().equals("absorbing")) {
                in = DoubleMatrix.zeros(out.rows, out.columns);
            }

            if (b.isEdgeOfMesh() && b.getBorderType().equals("free")) {
                DoubleMatrix X2j = AminusA.mmul(G);
                X2j= X2j.mmul(T1j);
                X2j = X2j.mmul(u);
                in = X2j.mmul(Fin);
            }

            edgeFlux[j] = in.add(out);
            edgeFlux[j] = (Tj.mul(-Sj / (2.0 * J))).mmul(edgeFlux[j]);
        }

        DoubleMatrix staticSourcesSum = new DoubleMatrix(AUDX.rows, AUDX.columns);
        // static source processing
        if (t.hasStaticSource()){
            // calculate
            for (StaticSource s: t.getStaticSources()) {
                // make use of time part
                // add
            }
        }

        // moving source processing
        // it is separated from the static case as it should be realized in a
        // different way

        DoubleMatrix sum = edgeFlux[0].add(edgeFlux[1]);
        sum = sum.add(edgeFlux[2]);
        sum = sum.add(AUDX);
        sum = sum.add(BUDY);
        sum = sum.add(staticSourcesSum);
        sum = sum.mmul(M_inv);

        return sum;
    }
}
