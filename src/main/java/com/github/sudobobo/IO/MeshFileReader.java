package com.github.sudobobo.IO;

import com.github.sudobobo.geometry.Point;
import com.github.sudobobo.geometry.Triangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

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

    public static Triangle[] readTriangles(Path meshFile, Point[] points) {
        // return 'triangles' with vertexes (left-clock) and domains
        // neighbors are null yet

        Triangle[] triangles = null;

        try (BufferedReader br = new BufferedReader(new FileReader(meshFile.toFile()))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("Triangles\n")) {
                    break;
                }
            }

            int numberOfTriangles = Integer.parseInt(br.readLine());
            triangles = new Triangle[numberOfTriangles];

            String triangleLine[];
            for (int triangleNumber = 0; triangleNumber < numberOfTriangles; triangleNumber++) {

                // expect '936 344 1090 1' as 'v1 v2 v3 domain'

                triangleLine = br.readLine().split(" ");

                assert (triangleLine.length == 4) : "triangle line has size != 4. Check input .mesh file";

                int[] pointsNumber = new int[]{Integer.parseInt(triangleLine[0]), Integer.parseInt(triangleLine[1]),
                        Integer.parseInt(triangleLine[2])};

                int domain = Integer.parseInt(triangleLine[3]);

                Point[] trianglePoints = new Point[3];
                trianglePoints[0] = points[pointsNumber[0] - 1];
                trianglePoints[1] = points[pointsNumber[1] - 1];
                trianglePoints[2] = points[pointsNumber[2] - 1];

                triangles[triangleNumber] = Triangle.builder().domain(domain).points(trianglePoints).
                        build();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return triangles;
    }

}

