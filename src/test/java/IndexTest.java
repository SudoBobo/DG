import org.jblas.DoubleMatrix;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.github.sudobobo.MeshConstructor.*;
import static java.lang.Math.pow;

public class IndexTest {

    private static double xMin = 0;
    private static double xMax = 100;

    private static double yMin = xMin;
    private static double yMax = xMax;

    private static double fine = 25;
    private static int number_of_ceils = (int) (pow(((xMax - xMin) / fine), 2)) * 4;


    @BeforeClass
    public static void setUpClass() {


        DoubleMatrix[] testMesh = new DoubleMatrix[number_of_ceils];

        double xWidth = xMax / 2;
        double yWidth = yMax;

        double initXCenter = 0;
        double initYCenter = 0;

        int amplitude = 1;
        DoubleMatrix R2 = new DoubleMatrix(new double[]{1.0, 0.0, 0.0, 0.0, 0.0});

        int triangleNumber = 0;
        for (double y = yMin; y < yMax; y += fine) {
            // горизонтальный "ход"
            for (double x = xMin; x < xMax; x += fine) {
                for (int numberInRectangle = 0; numberInRectangle < 4; numberInRectangle++) {

                    double centerX = calcCenterX(numberInRectangle, x, fine);
                    double centerY = calcCenterY(numberInRectangle, y, fine);

                    System.out.println(Integer.toString(triangleNumber) + ' ' + centerX + ' ' +  centerY);
                    //+ ' ' + centerY);
                    testMesh[triangleNumber] = calcInitialUStep(centerX, centerY, xWidth, yWidth, amplitude, R2, initXCenter, initYCenter);

                    triangleNumber++;

                }
            }
        }
        System.out.println(triangleNumber);

    }

    @Test
    public void calcIdxTest() {

        System.out.println('1');


        System.out.println('2');
//        int trNumber = 16;
//        int idx = MeshConstructor.calcIdxOnRight(xMin, xMax, fine, trNumber);
//        int expected_idx = 30;
//        assert (idx == expected_idx);
//
//        trNumber = 46;
//        idx = MeshConstructor.calcIdxOnLeft(xMin, xMax, fine, trNumber);
//        expected_idx = 32;
//        assert (idx == expected_idx);
//
//        trNumber = 11;
//        idx = MeshConstructor.calcIdxOnUp(xMin, xMax, yMin, yMax, fine, trNumber);
//        expected_idx = 57;
//        assert (idx == expected_idx);
//
//        trNumber = 57;
//        idx = MeshConstructor.calcIdxOnDown(xMin, xMax, yMin, yMax, fine, trNumber);
//        expected_idx = 11;
//        assert (idx == expected_idx);
//
//        trNumber = 998001;
//        idx = MeshConstructor.calcIdxOfTriangleUp(xMin, xMax, yMin, yMax, fine, trNumber);
//        expected_idx = 59;
//
//        System.out.println(idx);
//        System.out.println(expected_idx);

    }


    @AfterClass
    public static void tearDown() {
        // освобождение ресурсов
    }

}
