package com.github.sudobobo.meshconstruction;

import com.github.sudobobo.geometry.Point;
import org.jblas.DoubleMatrix;

import static com.github.sudobobo.meshconstruction.SalomeMeshConstructor.calcInversed;
import static java.lang.Math.abs;

public class PhysicalAttributesMatrixes {

    public static DoubleMatrix calcAnMatrix(DoubleMatrix a, DoubleMatrix b, Double nXInnerTriangleSystem, Double nYInnerTriangleSystem) {
        // in homo-mesh case these two matrices are the same for all triangles as they represent
        // their attributes in inner coordinate system (nX = 1, nY = 0)
        DoubleMatrix s = b.mul(nYInnerTriangleSystem);
        DoubleMatrix An = a.mul(nXInnerTriangleSystem).addi(s);
        return An;
    }

    public static DoubleMatrix calcAAbs(double cP, double cS, DoubleMatrix rpqn) {
        DoubleMatrix invRpqn = calcInversed(rpqn);
        DoubleMatrix diagMatrix = DoubleMatrix.diag(new DoubleMatrix(new double[]{
                abs(cP), abs(cS), 0, abs(cS), abs(cP)
        }));

        return rpqn.mmul(diagMatrix).mmul(invRpqn);
    }



    public static DoubleMatrix calcRpqn(double lbd, double mu, double cP, double cS, double nX, double nY) {
        return new DoubleMatrix(new double[][]{
                {lbd + 2 * mu * nX * nX, -2 * mu * nX * nY, nY * nY, -2 * mu * nX * nY, lbd + 2 * mu * nX * nX},
                {lbd + 2 * mu * nY * nY, 2 * mu * nX * nY, nX * nX, 2 * mu * nX * nY, lbd + 2 * mu * nY * nY},
                {2 * mu * nX * nY, mu * (nX * nX - nY * nY), -nX * nY, mu * (nX * nX - nY * nY), 2 * mu * nX * nY},
                {nX * cP, -nY * cS, 0, nY * cS, -nX * cP},
                {nY * cP, nX * cS, 0, -nX * cS, -nY * cP}
        });
    }

    public static DoubleMatrix calcAMatrix(double lambda, double mu, double rho) {
        return new DoubleMatrix(new double[][]{
                {0, 0, 0, -(lambda + 2 * mu), 0},
                {0, 0, 0, -lambda, 0},
                {0, 0, 0, 0, -mu},
                {-(1 / rho), 0, 0, 0, 0},
                {0, 0, -(1 / rho), 0, 0}
        });
    }

    public static DoubleMatrix calcBMatrix(double lambda, double mu, double rho) {
        return new DoubleMatrix(new double[][]{
                {0, 0, 0, 0, -lambda},
                {0, 0, 0, 0, -(lambda + 2 * mu)},
                {0, 0, 0, -mu, 0},
                {0, 0, -(1 / rho), 0, 0},
                {0, -(1 / rho), 0, 0, 0}
        });
    }

    public static DoubleMatrix calcAStr(DoubleMatrix a, DoubleMatrix b, double jacobian, Point[] v) {
        double dKsidX = (v[2].getCoordinates()[1] - v[0].getCoordinates()[1]) / jacobian;
        double dKsidY = (v[0].getCoordinates()[0] - v[2].getCoordinates()[0]) / jacobian;

        DoubleMatrix f = a.mul(dKsidX);
        DoubleMatrix s = b.mul(dKsidY);

        return f.addi(s);
    }

    public static DoubleMatrix calcBStr(DoubleMatrix a, DoubleMatrix b, double jacobian, Point[] v) {
        double dNudX = (v[0].getCoordinates()[1] - v[1].getCoordinates()[1]) / jacobian;
        double dNudY = (v[1].getCoordinates()[0] - v[0].getCoordinates()[0]) / jacobian;

        DoubleMatrix f = a.mul(dNudX);
        DoubleMatrix s = b.mul(dNudY);

        return f.addi(s);
    }
}
