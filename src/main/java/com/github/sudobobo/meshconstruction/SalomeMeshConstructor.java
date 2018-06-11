package com.github.sudobobo.meshconstruction;

import com.github.sudobobo.IO.MeshFileReader;
import com.github.sudobobo.basis.PreLinear2DBasis;
import com.github.sudobobo.geometry.*;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.github.sudobobo.meshconstruction.PhysicalAttributesMatrixes.*;
import static java.lang.Math.sqrt;

public class SalomeMeshConstructor {
    private static Map<DoubleMatrix, DoubleMatrix> TtoInversedT;
    private static Map<Double, DoubleMatrix> nToT;
    private static Map<Integer, String> IdxToBorderType;

    public static Mesh constructHomoMesh(Path meshFile, Domain[] domains,
                                         MeshBorder[] borders, SourceConfig [] sources,
                                         PreLinear2DBasis basis, double integrationStep) {
        // order of functions call here is important! (as these functions have output params)

        IdxToBorderType = new HashMap<Integer, String>();
        for (MeshBorder mb : borders) {
            IdxToBorderType.put(mb.getIndex(), mb.getBorderType());
        }

        Point[] points = MeshFileReader.readPoints(meshFile);
        Triangle[] triangles = MeshFileReader.readTriangles(meshFile, points, domains);

        Mesh mesh = new Mesh();
        mesh.setPoints(points);
        mesh.setTriangles(triangles);

        double minDistance = 0.00001;
        Map<Point, Point> pointToReplacementPoint = getPointToReplacementPoint(points, minDistance);

        points = getPointsWithNoDuplicates(points, pointToReplacementPoint);
        changeDuplicateVertexes(triangles, pointToReplacementPoint);

        changePointsOrderToReverseClock(triangles);

        setNeighborsAndBounds(mesh, borders);
        setIJ(triangles);
        setConstantPhysicalFields(triangles, domains);
        setSources(triangles, sources, basis, integrationStep);
        return mesh;
    }

    private static boolean matchPointSource(SourceConfig s, Triangle t){
         return t.isInTriangle(s.getPoint()[0], s.getPoint()[1]);
    }

    // static means spatial static
    private static void setSources(Triangle[] triangles, SourceConfig[] sources,
                                   PreLinear2DBasis basis, double integrationStep) {
        for (SourceConfig s: sources){
            for (Triangle t: triangles){
                if(matchPointSource(s, t)){
                    t.addPointSource(
                        new SinPointSource(s, basis, t, integrationStep));
                    break;
                    //point static source may be associated only with one
                    //triangle
                }

            }
        }
    }

    public static void setIJ(Triangle[] triangles) {
        for (Triangle triangle : triangles) {
            triangle.setIJ();
        }
    }

    private static void setConstantPhysicalFields(Triangle[] triangles, Domain[] domains) {
        Double nXInnerTriangleSystem = 1.0;
        Double nYInnerTriangleSystem = 0.0;

        // domain section - create matrices for all domains
        Map<Domain, Map<String, DoubleMatrix>> domainToMatricesSet = calcDomainToMatricesSet(domains, nXInnerTriangleSystem,
                nYInnerTriangleSystem);

        for (Triangle t : triangles) {
            Map<String, DoubleMatrix> set = domainToMatricesSet.get(t.getDomain());
            t.setA(set.get("A"));
            t.setB(set.get("B"));
            t.setAn(set.get("An"));
            t.setAAbs(set.get("AAbs"));
            t.setRpqn(set.get("Rpqn"));

            double jacobian = calcJacobian(t);

            t.setJacobian(jacobian);
            t.setAStr(calcAStr(t.getA(), t.getB(), jacobian, t.getPoints()));
            t.setBStr(calcBStr(t.getA(), t.getB(), jacobian, t.getPoints()));
        }
    }

    private static Map<Domain, Map<String, DoubleMatrix>> calcDomainToMatricesSet
        (Domain[] domains, Double nXInnerTriangleSystem, Double nYInnerTriangleSystem) {

        Map<Domain, Map<String, DoubleMatrix>> domainToMatricesSet = new HashMap<>();

        for (Domain d : domains) {

            Map<String, DoubleMatrix> set = new HashMap<>();
            domainToMatricesSet.put(d, set);

            DoubleMatrix A = calcAMatrix(d.getLambda(), d.getMu(), d.getRho());
            DoubleMatrix B = calcBMatrix(d.getLambda(), d.getMu(), d.getRho());

            set.put("A", A);
            set.put("B", B);

            DoubleMatrix An = calcAnMatrix(A, B, nXInnerTriangleSystem, nYInnerTriangleSystem);
            set.put("An", An);

            double cP = calcCP(d.getLambda(), d.getMu(), d.getRho());
            double cS = calcCS(d.getMu(), d.getRho());

            DoubleMatrix Rpqn = calcRpqn(d.getLambda(), d.getMu(), cP, cS, nXInnerTriangleSystem, nYInnerTriangleSystem);
            DoubleMatrix AAbs = calcAAbs(cP, cS, Rpqn);

            set.put("Rpqn", Rpqn);
            set.put("AAbs", AAbs);
        }
        return domainToMatricesSet;
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

        int numberOfVertexInTriangle = 3;

        for (Triangle t : triangles) {
            for (int p = 0; p < numberOfVertexInTriangle; p++) {
                boolean toReplace = (pointToReplacementPoint.get(t.getPoint(p)) != null);
                if (toReplace) {
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

    public static void setNeighborsAndBounds(Mesh mesh, MeshBorder[] meshBorders) {

        if (TtoInversedT == null) {
            TtoInversedT = new HashMap<DoubleMatrix, DoubleMatrix>();
        }

        if (nToT == null) {
            nToT = new HashMap<Double, DoubleMatrix>();
        }

        Border borderNotSet = Border.builder().build();
        Triangle triangleNotSet = Triangle.builder().build();

        for (Triangle t : mesh.getTriangles()) {

            Border[] borders = new Border[3];

            for (int b = 0; b < 3; b++) {
                borders[b] = Border.builder()
                        .beginPoint(t.getPoints()[b])
                        .endPoint(t.getPoints()[(b + 1) % 3])
                        .borderNumber(b)
                        .outerNormal(calcOuterNormal(
                                t.getPoints()[b], t.getPoints()[(b + 1) % 3])
                        )
                        .neighborBorder(borderNotSet)
                        .neighborTriangle(triangleNotSet)
                        .build();

                borders[b].setS(calcBorderS(borders[b].getBeginPoint(), borders[b].getEndPoint()));
                borders[b].setT(calcTMatrix(borders[b].getOuterNormal()[0], borders[b].getOuterNormal()[1]));
                borders[b].setTInv(calcTInversedMatrix(borders[b].getT()));
                borders[b].setTriangle(t);

            }
            t.setBorders(borders);
        }

        for (Triangle t : mesh.getTriangles()) {
            for (Border b : t.getBorders()) {
                findNeibAndSetBorder(t, mesh, b, borderNotSet);
            }
        }
    }

    // There are two possibilities for a point:
    // 1) point has a border index (set by Salome) and border with this
    //      index is described in config.yml
    // 2) point has a border index and border with this index is not
    //      described in config.yml
    //
    // In the pair of point which forms a border there are two 'ok' cases:
    // 1) Both of point's border indexes refers to the same described border
    //      type. In that case this border type is taken.
    // 2) One point's border index refers to explicitly described
    //      border type. Another point's border index refers to non-described
    //      border type. In this case resulting border must have border type
    //      described by the first point.
    //
    // All over cases are wrong.
    private static String getBorderType(Point bP, Point eP) {
        boolean isSetForA = false;
        boolean isSetForB = false;
        for (Map.Entry<Integer, String> b : IdxToBorderType.entrySet()) {
            if (bP.getBorderIndex() == b.getKey()) {
                isSetForA = true;
            }

            if (eP.getBorderIndex() == b.getKey()){
                isSetForB = true;
            }
        }

        String res = null;

        if (isSetForA && isSetForB && (bP.getBorderIndex() == eP.getBorderIndex())) {
            res = IdxToBorderType.get(bP.getBorderIndex());
        }

        if (isSetForA && (!isSetForB)) {
            res = IdxToBorderType.get(bP.getBorderIndex());
        }

        if (isSetForB && (!isSetForA)) {
            res = IdxToBorderType.get(eP.getBorderIndex());
        }

        if (!(isSetForA) && (!(isSetForB))){
            // -1 is border index for default border
            res =  IdxToBorderType.get(-1);
        }

        if (res == null) {
            try {
                throw new Exception("Error with determing border type on border types of " +
                    "points: " + bP.getBorderIndex() + " " + eP.getBorderIndex());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    private static void findNeibAndSetBorder(Triangle t, Mesh mesh, Border b,
                                             Border borderNotSet) {
        //@ todo optimize this

        // check if neighbor border is already set for this border
        if (b.getNeighborBorder() != borderNotSet) {
            return;
        }

        // try to find neighbor border among triangles' borders
        for (Triangle potentialNeib : mesh.getTriangles()) {

            if (potentialNeib == t) {
                continue;
            }

            for (Border potentialNeibBorder : potentialNeib.getBorders()) {
                // we don't consider neighbor borders which already have neighbors
                // because neighbor borders are set in pairs

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
                    b.setBorderType("normal");
                    potentialNeibBorder.setBorderType("normal");

                    // debug check
//                    System.out.println(doBordersHaveSameDirection(b, b.getNeighborBorder()));
                    //

                    return;
                }
            }
        }

        // if there is no such neighbor border that our border is on the edge of mesh
        b.setEdgeOfMesh(true);
        b.setNeighborTriangle(null);
        b.setNeighborBorder(null);

        // Setting special edge condition

        String borderType = getBorderType(b.getBeginPoint(), b.getEndPoint());
        b.setBorderType(borderType);

        // Debug print
        System.out.println(b.getBeginPoint());
        System.out.println(b.getEndPoint());
        System.out.println(b.getBorderType());

        if (b.getBorderType().equals("enclosed")) {

            Border enclosedBorder = findEnclosedBorder(b, mesh);
//            System.out.println(enclosedBorder);
            Triangle enclosedTriangle = enclosedBorder.getTriangle();

            b.setNeighborBorder(enclosedBorder);
            b.setNeighborTriangle(enclosedTriangle);

            enclosedBorder.setNeighborBorder(b);
            enclosedBorder.setNeighborTriangle(t);

            // debug check
//            System.out.println(doBordersHaveSameDirection(b, b.getNeighborBorder()));
            //
        }
        // no special fields must be set in case of absorbing/free boundary
    }

    private static boolean doBordersHaveSameDirection(Border b1, Border b2) {
        return (b1.getBeginPoint().equals(b2.getBeginPoint()));
    }

    // assume square mesh
    private static Border findEnclosedBorder(Border border, Mesh mesh) {
        Point b = border.getBeginPoint();
        Point e = border.getEndPoint();
        Point rb = mesh.getRBPoint();
        Point lt = mesh.getLTPoint();

        // border lies on the horizontal edge
        if (b.y == e.y){
            // border lies on the bottom edge
            if (b.y == rb.y){
                return mesh.findBorder(b.x, lt.y, e.x, lt.y);
            }
            //border lies on the top edge
            if (b.y == lt.y){
                return mesh.findBorder(b.x, rb.y, e.x, rb.y);
            }
        }

        // border lies on the vertical edge
        if (b.x == e.x){
            //border lies on the left edge
            if (b.x == lt.x){
                return mesh.findBorder(rb.x, b.y, rb.x, e.y);
            }
            //border lies on the right edge
            if (b.x == rb.x){
                return mesh.findBorder(lt.x, b.y, lt.x, e.y);
            }
        }

        System.out.println("findEnclosedBorder() failed to find enclosed border" +
            "enclosed border elements must lie on the edge of square mesh");

        assert false : "findEnclosedBorder() failed to find enclosed border" +
            "enclosed border elements must lie on the edge of square mesh";

        return null;
    }


    private static double calcBorderS(Point beginPoint, Point endPoint) {
//        return Math.sqrt(
//                Math.pow((endPoint.getCoordinates()[0] - beginPoint.getCoordinates()[0]), 2)
//                        + Math.pow((endPoint.getCoordinates()[1] - beginPoint.getCoordinates()[1]), 2)
//        );

        return Math.sqrt(
                Math.pow((endPoint.x - beginPoint.x), 2)
                        + Math.pow((endPoint.y - beginPoint.y), 2)
        );
    }

    private static double[] calcOuterNormal(Point beginPoint, Point endPoint) {

        // todo hardcodeed Rotation matrix with theta == 90
        double[] n = new double[2];
//        n[0] = endPoint.getCoordinates()[1] - beginPoint.getCoordinates()[1];
//        n[1] = -(endPoint.getCoordinates()[0] - beginPoint.getCoordinates()[0]);

        n[0] = endPoint.y - beginPoint.y;
        n[1] = -(endPoint.x - beginPoint.x);


        // normalize normal vector
        double n2 = Math.sqrt(n[0] * n[0] + n[1] * n[1]);

        n[0] /= n2;
        n[1] /= n2;

        return n;
    }


    public static double calcCS(double mu, double rho) {
        return sqrt(mu / rho);
    }

    public static double calcCP(double lambda, double mu, double rho) {
        return sqrt((lambda + 2 * mu) / rho);
    }


    private static double calcJacobian(Triangle t) {
        Point[] v = t.getPoints();

//        return ((v[1].getCoordinates()[0] - v[0].getCoordinates()[0]) * (v[2].getCoordinates()[1] - v[0].getCoordinates()[1])) -
//                ((v[2].getCoordinates()[0] - v[0].getCoordinates()[0]) * (v[1].getCoordinates()[1] - v[0].getCoordinates()[1]));

        return ((v[1].x - v[0].x) * (v[2].y - v[0].y)) -
                ((v[2].x - v[0].x) * (v[1].y - v[0].y));
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
