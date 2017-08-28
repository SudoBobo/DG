import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class IndexTest {

    double xMin = 0;
    double xMax = 20;

    double yMin = xMin;
    double yMax = xMax;

    double fine = 5;

    @BeforeClass
    public static void setUpClass() {


    }

    @Test
    public void calcIdxTest(){
        int trNumber = 16;
        int idx = MeshConstructor.calcIdxOnRight(xMin, xMax, fine, trNumber);
        int expected_idx = 30;
        assert(idx == expected_idx);

        trNumber = 46;
        idx = MeshConstructor.calcIdxOnLeft(xMin, xMax,fine, trNumber);
        expected_idx = 32;
        assert(idx == expected_idx);

        trNumber = 11;
        idx = MeshConstructor.calcIdxOnUp(xMin, xMax, yMin, yMax, fine, trNumber);
        expected_idx = 57;
        assert (idx == expected_idx);

        trNumber = 57;
        idx = MeshConstructor.calcIdxOnDown(xMin, xMax, yMin, yMax, fine, trNumber);
        expected_idx = 11;

        System.out.println(idx);
        System.out.println(expected_idx);
    }

    @AfterClass
    public static void tearDown(){
        // освобождение ресурсов

    }

}
