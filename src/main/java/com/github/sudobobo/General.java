package com.github.sudobobo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class General {
    public static void main(String[] args) {

        double lambda = 2.0;
        double mu = 1.0;
        double rho = 1.0;

        double size = 100;

        double xMin = 0;
        double yMin = 0;

        double yMax = size;
        double xMax = size;

        double realFullTime = 100;

        double timeStep = 0.1;
        double spatialStep = 1;

        int timeSteps = (int) (realFullTime / timeStep);

        Mesh initialCondition = NewMeshConstructor.constructHomoMesh(lambda, mu, rho, xMin, xMax,
                yMin, yMax, spatialStep);

        Path outputDir = getOutputPath(Paths.get("/home/bobo/AData/"), size, spatialStep, timeStep);

        MeshWriter meshWriter = new MeshWriter(outputDir, Paths.get("PvtrTemplate"), Paths.get("VtrApperTemplate"),
                Paths.get("VtrLowerTemplate"));

        Long[] extent = initialCondition.getRawExtent(xMin, xMax, yMin, yMax, spatialStep);

        SystemSolver eulerSolver = new EulerSystemSolver();
        Solver solver = new Solver(eulerSolver);

        meshWriter.writeAllPVTR(extent, timeSteps - 1);

        Mesh orig = initialCondition;
        Mesh next = initialCondition.getCopy();

        for (int t = 0; t < timeSteps; t++){

            try {
                meshWriter.writeMeshVTR(orig, extent, t);
            } catch (IOException e) {
                e.printStackTrace();
            }

            solver.solveOneStep(orig, next, timeStep);
            orig = next;
        }

    }

    private static Path getOutputPath(Path generalOutputPath, double size, double fine, double timeStep) {
        Path outputDir = Paths.get(String.format(generalOutputPath.toString() + "%.3f_%.3f_with_fine_%.3f_and_time_step_%.3f", size, size, fine, timeStep));

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