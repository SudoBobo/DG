//import com.github.sudobobo.Configuration;
//import com.github.sudobobo.basis.Basis;
//import com.github.sudobobo.basis.Linear2DBasis;
//import com.github.sudobobo.calculations.Value;
//import com.github.sudobobo.geometry.Mesh;
//import com.github.sudobobo.geometry.Triangle;
//import com.github.sudobobo.meshconstruction.SalomeMeshConstructor;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import static com.github.sudobobo.General.getConfigFromYML;
//
//public class ValuesTest {
//
//
//    static Configuration config;
//    static Mesh mesh;
//    static Basis basis;
//
//
//    @BeforeClass
//    public static void setUpClass() {
//
//
//        Path configFile = Paths.get("/home/bobo/IdeaProjects/galerkin_1/test_config.yml");
//
//        config = getConfigFromYML(configFile);
//        System.out.println(config.toString());
//
//        double lambda = 2.0;
//        double mu = 1.0;
//        double rho = 1.0;
//
//        double realFullTime = 1;
//
//        double cP = Math.sqrt((lambda + 2.0 * mu) / rho);
//        double cS = Math.sqrt(mu / rho);
//
//        Path meshFile = Paths.get(config.getPathToMeshFile());
//        mesh = SalomeMeshConstructor.constructHomoMesh(meshFile, lambda, mu, rho);
//
//        double spatialStepForNumericalIntegration = 0.01;
//        basis = new Linear2DBasis(spatialStepForNumericalIntegration);
//    }
//
//
//    @Test
//    public void checkValues() {
//
//        Value[] values = Value.makeValuesArray(mesh, config.getInitialCondition(), basis);
//        Triangle tInTheMiddle = mesh.getTriangles()[51];
//        Value valueOfTriangleInTheMiddle = tInTheMiddle.getValue();
//
//
//
////        System.out.println(tInTheMiddle.getCenter().x());
////        System.out.println( tInTheMiddle.getCenter().y());
//        System.out.println("stop point for manual testing");
//    }
//
//
//    @AfterClass
//    public static void tearDown() {
//    }
//}
