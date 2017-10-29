import com.github.sudobobo.Configuration;
import com.github.sudobobo.IO.MeshFileReader;
import com.github.sudobobo.geometry.Point;
import com.github.sudobobo.geometry.Triangle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.sudobobo.General.getConfigFromYML;

public class MeshFileReaderTest {

    static Configuration config;


    @BeforeClass
    public static void setUpClass() {

        Path configFile = Paths.get("/home/bobo/IdeaProjects/galerkin_1/test_config.yml");
        config = getConfigFromYML(configFile);
        System.out.println(config.toString());

        Path meshFile = Paths.get(config.getPathToMeshFile());
    }

    @Test
    public void checkReadPoints() {

        Point[] points = MeshFileReader.readPoints(Paths.get(config.getPathToMeshFile()));

        Point p1 = new Point(1, new double[]{-50, -50});
        Point p2 = new Point(2, new double[]{-50, 50});

        Point p27 = new Point(27, new double[]{13.3137468989871, -0.118369777771794 });
        Point lastPoint = new Point(28, new double[]{7.11951555199181, 13.0252276570685});

        assert (points[0].equals(p1));
        assert (points[1].equals(p2));

        assert (points[points.length - 2].equals(p27));
        assert (points[points.length - 1].equals(lastPoint));

    }

    @Test
    public void checkReadTriangles() {
        Path meshFile = Paths.get(config.getPathToMeshFile());

        Point[] points = MeshFileReader.readPoints(meshFile);
        Triangle[] triangles = MeshFileReader.readTriangles(meshFile, points);

        Triangle t1 = triangles[0];
        int [] pointsIds = new int[]{2, 5, 9};
        Point p1 = points[1];
        Point p2 = points[4];
        Point p3 = points[8];

        assert (t1.getPoints()[0].equals(p1));
        assert (t1.getPoints()[0].getId() == pointsIds[0]);

    }

    @AfterClass
    public static void tearDown() {
        // освобождение ресурсов
    }

}