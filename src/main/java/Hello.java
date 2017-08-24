import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class Hello {
    public static void main(String[] args) {

        double lambda = 2.0;
        double mu = 1.0;
        double rho = 1.0;

        double size = 100;
        double xMin = -(size / 2);
        double yMin = -(size / 2);
        double yMax = size / 2;
        double xMax = size / 2;

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

        Long[] extent = initialCondition.getRawExtent(xMin, xMax, yMin, yMax, spatialStep);

        try {
            meshWriter.writeMeshes(meshes, extent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("fin");


    }

    private static Path getOutputPath(Path generalOutputPath, double size, double fine) {
        Path outputDir = Paths.get(String.format(generalOutputPath.toString() + "%.3f_%.3f_with_fine_%.3f", size, size, fine));

        if (Files.exists(outputDir)) {
            deleteDirectory(outputDir.toFile());
        }

        outputDir.toFile().mkdir();
        return outputDir;
    }

    private static void deleteDirectory(File file) {

        Path directory = Paths.get(file.getPath());

        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}