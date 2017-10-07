package com.github.sudobobo;

import com.github.sudobobo.IO.MeshFileReader;
import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Border;
import com.github.sudobobo.geometry.Point;
import com.github.sudobobo.geometry.Triangle;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class SalomeMeshConstructor {

    private static Map<DoubleMatrix, DoubleMatrix> TtoInversedT;
    private static Map<Double, DoubleMatrix> nToT;

    public Mesh constructHomoMesh(Path meshFile, double lambda, double mu, double rho, double spatialStep,
                                  double spatialStepForNumericalIntegration, Basis basis) {

        Point[] points = MeshFileReader.readPoints(meshFile);
        Triangle[] triangles = MeshFileReader.readTriangles(meshFile);

        double minDistance = 0.00001;
        Map<Integer, Integer> pointToReplacementPoint = getPointToReplacementPoint(points, minDistance);

        removeDuplicatePoints(points, pointToReplacementPoint);
        changeDuplicateVertexes(triangles, pointToReplacementPoint);

        changePointsOrderToReverseClock(triangles, points);
        reduceDomains(triangles);

        setNeighborsAndBounds(triangles);
        setConstantPhysicalFields(triangles, lambda, mu, rho);
        setAbsorbingBoundary(triangles);

        // ltrb if needed

        return new Mesh(triangles, points);
    }

    private void setConstantPhysicalFields(Triangle[] triangles, double lambda, double mu, double rho) {


        Double nXInnerTriangleSystem = 1.0;
        Double nYInnerTriangleSystem = 0.0;

        DoubleMatrix A = calcAMatrix(lambda, mu, rho);
        DoubleMatrix B = calcBMatrix(lambda, mu, rho);

        DoubleMatrix An = calcAnMatrix(A, B, nXInnerTriangleSystem, nYInnerTriangleSystem);

        double cP = calcCP(lambda, mu, rho);
        double cS = calcCS(mu, rho);

        DoubleMatrix Rpqn = calcRpqn(lambda, mu, cP, cS, nXInnerTriangleSystem, nYInnerTriangleSystem);
        DoubleMatrix AAbs = calcAAbs(cP, cS, Rpqn);


        for (Triangle t : triangles) {
            t.setA(A);
            t.setB(B);
            t.setAn(An);

            double jacobian = (v[1][0] - v[0][0]) * (v[2][1] - v[0][1]) -
                    (v[2][0] - v[0][0]) * (v[1][1] - v[0][1]);

            DoubleMatrix AStr = calcAStr(A, B, jacobian, v);
            DoubleMatrix BStr = calcBStr(A, B, jacobian, v);


        }
    }

    private void setBasis(Triangle[] triangles, Basis basis) {
        for (Triangle t : triangles) {

            t
        }
    }


    public static Mesh constructHomoMesh(double lambda, double mu, double rho, double spatialStep, double spatialStepForNumericalIntegration, Basis basis) {
        return null;
    }

    public static Map<Integer, Integer> getPointToReplacementPoint(Point[] points, double minDistance) {
        Map<Integer, Integer> pointToReplacementPoint = new HashMap<>();

        for (Point replacementPoint : points) {

            // check if point already should be replaced
            // in this case it can't be replacement
            if (pointToReplacementPoint.get(replacementPoint.getId()) != null) {
                continue;
            }

            for (Point pointToReplace : points) {

                if (pointToReplace.getId() == replacementPoint.getId()) {
                    continue;
                }

                // check if point already should be replaced
                if (pointToReplacementPoint.get(pointToReplace.getId()) != null) {
                    continue;
                }

                // TODO test 'distance' method
                if (Point.distance(
                        replacementPoint, pointToReplace
                ) < minDistance) {
                    pointToReplacementPoint.put(pointToReplace.getId(), replacementPoint.getId());
                }


            }
        }

        return pointToReplacementPoint;
    }

    public static void removeDuplicatePoints(Point[] points, Map<Integer, Integer> pointToReplacementPoint) {

        int noDuplicateLength = points.length - pointToReplacementPoint.size();
        Point[] pointsWithNoDuplicates = new Point[noDuplicateLength];

        int pnd = 0;

        for (Point point : points) {
            if (pointToReplacementPoint.get(point.getId()) == null) {
                pointsWithNoDuplicates[pnd] = point;
                pnd++;
            }
        }

        assert (pnd == noDuplicateLength) : "Length on array of points without duplicates was calculated wrong";
        return pointsWithNoDuplicates;
    }

    public static void changeDuplicateVertexes(Triangle[] triangles, Map<Integer, Integer> pointToReplacementPoint) {

        for (Triangle triangle : triangles) {
            for (int p = 0; p < triangle.getPointsId().length; p++) {

                int pointId = triangle.getPointsId()[p];
                if (pointToReplacementPoint.get(pointId) != null) {
                    triangle.getPointsId()[p] = pointToReplacementPoint.get(pointId);
                }
            }
        }
    }

    public static void changePointsOrderToReverseClock(Triangle[] triangles, Point[] points) {
        assert (false) : "Not implemented yet!";

//        //check orientation is reverse clock (left)
//        Vector a = new Vector(ps[tPoints[0]], ps[tPoints[1]]);
//        Vector b = new Vector(ps[tPoints[0]], ps[tPoints[2]]);
//        if (a.mult2D(b) < 0) {//cменить порядок точек
//            int tmp = tPoints[1];
//            tPoints[1] = tPoints[2];
//            tPoints[2] = tmp;
    }

    public static void setNeighborsAndBounds(Triangle[] triangles) {


        Border borderNotSet = Border.builder().build();
        Triangle triangleNotSet = Triangle.builder().build();

        for (Triangle t : triangles) {

            Border[] borders = new Border[3];

            for (int b = 0; b < 3; b++) {

                borders[b] = Border.builder()
                        .beginPoint(t.getPoints()[b])
                        .endPoint(t.getPoints()[(b + 1) % 3])
                        .borderNumber(b)
                        .outerNormal(calcOuterNormal(
                                t.getPoints()[b], t.getPoints()[(b + 1) % 3], t.getPoints()[(b + 2) % 3])
                        )
                        .neighborBorder(borderNotSet)
                        .neighborTriangle(triangleNotSet)
                        .build();

                borders[b].setS(calcBorderS(borders[b].getBeginPoint(), borders[b].getEndPoint()));
                borders[b].setT(calcTMatrix(borders[b].getOuterNormal()[0], borders[b].getOuterNormal()[1]));
                borders[b].setTInv(calcTInversedMatrix(borders[b].getT()));

            }
            t.setBorders(borders);
        }

        for (Triangle t : triangles) {
            for (Border b : t.getBorders()) {

                // check if neibghor border is already set for this border
                if (b.getNeighborBorder() != borderNotSet) {
                    continue;
                }

                // try to find neighbor border among triangles' borders
                for (Triangle potentialNeib : triangles) {

                    if (potentialNeib == t) {
                        continue;
                    }


                    for (Border potentialNeibBorder : potentialNeib.getBorders()) {

                        if (potentialNeibBorder.getNeighborBorder() != borderNotSet) {
                            continue;
                        }


                        if (Border.doBordersPointsMatch(b, potentialNeibBorder)) {
                            b.setNeighborBorder(potentialNeibBorder);
                            b.setNeighborTriangle(potentialNeib);

                            potentialNeibBorder.setNeighborBorder(b);
                            potentialNeibBorder.setNeighborTriangle(t);

                            b.setEdgeOfMesh(false);
                            potentialNeibBorder.setEdgeOfMesh(false);

                            break;
                        }
                    }


                    if (b.getNeighborBorder() != borderNotSet) {
                        break;
                    }
                }

                // todo remove hardcoded ABSORBING_BOUNDARY
                // if there is no such neigbhor border that our border is on the edge of mesh

                b.setEdgeOfMesh(true);
            }
        }

    }

    private static double calcBorderS(Point beginPoint, Point endPoint) {
        return Math.sqrt(
                Math.pow((endPoint.getCoordinates()[0] - beginPoint.getCoordinates()[1]), 2)
                + Math.pow(endPoint.getCoordinates()[1] - beginPoint)
        )
    }

    private static double[] calcOuterNormal(Point beginPoint, Point endPoint, Point thirdPoint) {

        // todo hardcodeed Rotation matrix with theta == 90
        double[] n = new double[2];
        n[0] = endPoint.getCoordinates()[1] - beginPoint.getCoordinates()[1];
        n[1] = - (endPoint.getCoordinates()[0] - beginPoint.getCoordinates()[0]);
        return n;
    }

    public static void reduceDomains(Triangle[] triangles) {
        assert (false) : "not implemented yet";
//        t[tIndex].domain = fileData.triangles[tIndex][fileData.triangles[tIndex].length - 1];
//        domains.add(t[tIndex].domain);
//    }
//    //domain to index
//    Integer[] domainsUnique = domains.toArray(new Integer[domains.size()]);
//        Arrays.sort(domainsUnique);
//    // а тут заполняем в треугольниках поле "домен" 0,1,2 (индексами листа доменов)
//    // то есть были домены {1,33,100}
//    // а стали {0,1,2}
//    List<Integer> domainsList = new ArrayList(Arrays.asList(domainsUnique));
//        for (int tIndex = 0; tIndex < fileData.triangles.length; tIndex++) {
//        t[tIndex].domain = domainsList.indexOf(t[tIndex].domain);
//        if (t[tIndex].domain < 0) {
//            throw new AderException("domain identification problem!");
//        }
    }

    private static double calcCS(double mu, double rho) {
        return sqrt(mu / rho);
    }

    private static double calcCP(double lambda, double mu, double rho) {
        return sqrt((lambda + 2 * mu) / rho);
    }

    private static DoubleMatrix calcAnMatrix(DoubleMatrix a, DoubleMatrix b, Double nXInnerTriangleSystem, Double nYInnerTriangleSystem) {
        // in homo-mesh case these two matrices are the same for all triangles as they represent
        // their attributes in inner coordinate system (nX = 1, nY = 0)
        DoubleMatrix s = b.mul(nYInnerTriangleSystem);
        DoubleMatrix An = a.mul(nXInnerTriangleSystem).addi(s);
        return An;
    }

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

    private static DoubleMatrix calcAMatrix(double lambda, double mu, double rho) {
        return new DoubleMatrix(new double[][]{
                {0, 0, 0, -(lambda + 2 * mu), 0},
                {0, 0, 0, -lambda, 0},
                {0, 0, 0, 0, -mu},
                {-(1 / rho), 0, 0, 0, 0},
                {0, 0, -(1 / rho), 0, 0}
        });
    }

    private static DoubleMatrix calcBMatrix(double lambda, double mu, double rho) {
        return new DoubleMatrix(new double[][]{
                {0, 0, 0, 0, -lambda},
                {0, 0, 0, 0, -(lambda + 2 * mu)},
                {0, 0, 0, -mu, 0},
                {0, 0, -(1 / rho), 0, 0},
                {0, -(1 / rho), 0, 0, 0}
        });
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

    private static DoubleMatrix calcTInversedMatrix(DoubleMatrix t) {
        return calcInversed(t);
    }

    private static DoubleMatrix calcInversed(DoubleMatrix m) {

        if (!TtoInversedT.containsKey(m)) {
            TtoInversedT.put(m, Solve.pinv(m));
        }
        return TtoInversedT.get(m);
    }

    private static DoubleMatrix calcTMatrix(double nX, double nY) {
        // TODO probably wrong, check this
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
        return nToT.get(n);
    }
}