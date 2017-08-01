package main.java;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class MeshWriter {
    private Path directoryForOutPut;

    private Path pvtrTemplate;
    private Path vtrTemplate;

    public MeshWriter(Path dir, Path pvtrTemplate, Path vtrTemplate) {

        directoryForOutPut = dir;
        this.pvtrTemplate = pvtrTemplate;
        this.vtrTemplate = vtrTemplate;

    }

    public void writeMeshes(Mesh[] meshes, Long[] wholeExtent) {

        assert wholeExtent.length == 3;

        String vtrSourceNameTemplate = "part0_0.vtr";
        try {
            createPVTRs(wholeExtent, vtrSourceNameTemplate, meshes.length - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        for (int meshNum = 0; meshNum < meshes.length; meshNum++) {
//
//            Mesh mesh = meshes[meshNum];
//
//            Path VTRName = Paths.get(directoryForOutPut.toString() + String.format("/part0_%d.vtr", meshNum));
//
//            File PVTRFile = PVTRName.toFile();
//            File VTRFile = VTRName.toFile();
//
//            String vtrSource = String.format("/part0_%d.vtr", meshNum);
////            ArrayList<Double> dataArray = createDataArray(mesh);
//
//            try {
//                VTRFile.createNewFile();
//                PVTRFile.createNewFile();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


//            fillVTR(wholeExtent, dataArray);
//
////            try (BufferedWriter writer = Files.newBufferedWriter(PVTRName, StandardOpenOption.TRUNCATE_EXISTING)) {
////
////                writeUpperPartOfTemplate(writer);
//////                for (int tNum = 0; tNum < mesh.triangles.size(); tNum+=4){
//////                    String out = Double.toString(mesh.triangles.get(tNum).u.get(0));
//////                    writer.write(out);
//                }

    }


    private void createPVTRs(Long[] wholeExtent, String vtrSourceNameTemplate, int number) throws IOException {

        Path filledTemplate = Paths.get("template");

        boolean is_exist = Files.exists(filledTemplate);
        if (!is_exist) {
            System.out.println(filledTemplate.toFile().createNewFile());
        }

        Files.copy(pvtrTemplate, filledTemplate, REPLACE_EXISTING);

        oldLineToNewLine(filledTemplate, "<PRectilinearGrid WholeExtent=\"0 0 0 0 0 0\" GhostLevel=\"0\">",
                String.format("<PRectilinearGrid WholeExtent=\"0 %d 0 %d 0 %d\" GhostLevel=\"0\">",
                        wholeExtent[0], wholeExtent[1], wholeExtent[2]));


        Path PVTRName;
        for (int i = 0; i <= number; i++) {

            PVTRName = Paths.get(directoryForOutPut.toString() + String.format("/file%d.pvtr", i));
            if (!Files.exists(PVTRName)) {
                PVTRName.toFile().createNewFile();
            }

            Files.copy(filledTemplate, PVTRName, REPLACE_EXISTING);


            oldLineToNewLine(PVTRName, "<Piece Extent=\"0 0 0 0 0 0\" Source=\"part0_0.vtr\"/>",
                    String.format("<Piece Extent=\"0 %d 0 %d 0 %d\" Source=\"part0_%d.vtr\"/>",
                            wholeExtent[0], wholeExtent[1], wholeExtent[2], i)

            );


        }
    }


//    private void fillPVTR(File pvtrFile, Long[] wholeExtent, String vtrSource) {
//        try (Writer writer = Files.newBufferedWriter(pvtrFile.toPath(), StandardOpenOption.TRUNCATE_EXISTING)) {
//
//            for (String line = pvtrTemplate.readLine(); line != null; line = br.readLine()) {
//                System.out.println(line);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    private void oldLineToNewLine(Path FILE_PATH, String oldLine, String newLine) {

        List<String> fileContent = null;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8));
            for (int i = 0; i < fileContent.size(); i++) {

                if (fileContent.get(i).equals(oldLine)) {
                    fileContent.set(i, newLine);
                    break;
                }
            }

            Files.write(FILE_PATH, fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
