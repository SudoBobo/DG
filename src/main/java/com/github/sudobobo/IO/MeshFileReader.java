package com.github.sudobobo.IO;

import com.github.sudobobo.geometry.Domain;
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

// really nice comments I wrote

public class MeshFileReader {
    public static Point[] readPoints(Path meshFile) {
        Point[] points = null;

        try (BufferedReader br = new BufferedReader(new FileReader(meshFile.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("Vertices")) {
                    break;
                }
            }

            int numberOfPoints = Integer.parseInt(br.readLine());
            points = new Point[numberOfPoints];

            String [] pointLine;
            for (int pointNumber = 0; pointNumber < numberOfPoints; pointNumber++) {

                pointLine = br.readLine().split(" ");
                int pointId = pointNumber + 1;
                points[pointNumber] = new Point(pointId, new double[]{
                        Double.parseDouble(pointLine[0]),
                        Double.parseDouble(pointLine[1])});

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return points;
    }

    // expect certain constraint point[idx].Id == idx + 1
    public static Triangle[] readTriangles(Path meshFile, Point[] points, Domain[] domains) {
        // return 'triangles' with vertexes (left-clock) and domains
        // neighbors are null yet

        // check constraint point[idx].Id == idx + 1

        for (int pointIdx = 0; pointIdx < points.length; pointIdx++){
            assert (points[pointIdx].getId() == (pointIdx + 1)) : "constraint violated : point[idx].Id == idx + 1";
        }
        Triangle[] triangles = null;
        int [] triangleDomains = null;

        try (BufferedReader br = new BufferedReader(new FileReader(meshFile.toFile()))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("Triangles")) {
                    break;
                }
            }

            int numberOfTriangles = Integer.parseInt(br.readLine());
            triangles = new Triangle[numberOfTriangles];
            triangleDomains = new int[numberOfTriangles];

            String triangleLine[];
            for (int triangleNumber = 0; triangleNumber < numberOfTriangles; triangleNumber++) {

                // expect '936 344 1090 1' as 'v1 v2 v3 domain'

                triangleLine = br.readLine().split(" ");

                assert (triangleLine.length == 4) : String.format("triangle line has size != 4. Check input %s file", meshFile.toString());

                int[] pointsNumbers = new int[]{Integer.parseInt(triangleLine[0]), Integer.parseInt(triangleLine[1]),
                        Integer.parseInt(triangleLine[2])};

                triangleDomains[triangleNumber] = Integer.parseInt(triangleLine[3]);

                Point[] trianglePoints = new Point[3];

                trianglePoints[0] = points[pointsNumbers[0] - 1];
                trianglePoints[1] = points[pointsNumbers[1] - 1];
                trianglePoints[2] = points[pointsNumbers[2] - 1];

                triangles[triangleNumber] = Triangle.builder().points(trianglePoints).
                        build();

                triangles[triangleNumber].setTranslationCoefs();
                triangles[triangleNumber].setCenter();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        reduceDomains(triangleDomains);
        setDomains(triangles, triangleDomains, domains);

        return triangles;
    }

    private static void setDomains(Triangle[] triangles, int[] triangleDomains, Domain[] domains) {

        assert (triangles.length == triangleDomains.length);

        // проверка, совпадает ли число доменов в .mesh файле и в конфиге
        Set<Integer> uniqueDomains = new HashSet<>();
        Integer[] allDomains = Arrays.stream( triangleDomains ).boxed().toArray( Integer[]::new );
//        Integer [] allDomains = null;
        Collections.addAll(uniqueDomains, allDomains);

        assert (uniqueDomains.size() == domains.length);



        for (int i = 0; i < triangles.length; i++){
            Domain domain = domains[triangleDomains[i]];
            triangles[i].setDomain(domain);
        }
    }

    // given following triangles:
    // 1 2 3 1
    // 4 5 6 33
    // 7 8 9 21
    //
    // they may be translated to:
    // 1 2 3 0
    // 4 5 6 1
    // 7 8 9 2
    //
    // as well to:
    // 1 2 3 2
    // 4 5 6 0
    // 7 8 9 1
    //
    // It is deterministic, but not predictable
    //
    // So the only way to properly connect Salome domains and DG domains is
    // manual way, looking at result via Paraview
    private static void reduceDomains(int[] triangleDomains) {

        Set<Integer> uniqueOldDomains = new HashSet<>();
        for (int d : triangleDomains) {
            uniqueOldDomains.add(d);
        }

        Map<Integer, Integer> oldDomainToNewDomain = new HashMap<>();

        int newDomain = 0;

        for (int oldDomain : uniqueOldDomains) {
            if (!oldDomainToNewDomain.containsKey(oldDomain)) {
                oldDomainToNewDomain.put(oldDomain, newDomain);
                newDomain++;
            }
        }

        for (int i = 0; i < triangleDomains.length; i ++) {
            triangleDomains[i] = oldDomainToNewDomain.get(triangleDomains[i]);
        }
    }
}
