package com.github.sudobobo.IO;

import com.github.sudobobo.geometry.Point;
import com.github.sudobobo.geometry.Triangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

// TODO rewrite with streams

// triangles can have common borders only if they have common vertexes
// one can store in Point all triangles that have this Point as vertexes
// using this one can obtain O(n) complexity of Neighbor-Find instead of current O(n^2)
// one should use separate class PointWithTriangles

public class MeshFileReader {
    public static Point[] readPoints(Path meshFile) {

        Point[] points = null;

        try (BufferedReader br = new BufferedReader(new FileReader(meshFile.toFile()))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("Vertices\n")) {
                    break;
                }
            }


            int numberOfPoints = Integer.parseInt(br.readLine());
            points = new Point[numberOfPoints];

            String pointLine[];
            for (int pointNumber = 0; pointNumber < numberOfPoints; pointNumber++) {

                pointLine = br.readLine().split(" ");
                points[pointNumber] = new Point(pointNumber, new double[]{
                        Double.parseDouble(pointLine[0]),
                        Double.parseDouble(pointLine[1])});

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return points;

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

    public static Point[] getPointsWithNoDuplicates(Point[] points, Map<Integer, Integer> pointToReplacementPoint) {

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

}
