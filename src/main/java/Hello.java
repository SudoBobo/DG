import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Hello {
    public static void main(String[] args) {

        double lambda = 2.0;
        double mu = 1.0;
        double rho = 1.0;

        double size = 100;
        double xMin = -(size/2);
        double yMin = -(size/2);
        double yMax = size/2;
        double xMax = size/2;

        double spatialStep = 1;

        Mesh initialCondition = MeshConstructor.constructHomoMesh(lambda, mu, rho, xMin, xMax,
                yMin, yMax, spatialStep);


        Path outputDir = getOutputPath(Paths.get("/home/bobo/AData/"), size, spatialStep);



        MeshWriter meshWriter = new MeshWriter(outputDir, Paths.get("PvtrTemplate"), Paths.get("VtrApperTemplate"),
                Paths.get("VtrLowerTemplate"));

        Mesh[] meshes = new Mesh[2];
        meshes[0] = initialCondition;
        meshes[1] = initialCondition;

        SystemSolver eulerSolver = new EulerSystemSolver();
        Solver solver = new Solver(eulerSolver);

//        Mesh[] meshes = solver.solve(initialCondition, realFullTime, timeStep);

        Long [] extent = initialCondition.getRawExtent(xMin, xMax, yMin, yMax, spatialStep);

        try {
            meshWriter.writeMeshes(meshes, extent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("fin");


    }

    private static Path getOutputPath(Path generalOutputPath, double size, double fine) {
        Path outputDir = Paths.get(String.format(generalOutputPath.toString() + "%.3f_%.3f_with_fine_%.3f", size, size, fine));

        if (Files.exists(outputDir)){
            try {
                FileUtils.deleteDirectory(outputDir.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        outputDir.toFile().mkdir();
        return outputDir;
    }
}