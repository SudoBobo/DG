package main.java;

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
        Path dir = Paths.get("/home/bobo/AData/");
        MeshWriter meshWriter = new MeshWriter(dir, Paths.get("PvtrTemplate"), Paths.get("PvtrTemplate"));
        Mesh[] meshes = new Mesh[1];
        meshes[0] = mesh;

        Long [] extent = {120L,130L,140L};

        meshWriter.writeMeshes(meshes, extent);
        System.out.println("fin");


    }
}