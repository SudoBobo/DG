//import com.github.sudobobo.RKSolver;
//import com.github.sudobobo.basis.Basis;
//import com.github.sudobobo.calculations.Value;
//import com.github.sudobobo.dUmethod;
//import com.github.sudobobo.geometry.Triangle;
//import org.jblas.DoubleMatrix;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//public class rkTest {
//    static RKSolver s;
//
//    @BeforeClass
//    public static void setUpClass() {
//
//        int numberOfTriangles = 1;
//
//        // only one field of the table is used
//        dUmethod du_method = new dUmethod() {
//            @Override
//            public DoubleMatrix calcDU(DoubleMatrix u, Triangle t, Basis basis) {
//                // ignore triangle and basis
//                // take u[0][0] and do simple stuff
//                DoubleMatrix res = DoubleMatrix.zeros(1, 1);
//                res.put(0, 0, -u.get(0,0));
//                return res;
//            }
//        };
//
//        s = new RKSolver(du_method, numberOfTriangles);
//    }
//
//    @Test
//    public void checkCorrectnes() {
//        Value[] v = new Value[1];
//        DoubleMatrix u = DoubleMatrix.zeros(1, 1);
//        double initialValue = 1.0;
//        u.put(0, 0, initialValue);
//        Triangle t = null;
//        v[0] = new Value(u, t);
//
//        Value[] bf = new Value[1];
//        DoubleMatrix bu = DoubleMatrix.zeros(1, 1);
//        bu.put(0, 0, initialValue);
//        Triangle bt = null;
//        bf[0] = new Value(bu, bt);
//
//        Basis basis = null;
//
//        // analytical solution - analytical 'u'
//        double au = 0;
//        double x = 0;
//        double mu = 1;
//
//        System.out.println("x        u        an_u     diff");
//        double spatialStep = 0.1;
//        while(x < (1)) {
//            x = x + spatialStep;
//            au = Math.exp(-x);
//            mu = manualRK(mu, spatialStep);
//            s.solveOneStep(v, bf, spatialStep, basis);
//            System.out.println(String.format("%f %f %f %f %f", x, u.get(0,0), au, u.get(0, 0) - au, mu));
//
//        }
////        System.out.println(String.format("%f %f %f %f %f", x, u.get(0,0), au, u.get(0, 0) - au, mu));
//        System.out.println("x        u        an_u     diff     man_u");
//
//        assert (true);
//    }
//
//    private static double manualRK(double u, double dt) {
//        double k1, k2, k3, k4;
//        k1 = dt * -(u);
//        k2 = dt * -(u + k1/2.0);
//        k3 = dt * -(u + k2/2.0);
//        k4 = dt * -(u + k3);
//        return u + (k1 + 2.0 * k2 + 2.0 * k3 + k4) / 6.0;
//    }
//
//    @AfterClass
//    public static void tearDown() {
//        // освобождение ресурсов
//    }
//
//}
