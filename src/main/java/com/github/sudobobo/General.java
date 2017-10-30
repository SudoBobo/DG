package com.github.sudobobo;

import com.github.sudobobo.IO.MeshWriter;
import com.github.sudobobo.IO.ValuesToWrite;
import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.basis.Linear2DBasis;
import com.github.sudobobo.calculations.Value;
import com.github.sudobobo.geometry.Mesh;
import com.github.sudobobo.meshconstruction.SalomeMeshConstructor;
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

        double realFullTime = 1;

        double cP = Math.sqrt((lambda + 2.0 * mu) / rho);
        double cS = Math.sqrt(mu / rho);

        Path meshFile = Paths.get(config.getPathToMeshFile());
        Mesh mesh = SalomeMeshConstructor.constructHomoMesh(meshFile, lambda, mu, rho);

        double minSideLength = mesh.getMinSideLength();
        // durability - desiriable value of relation
        double durability = 0.5;
//        double timeStep = calcCourantTimeStep(cP, cS,  minSideLength, durability);
        double timeStep = realFullTime;

        double spatialStepForNumericalIntegration = 0.001;
        Basis basis = new Linear2DBasis(spatialStepForNumericalIntegration);

        System.out.println("min dx = " + minSideLength);
        System.out.println("dt = " + timeStep);

        int timeSteps = (int) (realFullTime / timeStep);


        Path outputDir = getOutputPath(Paths.get("/home/bobo/IdeaProjects/galerkin_1/results"), mesh.getTriangles().length, minSideLength, timeStep);

        MeshWriter meshWriter = new MeshWriter(outputDir, Paths.get("PvtrTemplate"), Paths.get("VtrApperTemplate"),
                Paths.get("VtrLowerTemplate"));


        // todo remove hardcode inside makeValuesArray (pass initialCondition function as an argument)


        double rectangleSideLength = minSideLength / 10;

        // associate values with mesh triangles and triangles with values
        // change appropriate fields
        Value[] values = Value.makeValuesArray(mesh, config.getInitialCondition(), basis);
        Value [] bufferValues = Value.makeBufferValuesArray(mesh, basis);

        ValuesToWrite valuesToWrite = new ValuesToWrite(values, rectangleSideLength, minSideLength, mesh.getLTPoint(),
                mesh.getRBPoint(), basis);

        Long [] extent = valuesToWrite.getExtent(rectangleSideLength, mesh.getLTPoint(), mesh.getRBPoint());

        dUmethod dU_method = new dUmethod();
        Solver RK_Solver = new RKSolver(dU_method, mesh.getTriangles().length);

        meshWriter.writeAllPVTR(extent, timeSteps - 1);

        // We have:
        // 1 triangle array
        // 1 valueToWrite array, associated with 'values' value vector
        // 2 value vectors, one 'values' is associated with valueToWrite, another 'bufferValues is not associated with
        // any valueToWriteValue
        // and USED ONLY FOR CALCULATIONS

//
//        for (Value v : values){
//            String s = v.getU().toString();
//            System.out.println(s);
//            System.out.println("\n");
//        }

        try {
            meshWriter.writeMeshVTR(valuesToWrite, extent, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        for (int t = 0; t < timeSteps; t++) {
//
//            try {
//                meshWriter.writeMeshVTR(valuesToWrite, extent, t);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            RK_Solver.solveOneStep(values, bufferValues, timeStep, basis);
//        }

    }



    public static Configuration getConfigFromYML(Path configFile) {

        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(configFile)) {
            Configuration config = yaml.loadAs(in, Configuration.class);
            return config;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static double calcCourantTimeStep(double cP, double cS, double spatialStep, double durability) {
        return durability * spatialStep / Math.max(cP, cS);
    }

    public static Path getOutputPath(Path generalOutputPath, double size, double fine, double timeStep) {
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