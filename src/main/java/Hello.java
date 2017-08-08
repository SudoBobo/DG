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

        double fine = 0.1;

        Mesh mesh = MeshConstructor.constructHomoMesh(lambda, mu, rho, xMin, xMax,
                yMin, yMax, fine);



        Path outPutDir = Paths.get(String.format("/home/bobo/AData/%.3f_%.3f_with_fine_%.3f", size, size, fine));

        if (Files.exists(outPutDir)){
            try {
                FileUtils.deleteDirectory(outPutDir.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        outPutDir.toFile().mkdir();

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