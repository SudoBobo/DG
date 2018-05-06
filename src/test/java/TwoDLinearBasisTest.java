import com.github.sudobobo.basis.Basis;
import org.jblas.DoubleMatrix;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.sudobobo.basis.Linear2DBasis;
import com.github.sudobobo.basis.PreLinear2DBasis;

public class TwoDLinearBasisTest {

    static Basis basis;
    static Basis handCalculatedBasis;

    private static boolean eq(DoubleMatrix f, DoubleMatrix s) {
        // comparing precision
        double p = 0.001;
        assert (f.rows == s.rows);
        assert (f.columns == s.columns);

        for (int r = 0; r < f.rows; r++){
            for (int c = 0; c < f.columns; c++){
                if (Math.abs(f.get(r, c) - s.get(r, c)) > p) {
                    return false;
                }
            }
        }
        return true;
    }

    @BeforeClass
    public static void setUpClass() {
        double spatialStepForNumericalIntegration = 0.001;
        basis = new Linear2DBasis(spatialStepForNumericalIntegration);
        handCalculatedBasis = new PreLinear2DBasis(spatialStepForNumericalIntegration);
    }

    @Test
    public void checkCorrectness() {
        assert (eq(basis.M(), handCalculatedBasis.M()));

        assert (eq(basis.KKsi(), handCalculatedBasis.KKsi()));

        assert (eq(basis.KEta(), handCalculatedBasis.KEta()));

        assert (eq(basis.F0(0), handCalculatedBasis.F0(0)));
        assert (eq(basis.F0(1), handCalculatedBasis.F0(1)));
        assert (eq(basis.F0(2), handCalculatedBasis.F0(2)));

        assert (eq(basis.F(0, 0), handCalculatedBasis.F(0, 0)));
        assert (eq(basis.F(0, 1), handCalculatedBasis.F(0, 1)));
        assert (eq(basis.F(0, 2), handCalculatedBasis.F(0, 2)));

        assert (eq(basis.F(1, 0), handCalculatedBasis.F(1, 0)));
        assert (eq(basis.F(1, 1), handCalculatedBasis.F(1, 1)));
        assert (eq(basis.F(1, 2), handCalculatedBasis.F(1, 2)));

        assert (eq(basis.F(2, 0), handCalculatedBasis.F(2, 0)));
        assert (eq(basis.F(2, 1), handCalculatedBasis.F(2, 1)));
        assert (eq(basis.F(2, 2), handCalculatedBasis.F(2, 2)));
    }

    // a series of test on each usage

    // test for dUmethodReal usage
    // check if basis is correct from mathmatical point of view
    // In this test basis is compared with manually calculated
    @Test
    public void dUmethodRealCheck() {
        System.out.println("kek");
        DoubleMatrix u = DoubleMatrix.ones(5, 3);
        double a = 3;
        DoubleMatrix res = u.mul(a);
        System.out.println(res);
    }

    // test for integration functions - linear and square integrals
    // In this test functions' results are compared with manually calculated

    //

    @AfterClass
    public static void tearDown() {
        // освобождение ресурсов
    }

}
