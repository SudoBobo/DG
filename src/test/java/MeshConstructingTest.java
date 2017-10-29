import com.github.sudobobo.Configuration;
import com.github.sudobobo.IO.MeshFileReader;
import com.github.sudobobo.geometry.Point;
import com.github.sudobobo.geometry.Triangle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.github.sudobobo.General.getConfigFromYML;
import static com.github.sudobobo.meshconstruction.SalomeMeshConstructor.*;

public class MeshConstructingTest {

    static Configuration config;


    @BeforeClass
    public static void setUpClass() {

        Path configFile = Paths.get("/home/bobo/IdeaProjects/galerkin_1/test_config.yml");
        config = getConfigFromYML(configFile);
        System.out.println(config.toString());

        double lambda = 2.0;
        double mu = 1.0;
        double rho = 1.0;


    }

    @Test
    public void checkPointToReplacementPoint() {

        Path meshFile = Paths.get(config.getPathToMeshFile());

        Point[] points = MeshFileReader.readPoints(meshFile);
        Triangle[] triangles = MeshFileReader.readTriangles(meshFile, points);

        double minDistance = 0.00001;
        Map<Point, Point> pointToReplacementPoint = getPointToReplacementPoint(points, minDistance);

    }

    @Test
    public void checkGetPointsWithNoDuplicates() {

        Path meshFile = Paths.get(config.getPathToMeshFile());

        Point[] points = MeshFileReader.readPoints(meshFile);
        Triangle[] triangles = MeshFileReader.readTriangles(meshFile, points);

        double minDistance = 0.00001;
        Map<Point, Point> pointToReplacementPoint = getPointToReplacementPoint(points, minDistance);

        Point [] pointsWithNoDuplicates = getPointsWithNoDuplicates(points, pointToReplacementPoint);

        for (int pointIdx = 0; pointIdx < pointsWithNoDuplicates.length; pointIdx++){
            assert (points[pointIdx].equals(pointsWithNoDuplicates[pointIdx]));
        }

    }

    @Test
    public void checkChangeDuplicateVertexes(){

        Path meshFile = Paths.get(config.getPathToMeshFile());

        Point[] points = MeshFileReader.readPoints(meshFile);
        Triangle[] triangles = MeshFileReader.readTriangles(meshFile, points);

        double minDistance = 0.00001;
        Map<Point, Point> pointToReplacementPoint = getPointToReplacementPoint(points, minDistance);

        points = getPointsWithNoDuplicates(points, pointToReplacementPoint);
        changeDuplicateVertexes(triangles, pointToReplacementPoint);
    }


    @Test
    public void checkSetNeighborsAndBounds() {

        Path meshFile = Paths.get(config.getPathToMeshFile());

        Point[] points = MeshFileReader.readPoints(meshFile);
        Triangle[] triangles = MeshFileReader.readTriangles(meshFile, points);

        double minDistance = 0.00001;
        Map<Point, Point> pointToReplacementPoint = getPointToReplacementPoint(points, minDistance);

        points = getPointsWithNoDuplicates(points, pointToReplacementPoint);
        changeDuplicateVertexes(triangles, pointToReplacementPoint);

        changePointsOrderToReverseClock(triangles);
        reduceDomains(triangles);

        setNeighborsAndBounds(triangles);

        // take some random triangles (both in the center of the mesh and on the borders) and check them

    }

    @AfterClass
    public static void tearDown() {
        // освобождение ресурсов
    }

}
