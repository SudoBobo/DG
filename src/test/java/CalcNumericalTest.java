import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.basis.Function;
import com.github.sudobobo.basis.PreLinear2DBasis;
import org.jblas.DoubleMatrix;
import org.junit.Test;

public class CalcNumericalTest {

    private double squareIntegral(Function f1, Function f2, double integrationStep) {
        double dl = integrationStep;
        double numericalValue = 0;

        double[] x = new double[]{0, 0};
        for (x[0] = 0; x[0] < 1; x[0] += dl) {
            for (x[1] = 0; x[1] < (1 - x[0]); x[1] += dl) {
                numericalValue += f1.getValue(x) * f2.getValue(x);
            }
        }
        numericalValue *= dl * dl;

        if (Math.abs(numericalValue) < integrationStep) {
            numericalValue = 0.0f;
        }

        return numericalValue;
    }


    @Test
    public void testCalc(){

        Basis b = new PreLinear2DBasis(0.001);

        DoubleMatrix uCoeff = new DoubleMatrix(new double[][]{
                {-0.000000, -0.000000, 0.000000},
                {0.000000, 0.000000, -0.000000},
                {0.039630, 0.357480, -0.156804},
                {0.000000, -0.000000, 0.000000},
                {0.039630, 0.357480, -0.156804}
        });

//        Triangle t = Triangle.testTriangle();
//        double[] uNumerical = b.calcUNumerical(uCoeff, t);

        int rows = 5;
        int columns = 3;
//
//        DoubleMatrix u = new DoubleMatrix(rows, columns);
//
//        for (int numberOfCoeff = 0; numberOfCoeff < columns; numberOfCoeff++) {
//
//            double upperIntegral = squareIntegral(initialConditionPhaseInInnerSystem, basisFunctions[numberOfCoeff], integrationStep);
//            double downIntegral = M.get(numberOfCoeff, numberOfCoeff);
//
//            DoubleMatrix column = initialConditionAmplitude.mul(upperIntegral / downIntegral);
//            u.putColumn(numberOfCoeff, column);
//        }
    }

}
