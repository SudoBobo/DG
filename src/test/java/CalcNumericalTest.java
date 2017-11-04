import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.basis.Linear2DBasis;
import org.jblas.DoubleMatrix;
import org.junit.Test;

public class CalcNumericalTest {

    @Test
    public void testCalc(){

        Basis basis = new Linear2DBasis(0.01);

        DoubleMatrix uCoeff = new DoubleMatrix(new double[][]{
                {-0.000000, -0.000000, 0.000000},
                {0.000000, 0.000000, -0.000000},
                {0.039630, 0.357480, -0.156804},
                {0.000000, -0.000000, 0.000000},
                {0.039630, 0.357480, -0.156804}
        });

//        double[] uNumerical = basis.calcUNumer/ical(uCoeff)
    }

}
