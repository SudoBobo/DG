import java.io.*;
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
    private Path vtrApperTemplate;
    private Path vtrLowerTemplate;


    public MeshWriter(Path outputDir, Path pvtrTemplate, Path vtrApperTemplate, Path vtrLowerTemplate) {

        directoryForOutPut = outputDir;
        this.pvtrTemplate = pvtrTemplate;
        this.vtrApperTemplate = vtrApperTemplate;
        this.vtrLowerTemplate = vtrLowerTemplate;

    }

    public void writeMeshVTR(Mesh mesh, Long[] wholeExtent, int meshNum) throws IOException {

        assert wholeExtent.length == 3;

        Path apperTemplate = null;
        Path lowerTemplate = null;

        try {
            apperTemplate = createVtrApperTemplate(wholeExtent, vtrApperTemplate);
            lowerTemplate = createVtrLowerTemplate(wholeExtent, vtrLowerTemplate);
        } catch (IOException e) {
            e.printStackTrace();
        }

        createVTR(wholeExtent, mesh.getDataArray(), meshNum, apperTemplate, lowerTemplate);


    }

    public void writeAllPVTR(Long[] wholeExtent, int numberOfMeshes) {
        String vtrSourceNameTemplate = "part0_0.vtr";
        try {
            createPVTRs(wholeExtent, vtrSourceNameTemplate, numberOfMeshes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path createVtrLowerTemplate(Long[] wholeExtent, Path vtrLowerTemplate) throws IOException {
        Path result = Paths.get("VtrLowerTemplatePrepared");
        if (!Files.exists(result)) {
            result.toFile().createNewFile();
        }

        String xString = produceSizeString(wholeExtent[0]);
        String yString = produceSizeString(wholeExtent[1]);
        String zString = produceSizeString(wholeExtent[2]);


        Files.copy(vtrLowerTemplate, result, REPLACE_EXISTING);
        oldLineToNewLine(result, "x", xString
        );
        oldLineToNewLine(result, "y", yString
        );

        oldLineToNewLine(result, "z", zString
        );

        return result;
    }

    private String produceSizeString(long size) {
        StringBuilder sb = new StringBuilder();
        for (long i = 0; i <= size; i++) {
            sb.append(Long.toString(i) + ' ');
        }
        return sb.toString();
    }

    private Path createVtrApperTemplate(Long[] wholeExtent, Path vtrApperTemplate) throws IOException {
        Path result = Paths.get("VtrApperTemplatePrepared");
        if (!Files.exists(result)) {
            result.toFile().createNewFile();
        }

        Files.copy(vtrApperTemplate, result, REPLACE_EXISTING);
        oldLineToNewLine(result, "    <RectilinearGrid WholeExtent=\"0 0 0 0 0 0\">",
                String.format("    <RectilinearGrid WholeExtent=\"0 %d 0 %d 0 %d\">", wholeExtent[0], wholeExtent[1],
                        wholeExtent[2]));

        oldLineToNewLine(result, "        <Piece Extent=\"0 0 0 0 0 0\">",
                String.format("        <Piece Extent=\"0 %d 0 %d 0 %d\">", wholeExtent[0], wholeExtent[1],
                        wholeExtent[2]));


        return result;

    }


    private void createVTR(Long[] wholeExtent, Double[] dataArray, int number,
                           Path apperTemplate, Path lowerTemplate) throws IOException {

        Path vtrFile = Paths.get(directoryForOutPut.toString() + String.format("/part0_%d.vtr", number));
        boolean is_exist = Files.exists(vtrFile);
        if (!is_exist) {
            vtrFile.toFile().createNewFile();
        }

        Files.copy(apperTemplate, vtrFile, REPLACE_EXISTING);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(vtrFile), true))) {
            for (Double d : dataArray) {
                writer.write(d.toString() + " ");
            }
        }

        File vtr = vtrFile.toFile();

        File[] files = {vtr, lowerTemplate.toFile()};

        File finalTemp = Paths.get("FinalTemp").toFile();
        finalTemp.delete();
        finalTemp.createNewFile();

        mergeFiles(files, finalTemp);
        Files.copy(Paths.get("FinalTemp"), vtrFile, REPLACE_EXISTING);


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

    private static void mergeFiles(File[] files, File mergedFile) {

        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(mergedFile, true);
            out = new BufferedWriter(fstream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        for (File f : files) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));

                String aLine;
                while ((aLine = in.readLine()) != null) {
                    out.write(aLine);
                    out.newLine();
                }

                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
