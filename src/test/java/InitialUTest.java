import org.jblas.DoubleMatrix;
import org.junit.Test;

public class InitialUTest {


    @Test
    public void checkCorrectnes() {

        DoubleMatrix m = new DoubleMatrix (new double[]{1.0, 0.0, 0.0});
        System.out.println(m.columns);
        System.out.println(m.rows);
    }

}
