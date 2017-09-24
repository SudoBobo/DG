package com.github.sudobobo;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class General {
    public static void main(String[] args) {

        Path configFile = Paths.get(args[0]);
        Configuration config = getConfigFromYML(configFile);
        System.out.println(config.toString());

        double lambda = 2.0;
        double mu = 1.0;
        double rho = 1.0;

        double size = 100;

        double xMin = 0;
        double yMin = 0;

        double yMax = size;
        double xMax = size;

        double realFullTime = 100;

        double cP = Math.sqrt((lambda + 2.0 * mu) / rho);
        double cS = Math.sqrt(mu / rho);

        double spatialStep = 1;
        // durability - desiriable value of relation
        double durability = 0.5;
        double courantTimeStep = calcCourantTimeStep(cP, cS, spatialStep, durability);
        double timeStep = courantTimeStep;

        double spatialStepForNumericalIntegration = 0.00000001;
        Basis basis = new Basis();

        System.out.println("dx = " + spatialStep);
        System.out.println("dt = " + timeStep);

        int timeSteps = (int) (realFullTime / timeStep);

        Mesh initialCondition = MeshConstructor.constructHomoMesh(lambda, mu, rho, xMin, xMax,
                yMin, yMax, spatialStep, spatialStepForNumericalIntegration, basis);

        Path outputDir = getOutputPath(Paths.get("/home/bobo/AData/"), size, spatialStep, timeStep);

        MeshWriter meshWriter = new MeshWriter(outputDir, Paths.get("PvtrTemplate"), Paths.get("VtrApperTemplate"),
                Paths.get("VtrLowerTemplate"));

        Long[] extent = initialCondition.getRawExtent(xMin, xMax, yMin, yMax, spatialStep);

        SystemSolver eulerSolver = new EulerSystemSolver();
        Solver solver = new Solver(eulerSolver);

        meshWriter.writeAllPVTR(extent, timeSteps - 1);

        Mesh orig = initialCondition;
        Mesh next = MeshConstructor.constructHomoMesh(lambda, mu, rho, xMin, xMax,
                yMin, yMax, spatialStep, spatialStepForNumericalIntegration, basis);

        for (int t = 0; t < timeSteps; t++) {

            try {
                meshWriter.writeMeshVTR(orig, extent, t);
            } catch (IOException e) {
                e.printStackTrace();
            }

            solver.solveOneStep(orig, next, timeStep);
            orig = next;
        }

    }

    private static Configuration getConfigFromYML(Path configFile) {

        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(configFile)) {
            Configuration config = yaml.loadAs(in, Configuration.class);
            return config;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static double calcCourantTimeStep(double cP, double cS, double spatialStep, double durability) {
        return durability * spatialStep / Math.max(cP, cS);
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