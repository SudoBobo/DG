import org.jblas.DoubleMatrix;
import org.jblas.Solve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.abs;


public class MeshConstructor {

    private static Map<DoubleMatrix, DoubleMatrix> TtoInversedT;
    private static Map<Double, DoubleMatrix> nToT;

    public static Mesh constructHomoMesh(double lambda, double mu, double rho,
                                         double xMin, double xMax, double yMin, double yMax, double fine) {

        TtoInversedT = new HashMap<>(12);
        nToT = new HashMap<>(12);

        // For regular mesh
        assert xMax - xMin == yMax - yMin;

        double sideLength = xMax - xMin;
        int numberOfTriangles = (int) (sideLength / fine) * (int) (sideLength / fine) * 4;

        List<Triangle> triangles = new ArrayList<>(numberOfTriangles);
        for (int i = 0; i < numberOfTriangles; i++) {
            Triangle emptyTriangle = new Triangle();
            triangles.add(emptyTriangle);
        }

        // x, y - координаты левой нижней вершины текущего ПРЯМОУГОЛЬНИКА
        // вертикальный "ход"
        double nXGeneral = 1;
        double nYGeneral = 1;

        double cP = sqrt((lambda + 2 * mu) / rho);
        double cS = sqrt(mu / rho);


        DoubleMatrix R2 = new DoubleMatrix(new double[]{
                -2.0 * mu * nXGeneral * nYGeneral,
                2.0 * mu * nXGeneral * nYGeneral,
                mu * (nXGeneral * nXGeneral - nYGeneral * nYGeneral),
                -nYGeneral * cS,
                nXGeneral * cS

        });

        DoubleMatrix R5 = new DoubleMatrix(new double[]{
                lambda + 2.0 * mu * nXGeneral * nXGeneral,
                lambda + 2.0 * mu * nYGeneral * nYGeneral,
                2.0 * mu * nXGeneral * nYGeneral,
                -nXGeneral * cP,
                -nYGeneral * cP
        });

        DoubleMatrix k = new DoubleMatrix(new double[]{
                2.0 * Math.PI / 25.0, 2.0 * Math.PI / 25.0
        });

        assert cP == 2;
        assert cS == 1;


        // A, B are constant all across the mesh
        DoubleMatrix A = new DoubleMatrix(new double[][]{
                {0, 0, 0, -(lambda + 2 * mu), 0},
                {0, 0, 0, -lambda, 0},
                {0, 0, 0, 0, -mu},
                {-(1 / rho), 0, 0, 0, 0},
                {0, 0, -(1 / rho), 0, 0}
        });

        DoubleMatrix B = new DoubleMatrix(new double[][]{
                {0, 0, 0, 0, -lambda},
                {0, 0, 0, 0, -(lambda + 2 * mu)},
                {0, 0, 0, -mu, 0},
                {0, 0, -(1 / rho), 0, 0},
                {0, -(1 / rho), 0, 0, 0}
        });


        double nX = 1;
        double nY = 0;

        // in homo-mesh case these two matrices are the same for all triangles as they represent
        // their attributes in inner coordinate system (nX = 1, nY = 0)
        DoubleMatrix s = B.mul(nY);
        DoubleMatrix An = A.mul(nX).add(s);


        // as given in formula 8 on page 4
        DoubleMatrix Rpqn = calcRpqn(lambda, mu, cP, cS, nX, nY);
        // as given in formula 15 on page 4
        DoubleMatrix AAbs = calcAAbs(cP, cS, Rpqn);


        DoubleMatrix Mkl = new DoubleMatrix(new double[]{1.0});
        DoubleMatrix Fkl = new DoubleMatrix(new double[]{1.0});

        DoubleMatrix[] Fkl_j = new DoubleMatrix[3];
        for (int j = 0; j < 3; j++) {
            Fkl_j[j] = new DoubleMatrix(new double[]{1.0});
        }


        DoubleMatrix KKsi = new DoubleMatrix(new double[]{0.0});
        DoubleMatrix KMu = new DoubleMatrix(new double[]{0.0});


        int currentTriangle = 0;

        for (double y = yMin; y < yMax; y += fine) {
            // горизонтальный "ход"
            for (double x = xMin; x < xMax; x += fine) {
                // в каждом квадрате по четыре треугольника
                // заполняем каждый в соответствии с ФИЗИЧЕСКОЙ координатой
                for (int numberInRectangle = 0; numberInRectangle < 4; numberInRectangle++) {

                    double centerX = 0;
                    double centerY = 0;

                    // x, y - координаты левой нижней вершины текущего прямоугольника
                    double v[][] = calcVertexes(numberInRectangle, x, y, fine);
                    // j = (x2 - x1)*(y3 - y1) - (x3 - x1)*(y2 - y1)
                    double jacobian = (v[1][0] - v[0][0]) * (v[2][1] - v[0][1]) -
                            (v[2][0] - v[0][0]) * (v[1][1] - v[0][1]);


                    switch (numberInRectangle) {
                        case 0:

                            centerX = (x + x + x + fine / 2.0) / 3.0;
                            centerY = (y + y + fine + y + fine / 2.0) / 3.0;
                            break;
                        case 1:
                            centerX = (x + x + fine / 2 + x + fine) / 3.0;
                            centerY = (y + fine + y + fine + y + fine / 2.0);
                            break;

                        case 2:
                            centerX = (x + fine + x + fine / 2.0 + x + fine) / 3.0;
                            centerY = (y + fine + y + y + fine / 2.0) / 3.0;
                            break;

                        case 3:

                            centerX = (x + x + fine + x + fine / 2.0) / 3.0;
                            centerY = (y + y + y + fine / 2.0);
                            break;
                    }

                    DoubleMatrix centerVector = new DoubleMatrix(new double[]{
                            centerX, centerY
                    });


                    DoubleMatrix u = R2.mmul(sin(k.dot(centerVector))).addi(
                            R5.mmul(sin(k.dot(centerVector))));


                    Border[] borders = makeBorders(xMin, xMax, yMin, yMax, fine, x, y, triangles,
                            currentTriangle, numberInRectangle);

                    // TODO this is ONLY FOR l = 0 case !
//                    DoubleMatrix AStr = calcAStr(A, B, jacobian, v);
//                    DoubleMatrix BStr = calcBStr(A, B, jacobian, v);

                    DoubleMatrix AStr = new DoubleMatrix();
                    DoubleMatrix BStr = new DoubleMatrix();


                    DoubleMatrix T[] = calcTMatrixes(borders);
                    DoubleMatrix TInv[] = calcTInversedMatrixes(T);

                    double[] S = calcSidesLengths(numberInRectangle, fine);


                    Triangle uNeib[] = new Triangle[]{borders[0].getNeighbor(), borders[1].getNeighbor(), borders[2].getNeighbor()};

                    triangles.get(currentTriangle).init(currentTriangle, numberInRectangle, A, B, AAbs, AStr,
                            BStr, S, jacobian, Mkl, Fkl, KKsi, KMu, Fkl_j, T, TInv, u, An);

                    triangles.get(currentTriangle).setNeighbors(uNeib);
                    currentTriangle++;

                }
            }
        }


        // необходимо повторить обход, чтобы заполнить сведения о том, какие треугольники граничат с данным
        // какие координаты у вершин и какие нормали к вершинам

        return new Mesh(triangles);
    }

    // TODO test this
    private static DoubleMatrix calcAAbs(double cP, double cS, DoubleMatrix rpqn) {
        DoubleMatrix invRpqn = inverseMatrix(rpqn);
        DoubleMatrix diagMatrix = DoubleMatrix.diag(new DoubleMatrix(new double[]{
                abs(cP), abs(cS), 0, abs(cS), abs(cP)
        }));

        return rpqn.mmul(diagMatrix).mmul(invRpqn);
    }

    private static DoubleMatrix calcRpqn(double lbd, double mu, double cP, double cS, double nX, double nY) {
        return new DoubleMatrix(new double[][]{
                {lbd + 2 * mu * nX * nX, -2 * mu * nX * nY, nY * nY, -2 * mu * nX * nY, lbd + 2 * mu * nX * nX},
                {lbd + 2 * mu * nY * nY, 2 * mu * nX * nY, nX * nX, 2 * mu * nX * nY, lbd + 2 * mu * nY * nY},
                {2 * mu * nX * nY, mu * (nX * nX - nY * nY), -nX * nY, mu * (nX * nX - nY * nY), 2 * mu * nX * nY},
                {nX * cP, -nY * cS, 0, nY * cS, -nX * cP},
                {nY * cP, nX * cS, 0, -nX * cS, -nY * cP}
        });
    }

    private static double[] calcSidesLengths(int numberInRectangle, double fine) {
        double S[] = new double[3];

        switch (numberInRectangle) {
            case 0:

                S[0] = 1.0;
                S[1] = 0.7071067811865476;
                S[2] = 0.7071067811865476;

                break;

            case 1:

                S[0] = 0.7071067811865476;
                S[1] = 1.0;
                S[2] = 0.7071067811865476;

                break;

            case 2:

                S[0] = 0.7071067811865476;
                S[1] = 0.7071067811865476;
                S[2] = 1.0;

                break;

            case 3:

                S[0] = 1.0;
                S[1] = 0.7071067811865476;
                S[2] = 0.7071067811865476;

                break;

        }

        for (int j = 0; j < 3; j++) {
            S[j] = S[j] * fine;
        }
        return S;
    }

    private static DoubleMatrix[] calcTInversedMatrixes(DoubleMatrix[] t) {
        DoubleMatrix inversed[] = new DoubleMatrix[3];

        for (int j = 0; j < 3; j++) {
            inversed[j] = calcInversed(t[j]);
        }
        return inversed;
    }

    private static DoubleMatrix calcInversed(DoubleMatrix m) {

        if (!TtoInversedT.containsKey(m)) {
            TtoInversedT.put(m, Solve.pinv(m));
        }
        return TtoInversedT.get(m);
    }

    private static DoubleMatrix[] calcTMatrixes(Border[] borders) {
        DoubleMatrix T[] = new DoubleMatrix[3];

        double nX, nY;

        for (int j = 0; j < 3; j++) {
            nX = borders[j].nX;
            nY = borders[j].nY;

            double n = nX * 100 + nY;


            if (!nToT.containsKey(n)) {
                nToT.put(n, new DoubleMatrix(new double[][]{
                        {nX * nX, nY * nY, -2 * nX * nY, 0, 0},
                        {nY * nY, nX * nX, 2 * nX * nY, 0, 0},
                        {nX * nY, -nX * nY, nX * nX - nY * nY, 0, 0},
                        {0, 0, 0, nX, -nY},
                        {0, 0, 0, nY, nX}
                }));
            }
            T[j] = nToT.get(n);
        }

        return T;
    }

    private static DoubleMatrix calcAStr(DoubleMatrix a, DoubleMatrix b, double jacobian, double[][] v) {
        double dKsidX = (v[2][1] - v[0][1]) / jacobian;
        double dKsidY = (v[0][0] - v[2][0]) / jacobian;

        DoubleMatrix f = a.mul(dKsidX);
        DoubleMatrix s = b.mul(dKsidY);

        return f.addi(s);
    }

    private static DoubleMatrix calcBStr(DoubleMatrix a, DoubleMatrix b, double jacobian, double[][] v) {
        double dNudX = (v[0][1] - v[1][1]) / jacobian;
        double dNudY = (v[1][0] - v[0][0]) / jacobian;

        DoubleMatrix f = a.mul(dNudX);
        DoubleMatrix s = b.mul(dNudY);

        return f.addi(s);
    }


    private static double[][] calcVertexes(int numberInRectangle, double x, double y, double fine) {
        // returns [[x,y], [x,y], [x,y]]

        double v[][] = new double[3][2];

        switch (numberInRectangle) {
            case 0:

                // x1, y1
                v[0][0] = x;
                v[0][1] = y;

                // x2, y2
                v[2][0] = x + fine / 2;
                v[2][1] = y + fine / 2;


                // x3, y3
                v[1][0] = x;
                v[1][1] = y + fine;


                break;

            case 1:

                // x1, y1
                v[0][0] = x + fine / 2;
                v[0][1] = y + fine / 2;

                // x2, y2
                v[2][0] = x + fine;
                v[2][1] = y + fine;

                // x3, y3
                v[1][0] = x;
                v[1][1] = y + fine;

                break;

            case 2:

                // x1, y1
                v[0][0] = x + fine;
                v[0][1] = y;

                // x2, y2
                v[2][0] = x + fine;
                v[2][1] = y + fine;


                // x3, y3
                v[1][0] = x + fine / 2;
                v[1][1] = y + fine / 2;

                break;

            case 3:

                // x1, y1
                v[2][0] = x + fine;
                v[2][1] = y;

                // x2, y2
                v[1][0] = x + fine / 2;
                v[1][1] = y + fine / 2;

                // x3, y3
                v[0][0] = x;
                v[0][1] = y;

                break;
        }

        return v;
    }


    // make it parametrised

    private static Border[] makeBorders(double xMin, double xMax, double yMin,
                                        double yMax, double fine, double x, double y, List<Triangle> triangles,
                                        int triangleNumber, int numberInRectangle) {

        boolean isLeftBordered = (x == xMin);
        boolean isRightBordered = (x == (xMax - fine));
        assert ((!isLeftBordered && !isRightBordered) || (isLeftBordered != isRightBordered));

        boolean isUpBordered = (y == (yMax - fine));
        boolean isDownBordered = (y == yMin);
        assert ((!isDownBordered && !isUpBordered) || (isDownBordered != isUpBordered));


        switch (numberInRectangle) {
            case 0:
                return zeroCase(xMin, xMax, fine, triangles, triangleNumber, isLeftBordered);
            case 1:
                return firstCase(xMin, xMax, yMin, yMax, fine, triangles, triangleNumber, isUpBordered);
            case 2:
                return secondCase(xMin, xMax, yMin, yMax, fine, triangles, triangleNumber, isRightBordered);
            case 3:
                return thirdCase(xMin, xMax, yMin, yMax, fine, triangles, triangleNumber, isDownBordered);
        }
        return null;
    }

    private static Border[] zeroCase(double xMin, double xMax, double fine, List<Triangle> triangles,
                                     int triangleNumber, boolean isLeftBordered) {

        Border borders[] = new Border[3];
        double nX = -1;
        double nY = 0;

        int neibIdx = -1;
        if (isLeftBordered) {
            neibIdx = calcIdxOnRight(xMin, xMax, fine, triangleNumber);
        } else {
            neibIdx = triangleNumber - 2;
        }

        borders[0] = new Border(nX, nY, triangles.get(neibIdx));

        // second border

        nX = 0.7071067811865475;
        nY = 0.7071067811865475;

        neibIdx = triangleNumber + 1;

        borders[1] = new Border(nX, nY, triangles.get(neibIdx));

        // third border

        nX = 0.7071067811865475;
        nY = -0.7071067811865475;

        neibIdx = triangleNumber + 3;

        borders[2] = new Border(nX, nY, triangles.get(neibIdx));

        return borders;
    }

    private static Border[] firstCase(double xMin, double xMax, double yMin, double yMax, double fine, List<Triangle> triangles,
                                      int triangleNumber, boolean isUpBordered) {

        Border borders[] = new Border[3];

        // first border

        double nX = -0.7071067811865475;
        double nY = -0.7071067811865475;

        int neibIdx = triangleNumber - 1;

        borders[0] = new Border(nX, nY, triangles.get(neibIdx));

        // second border

        nX = 0.0;
        nY = 1.0;

        neibIdx = -1;
        if (isUpBordered) {
            neibIdx = calcIdxOnDown(xMin, xMax, yMin, yMax, fine, triangleNumber);
        } else {
            neibIdx = calcIdxOfTriangleUp(xMin, xMax, yMin, yMax, fine, triangleNumber);
        }

        borders[1] = new Border(nX, nY, triangles.get(neibIdx));

        // third border

        nX = 0.7071067811865475;
        nY = -0.7071067811865475;

        neibIdx = triangleNumber + 1;

        borders[2] = new Border(nX, nY, triangles.get(neibIdx));

        return borders;
    }

    private static Border[] secondCase(double xMin, double xMax, double yMin, double yMax, double fine, List<Triangle> triangles,
                                       int triangleNumber, boolean isRightBordered) {

        Border borders[] = new Border[3];

        // first border

        double nX = -0.7071067811865475;
        double nY = -0.7071067811865475;

        int neibIdx = triangleNumber + 1;

        borders[0] = new Border(nX, nY, triangles.get(neibIdx));

        // second border

        nX = -0.7071067811865475;
        nY = 0.7071067811865475;

        neibIdx = triangleNumber - 1;

        borders[1] = new Border(nX, nY, triangles.get(neibIdx));

        // third border

        nX = 1;
        nY = 0;

        neibIdx = -1;
        if (isRightBordered) {
            neibIdx = calcIdxOnLeft(xMin, xMax, fine, triangleNumber);
        } else {
            neibIdx = triangleNumber + 2;
        }

        borders[2] = new Border(nX, nY, triangles.get(neibIdx));

        return borders;
    }

    private static Border[] thirdCase(double xMin, double xMax, double yMin, double yMax, double fine, List<Triangle> triangles,
                                      int triangleNumber, boolean isDownBordered) {

        Border borders[] = new Border[3];

        // first border

        double nX = 0;
        double nY = -1;

        int neibIdx = -1;
        if (isDownBordered) {
            neibIdx = calcIdxOnUp(xMin, xMax, yMin, yMax, fine, triangleNumber);
        } else {
            neibIdx = calcIdOfTriangleDown(xMin, xMax, yMin, yMax, fine, triangleNumber);
        }

        borders[0] = new Border(nX, nY, triangles.get(neibIdx));

        // second border

        nX = -0.7071067811865475;
        nY = 0.7071067811865475;

        neibIdx = triangleNumber - 3;

        borders[1] = new Border(nX, nY, triangles.get(neibIdx));

        // third border

        nX = 0.7071067811865475;
        nY = 0.7071067811865475;

        neibIdx = triangleNumber - 1;

        borders[2] = new Border(nX, nY, triangles.get(neibIdx));

        return borders;
    }

    public static int calcIdxOnRight(double xMin, double xMax, double fine, int triangleNumber) {
        // For regular mesh
        // return left neighbor for triangles on the left borders of the mesh
        return ((int) ((xMax - xMin) / fine) * 4 - 2) + triangleNumber;
    }

    public static int calcIdxOnLeft(double xMin, double xMax, double fine, int triangleNumber) {
        // For regular mesh
        return triangleNumber - ((int) ((xMax - xMin) / fine) * 4 - 2);
    }

    public static int calcIdxOnUp(double xMin, double xMax, double yMin, double yMax, double fine, int triangleNumber) {
        // For regular mesh
        // return down neighbor for triangle on the bottom of the mesh
        return triangleNumber + (int) ((((xMax - xMin) / fine) * 4) * (((yMax - yMin) / fine) - 1) - 2);
    }

    public static int calcIdxOnDown(double xMin, double xMax, double yMin, double yMax, double fine, int triangleNumber) {
        // For regular mesh
        return triangleNumber * 2 - calcIdxOnUp(xMin, xMax, yMin, yMax, fine, triangleNumber);
    }

    public static int calcIdxOfTriangleUp(double xMin, double xMax, double yMin, double yMax, double fine, int triangleNumber) {
        // For regular mesh
        return triangleNumber + (int) ((((xMax - xMin) / fine + 1) * 4)) - 2;
    }

    public static int calcIdOfTriangleDown(double xMin, double xMax, double yMin, double yMax, double fine, int triangleNumber) {
        // For regular mesh
        return triangleNumber * 2 - calcIdxOfTriangleUp(xMin, xMax, yMin, yMax, fine, triangleNumber);
    }

    private static DoubleMatrix inverseMatrix(DoubleMatrix orig) {
        return Solve.pinv(orig);
    }
}

