package com.github.sudobobo;

import com.github.sudobobo.IO.MeshWriter;
import com.github.sudobobo.IO.ValuesToWrite;
import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.basis.PreLinear2DBasis;
import com.github.sudobobo.calculations.Value;
import com.github.sudobobo.geometry.Domain;
import com.github.sudobobo.geometry.Mesh;
import com.github.sudobobo.geometry.Triangle;
import com.github.sudobobo.meshconstruction.MeshBorder;
import com.github.sudobobo.meshconstruction.SalomeMeshConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;

import static com.github.sudobobo.meshconstruction.SalomeMeshConstructor.calcCP;
import static com.github.sudobobo.meshconstruction.SalomeMeshConstructor.calcCS;

public class General {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        System.out.println("lambda, mu and rho are hardcoded in triangle.calcRpqnJ");
        Path configFile = Paths.get(args[0]);
        Configuration config = getConfigFromYML(configFile);
        System.out.println(config.toString());

        Path meshFile = Paths.get(config.getPathToMeshFile());
        Domain[] domains = Domain.createDomains(config.getDomains());
        MeshBorder[] borders = MeshBorder.createMeshBorders(config.getBorders(), config.getDefaultBorderType());

        Mesh mesh = SalomeMeshConstructor.constructHomoMesh(meshFile, domains, borders);
        System.out.println("Mesh is built");

        int k = 0;
        for (Triangle t: mesh.getTriangles()) {
            if (Math.abs(t.getCenter().x) < 5 && Math.abs(t.getCenter().y) < 5){
                System.out.println(k);
            }
            k++;
        }

        double maxSideLength = mesh.getMaxSideLength();
        double avgSideLength = mesh.getAVGSideLength();
        double minSideLength = mesh.getMinSideLength();
        // durability - desiriable value of relation
        double durability = 0.5;
        double maxCP = calcMaxCP(domains);
        double maxCS = calcMaxCS(domains);
        double timeStep = calcCourantTimeStep(maxCP, maxCS,  minSideLength, durability);
        timeStep = timeStep / 5.0;

        double spatialStepForNumericalIntegration = 0.001;
//        Basis basis = new Linear2DBasis(spatialStepForNumericalIntegration);
        Basis basis = new PreLinear2DBasis(spatialStepForNumericalIntegration);
//        Basis basis = new SimpleBasis(spatialStepForNumericalIntegration);
//        Basis basis = new ArticleBasis(spatialStepForNumericalIntegration);


        System.out.println("Basis functions are calculated");

        System.out.println("min dx = " + minSideLength);
        System.out.println("dt = " + timeStep);

//        int timeSteps = (int) (config.getRealFullTime() / timeStep);
        int timeSteps = (int) (timeStep * 10);
        if (timeSteps < 5){
            timeSteps = 30;
        }

        timeSteps = 100;
        String meshName = config.getPathToMeshFile().substring(config.getPathToMeshFile().lastIndexOf("/") + 1);
        meshName = meshName.substring(0, meshName.indexOf("."));

        Path outputDir = getOutputPath("/home/bobo/IdeaProjects/DG/results/", meshName, config.getRealFullTime(), timeStep, config.getResName());
        System.out.println("Results are at " + outputDir.toString());
        MeshWriter meshWriter = new MeshWriter(outputDir, Paths.get("PvtrTemplate"));
        
        // todo remove hardcode inside makeValuesArray (pass initialCondition function as an argument)

        // side of one to-write rectangle
        double rectangleSideLength = 0;
        if (avgSideLength > 5) {
            rectangleSideLength = minSideLength / 2.0;
            rectangleSideLength = Math.floor(rectangleSideLength);
            rectangleSideLength = (rectangleSideLength > 1) ? rectangleSideLength : 1;
        }

        if (avgSideLength < 5) {
            rectangleSideLength = avgSideLength / 20.0;
        }

        assert(rectangleSideLength > 0);

        if (avgSideLength > 1) {
            rectangleSideLength = avgSideLength / 20.0;
        }

        System.out.println("rectangle side length is " + rectangleSideLength);



        // associate values with mesh triangles and triangles with values
        // change appropriate fields
        System.out.println("Start to calculate values");
        Value[] values = Value.makeValuesArray(mesh, config.getInitialCondition(), basis);
        Value[] bufferValues = Value.makeBufferValuesArray(mesh, basis);

        System.out.println("Values are calculated");

        ValuesToWrite valuesToWrite = new ValuesToWrite(values, rectangleSideLength, mesh.getLTPoint(),
                mesh.getRBPoint(), basis);

        System.out.println("ValuesToWrite are ready");

        Long[] extent = valuesToWrite.getExtent(rectangleSideLength, mesh.getLTPoint(), mesh.getRBPoint());

//        dUmethod dU_method = new dUmethodReal();
        dUmethod dU_method = new DendUmethodWithNoDebug();
//        Solver RK_Solver = new RKSolver(dU_method, mesh.getTriangles().length);
        Solver RK_Solver = new RKSolverBack(dU_method, mesh.getTriangles().length);

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

//        try {
//            meshWriter.writeMeshVTR(valuesToWrite, extent, 0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        System.out.println("Values to write print");
//        writeValuesToWriteSorted(valuesToWrite, basis);

//        System.out.println(
//            String.format("Triangle-to-domain info is written to %s"),
//            domainFile.toString());

        try {
            meshWriter.writeMeshVTR(valuesToWrite.getRawTrianglesToDomains(),
                extent, 0, "/Domains_%d.vtr",  Paths.get("DomainsApperTemplate"),
                Paths.get("DomainsLowerTemplate"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileNameTemplate = "/part0_%d.vtr";

        for (int t = 0; t < timeSteps; t++) {
            try {
                meshWriter.writeMeshVTR(valuesToWrite.getRawValuesToWrite(),
                    extent, t, fileNameTemplate,  Paths.get("VtrApperTemplate"),
                    Paths.get("VtrLowerTemplate"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            RK_Solver.solveOneStep(values, bufferValues, timeStep, basis);
        }

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(String.format("Total time %d", totalTime / 1000));
    }

    private static double calcMaxCP(Domain[] domains) {
        double maxCP = 0;
        double CP;

        for (Domain domain : domains){
            CP = calcCS(domain.getMu(), domain.getRho());
            maxCP = (CP > maxCP) ? CP : maxCP;
        }

        return maxCP;
    }

    private static double calcMaxCS(Domain[] domains) {
        double maxCS = 0;
        double CS;

        for (Domain domain : domains){
            CS = calcCP(domain.getLambda(), domain.getMu(), domain.getRho());
            maxCS = (CS > maxCS) ? CS : maxCS;
        }

        return maxCS;
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

    public static Path getOutputPath(String generalOutputPath, String meshName, double realTime, double timeStep, String resName) {

        String dirName = "";
        if (resName != null) {
            dirName = generalOutputPath + resName;
        }

        if (resName == null) {
            dirName = String.format(generalOutputPath + "mesh_%s_real_time_%.3f_time_step_%.3f", meshName, realTime, timeStep);
        }
        Path outputDir = Paths.get(dirName);

        if (Files.exists(outputDir)) {
            deleteDirectory(outputDir.toFile());
        }

        if (!outputDir.toFile().mkdir()) {
            System.out.println(String.format("Output directoru %s was not created", dirName));
        }
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

    private static void writeSimpleValues(Value[] values, Basis basis) {

        class valuesComp implements Comparator<Value> {
            // Used for sorting in ascending order of
            // roll number
            public int compare(Value a, Value b) {
                return (int) (a.getAssociatedTriangle().getCenter().x - b.getAssociatedTriangle().getCenter().x);
            }
        }

        Arrays.sort(values, new valuesComp());

//        for (Value v : values) {
//            String r = Arrays.toString(v.getAssociatedTriangle().getCenter().coordinates());
////            System.out.println(r);
//            System.out.println(Arrays.toString(basis.calcUNumerical(v.getU(), v.getAssociatedTriangle())));
//        }
    }

//    private static void writeValuesToWriteSorted(ValuesToWrite valuesToWrite, Basis basis) {
//
//
//        class vComp implements Comparator<ValueToWrite> {
//            // Used for sorting in ascending order of
//            // roll number
//            public int compare(ValueToWrite a, ValueToWrite b) {
//                return (int) (a.getAssociatedValue().getAssociatedTriangle().getCenter().x
//                        - b.getAssociatedValue().getAssociatedTriangle().getCenter().x);
//            }
//        }
//
//        Arrays.sort(valuesToWrite.getValuesToWrite(), new vComp());
//        for (ValueToWrite v : valuesToWrite.getValuesToWrite()) {
//            String r = Arrays.toString(v.getAssociatedValue().getAssociatedTriangle().getCenter().coordinates());
//            System.out.println(r);
//
//            System.out.println(Arrays.toString(basis.calcUNumerical(v.getAssociatedValue().getU(), v.getAssociatedValue().getAssociatedTriangle())));
//
//
//        }
//    }
}