import com.github.sudobobo.General;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class GeneralResultTest {
    @Test
    public void checkCorrectnes() {

        String[] args = new String[]{"/home/bobo/IdeaProjects/galerkin_1/config.yml"};
        General.main(args);

        String resultPath = "/home/bobo/AData100,000_100,000_with_fine_1,000_and_time_step_0,250/part0_1.vtr";
        String rightResultPath = "/home/bobo/DG_for_test/part0_1.vtr";

        Boolean isTwoEqual = null;

        File resultFile = new File(resultPath);
        File rightResultFile = new File(rightResultPath);
        try {
            isTwoEqual = FileUtils.contentEquals(resultFile, rightResultFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileUtils.deleteDirectory(new File("/home/bobo/AData100,000_100,000_with_fine_1,000_and_time_step_0,250"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert isTwoEqual;
    }
}

