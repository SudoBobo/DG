import org.jblas.DoubleMatrix;

public class Triangle {
    public  long number;
    public  int numberInRectangle;

    private final DoubleMatrix A;
    private final DoubleMatrix B;

    private final DoubleMatrix AAbs;
    private DoubleMatrix AStr;
    private DoubleMatrix BStr;

    private double S [];
    private double jacobian;

    private DoubleMatrix Mkl, Fkl, KKsi, KMu;
    private DoubleMatrix Fkl_j [];

    private DoubleMatrix T [];
    private DoubleMatrix TInv[];

    private DoubleMatrix uNeib [];

    // static lambda spatialBasis []

    // (as mentioned in the article) u_p_l
    // p - stands for index of variable (sigma x, sigma y, etc)
    // l - stands for index of time-dependent coefficient
    // expected to be p x l matrics (p - rows, l - columns)

    public DoubleMatrix u;

    public Triangle(long number, int numberInRectangle,
                    DoubleMatrix a, DoubleMatrix b,
                    DoubleMatrix AAbs, DoubleMatrix AStr,
                    DoubleMatrix BStr, double[] s,
                    double jacobian, DoubleMatrix mkl,
                    DoubleMatrix fkl, DoubleMatrix KKsi,
                    DoubleMatrix KMu, DoubleMatrix[] fkl_j,
                    DoubleMatrix[] t, DoubleMatrix[] TInv,
                    DoubleMatrix[] uNeib, DoubleMatrix u) {
        this.number = number;
        this.numberInRectangle = numberInRectangle;
        A = a;
        B = b;
        this.AAbs = AAbs;
        this.AStr = AStr;
        this.BStr = BStr;
        S = s;
        this.jacobian = jacobian;
        Mkl = mkl;
        Fkl = fkl;
        this.KKsi = KKsi;
        this.KMu = KMu;
        Fkl_j = fkl_j;
        T = t;
        this.TInv = TInv;
        this.uNeib = uNeib;
        this.u = u;
    }

    public double rowSum(int rowIndex){
        // TODO fix when situation with basis functions will be clear
       return u.get(rowIndex, 0);
    }


    
    public double jacobian(){
        return jacobian;
    }


    // return pre-calculated values

    public DoubleMatrix A() {
        return A;
    }

    public DoubleMatrix AAbs() {
        return AAbs;
    }

    public DoubleMatrix TInv(int j) {
        return TInv[j];
    }

    public DoubleMatrix T(int j) {
        return T[j];
    }

    public double S(int j) {
        return S[j];
    }

    public DoubleMatrix Fkl() {
        return Fkl;
    }

    public DoubleMatrix uNeib(int j) {

        return uNeib[j];
    }

    public DoubleMatrix Fkl(int j) {
        return Fkl_j[j];
    }

    public DoubleMatrix Mkl() {
        return Mkl;
    }

    public DoubleMatrix AStr() {
        return AStr;
    }

    public DoubleMatrix KKsi() {
        return KKsi;
    }

    public DoubleMatrix BStr() {
        return BStr;
    }

    public DoubleMatrix KMu() {
        return KMu;
    }

    public DoubleMatrix B() {
        return B;
    }
}
