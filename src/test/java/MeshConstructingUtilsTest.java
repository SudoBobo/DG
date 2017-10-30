import com.github.sudobobo.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.sudobobo.General.getConfigFromYML;

public class MeshConstructingUtilsTest {

    static Configuration config;

    @BeforeClass
    public static void setUpClass() {
        Path configFile = Paths.get("/home/bobo/IdeaProjects/galerkin_1/test_config.yml");
        config = getConfigFromYML(configFile);
        System.out.println(config.toString());


    }


    @Test
    public void checkNormals() {
    }

    @Test
    public void checkIJseting(){


        // manual test
    }


    @AfterClass
    public static void tearDown() {
    }
}
