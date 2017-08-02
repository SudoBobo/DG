package main.java;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Hello {
    public static void main(String[] args) {
        double lambda = 2.0;
        double mu = 1.0;
        double rho = 1.0;

        int xMin = -50;
        int xMax = 50;
        int yMin = -50;
        int yMax = 50;

        int fine = 50;

        Mesh mesh = MeshConstructor.constructHomoMesh(lambda, mu, rho, xMin, xMax,
                yMin, yMax, fine);
        Path outPutDir = Paths.get("/home/bobo/AData/");
        MeshWriter meshWriter = new MeshWriter(outPutDir, Paths.get("PvtrTemplate"), Paths.get("VtrApperTemplate"),
                Paths.get("VtrLowerTemplate"));
        Mesh[] meshes = new Mesh[2];
        meshes[0] = mesh;
        meshes[1] = mesh;

        Long [] extent = mesh.getRawExtent(xMin, xMax, yMin, yMax, fine);
        try {
            meshWriter.writeMeshes(meshes, extent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("fin");


    }
}