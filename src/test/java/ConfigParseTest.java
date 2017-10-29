import com.github.sudobobo.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.sudobobo.General.getConfigFromYML;

public class ConfigParseTest {


    static Configuration config;

    @BeforeClass
    public static void setUpClass() {

        Path configFile = Paths.get("/home/bobo/IdeaProjects/galerkin_1/test_config.yml");
        config = getConfigFromYML(configFile);
        System.out.println(config.toString());
    }

    @Test
    public void checkCorrectnes() {

        double spatialStep = 1.0;
        double realFullTime = 100.0;
        String pathToMeshFile = "/home/bobo/IdeaProjects/galerkin_1/meshes/Test2D_2142.mesh";
        String initialCondition = "sin";
        double spatialStepForNumericalIntegration = 0.0001;

        assert (config.getSpatialStep() == spatialStep);
        assert (config.getRealFullTime() == realFullTime);
        assert (config.getPathToMeshFile().equals(pathToMeshFile));
        assert (config.getInitialCondition().equals(initialCondition));
        assert (config.getSpatialStepForNumericalIntegration() == spatialStepForNumericalIntegration);
    }

    @AfterClass
    public static void tearDown() {
        // освобождение ресурсов
    }

}
