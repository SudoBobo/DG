package com.github.sudobobo.meshconstruction;

import com.github.sudobobo.IO.MeshFileReader;
import com.github.sudobobo.Mesh;
import com.github.sudobobo.geometry.Border;
import com.github.sudobobo.geometry.Point;
import com.github.sudobobo.geometry.Triangle;
import com.github.sudobobo.geometry.Vector;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.github.sudobobo.meshconstruction.PhysicalAttributesMatrixes.*;
import static java.lang.Math.sqrt;

public class SalomeMeshConstructor {

    private static Map<DoubleMatrix, DoubleMatrix> TtoInversedT;
    private static Map<Double, DoubleMatrix> nToT;

    public Mesh constructHomoMesh(Path meshFile, double lambda, double mu, double rho) {


        // order of functions call here is important!

        Point[] points = MeshFileReader.readPoints(meshFile);
        Triangle[] triangles = MeshFileReader.readTriangles(meshFile, points);

        double minDistance = 0.00001;
        Map<Point, Point> pointToReplacementPoint = getPointToReplacementPoint(points, minDistance);

        points = getPointsWithNoDuplicates(points, pointToReplacementPoint);
        changeDuplicateVertexes(triangles, pointToReplacementPoint);

        changePointsOrderToReverseClock(triangles);
        reduceDomains(triangles);

        setNeighborsAndBounds(triangles);
        setConstantPhysicalFields(triangles, lambda, mu, rho);
//        setAbsorbingBoundary(triangles);

        // ltrb if needed

        Mesh mesh = new Mesh();
        mesh.setPoints(points);
        mesh.setTriangles(triangles);

        return mesh;
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
            t.setAAbs(AAbs);

            double jacobian = calcJacobian(t);

            t.setJacobian(jacobian);
            t.setAStr(calcAStr(A, B, jacobian, t.getPoints()));
            t.setBStr(calcBStr(A, B, jacobian, t.getPoints()));
        }
    }

    public static Map<Point, Point> getPointToReplacementPoint(Point[] points, double minDistance) {
        Map<Point, Point> pointToReplacementPoint = new HashMap<>();

        for (Point replacementPoint : points) {

            // check if point already should be replaced
            // in this case it can't be a replacement
            if (pointToReplacementPoint.get(replacementPoint) != null) {
                continue;
            }

            for (Point pointToReplace : points) {

                if (pointToReplace.equals(replacementPoint)) {
                    continue;
                }

                // check if point already should be replaced
                if (pointToReplacementPoint.get(pointToReplace) != null) {
                    continue;
                }

                // TODO test 'distance' method
                if (Point.distance(
                        replacementPoint, pointToReplace
                ) < minDistance) {
                    pointToReplacementPoint.put(pointToReplace, replacementPoint);
                }


            }
        }

        return pointToReplacementPoint;
    }

    public static void reduceDomains(Triangle[] triangles) {

        Set<Integer> domains = new HashSet();

        for (Triangle t : triangles) {
            domains.add(t.getDomain());
        }

        Map<Integer, Integer> oldDomainToNewDomain = new HashMap<>();

        int newDomain = 0;

        for (int oldDomain : domains) {

            if (!oldDomainToNewDomain.containsKey(oldDomain)) {
                oldDomainToNewDomain.put(oldDomain, newDomain);
                newDomain++;
            }
        }

        for (Triangle t : triangles) {
            t.setDomain(
                    oldDomainToNewDomain.get(t.getDomain())
            );
        }
    }

    public static Point[] getPointsWithNoDuplicates(Point[] points, Map<Point, Point> pointToReplacementPoint) {

        int noDuplicateLength = points.length - pointToReplacementPoint.size();
        Point[] pointsWithNoDuplicates = new Point[noDuplicateLength];

        int pnd = 0;

        for (Point point : points) {
            if (!pointToReplacementPoint.containsKey(point)) {
                pointsWithNoDuplicates[pnd] = point;
                pnd++;
            }
        }

        assert (pnd == noDuplicateLength) : "Length on array of points without duplicates was calculated wrong";
        return pointsWithNoDuplicates;
    }

    public static void changeDuplicateVertexes(Triangle[] triangles, Map<Point, Point> pointToReplacementPoint) {

        for (Triangle t : triangles){
            for (int p = 0; p < t.getBorders().length; p++){
                boolean toReplace = (pointToReplacementPoint.get(t.getPoint(p)) != null);
                if (toReplace){
                    t.setPoint(p, pointToReplacementPoint.get(t.getPoint(p)));
                }
            }
        }
    }

    public static void changePointsOrderToReverseClock(Triangle[] triangles) {

        //check if  orientation is reverse clock
        for (Triangle t : triangles) {

            // todo optimise this
            Vector a = new Vector(t.getPoint(0), t.getPoint(1));
            Vector b = new Vector(t.getPoint(1), t.getPoint(2));

            // todo test this
            if (Vector.mult2D(a, b) < 0) {
                Point temp = t.getPoint(1);
                t.setPoint(1, t.getPoint(2));
                t.setPoint(2, temp);
            }
        }
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
                Math.pow((endPoint.getCoordinates()[0] - beginPoint.getCoordinates()[0]), 2)
                + Math.pow((endPoint.getCoordinates()[1] - beginPoint.getCoordinates()[1]), 2)
        );
    }

    private static double[] calcOuterNormal(Point beginPoint, Point endPoint, Point thirdPoint) {

        // todo hardcodeed Rotation matrix with theta == 90
        double[] n = new double[2];
        n[0] = endPoint.getCoordinates()[1] - beginPoint.getCoordinates()[1];
        n[1] = - (endPoint.getCoordinates()[0] - beginPoint.getCoordinates()[0]);

        // normalize normal vector
        double n2 = Math.sqrt(n[0] * n[0] + n[1] * n[1]);

        n[0] /= n2;
        n[1] /= n2;

        return n;
    }



    private static double calcCS(double mu, double rho) {
        return sqrt(mu / rho);
    }

    private static double calcCP(double lambda, double mu, double rho) {
        return sqrt((lambda + 2 * mu) / rho);
    }


    private double calcJacobian(Triangle t) {
        Point[] v = t.getPoints();

        return ((v[1].getCoordinates()[0] - v[0].getCoordinates()[0]) * (v[2].getCoordinates()[1] - v[0].getCoordinates()[1])) -
                ((v[2].getCoordinates()[0] - v[0].getCoordinates()[0]) * (v[1].getCoordinates()[1] - v[0].getCoordinates()[1]));
    }



    private static DoubleMatrix calcTInversedMatrix(DoubleMatrix t) {

        if (!TtoInversedT.containsKey(t)) {
            TtoInversedT.put(t, calcInversed(t));
        }
        return TtoInversedT.get(t);
    }

    public static DoubleMatrix calcInversed(DoubleMatrix m) {
        return Solve.pinv(m);
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