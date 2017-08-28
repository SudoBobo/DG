import org.jblas.DoubleMatrix;

public class Triangle {
    public long number;
    public int numberInRectangle;

    private DoubleMatrix A;
    private DoubleMatrix B;

    private DoubleMatrix AAbs;
    private DoubleMatrix AStr;
    private DoubleMatrix BStr;

    private double S[];
    private double jacobian;

    private DoubleMatrix Mkl, Fkl, KKsi, KMu;
    private DoubleMatrix Fkl_j[];

    private DoubleMatrix T[];
    private DoubleMatrix TInv[];

    private DoubleMatrix An;

    private Triangle uNeib[];

    // static lambda spatialBasis []

    // (as mentioned in the article) u_p_l
    // p - stands for index of variable (sigma x, sigma y, etc)
    // l - stands for index of time-dependent coefficient
    // expected to be p x l matrics (p - rows, l - columns)

    public DoubleMatrix u;

    public Triangle() {
    }

    public void init(long number, int numberInRectangle,
                             DoubleMatrix a, DoubleMatrix b,
                             DoubleMatrix AAbs, DoubleMatrix AStr,
                             DoubleMatrix BStr, double[] s,
                             double jacobian, DoubleMatrix mkl,
                             DoubleMatrix fkl, DoubleMatrix KKsi,
                             DoubleMatrix KMu, DoubleMatrix[] fkl_j,
                             DoubleMatrix[] t, DoubleMatrix[] TInv,
                             DoubleMatrix u, DoubleMatrix An) {
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
        this.u = u;
        this.An = An;
    }

    public void setNeighbors(Triangle[] uNeib) {
        this.uNeib = uNeib;
    }


    public double rowSum(int rowIndex) {
        // TODO fix when situation with basis functions will be clear
        return u.get(rowIndex, 0);
    }


    public double jacobian() {
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

    public Triangle uNeib(int j) {

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

    public DoubleMatrix An() {

        if (An == null) {
            // create and save An
            assert false;
        }

        return An;
    }

    public DoubleMatrix B() {
        return B;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public void setNumberInRectangle(int numberInRectangle) {
        this.numberInRectangle = numberInRectangle;
    }

    public void setAStr(DoubleMatrix AStr) {
        this.AStr = AStr;
    }

    public void setBStr(DoubleMatrix BStr) {
        this.BStr = BStr;
    }

    public void setS(double[] s) {
        S = s;
    }

    public void setJacobian(double jacobian) {
        this.jacobian = jacobian;
    }

    public void setMkl(DoubleMatrix mkl) {
        Mkl = mkl;
    }

    public void setFkl(DoubleMatrix fkl) {
        Fkl = fkl;
    }

    public void setKKsi(DoubleMatrix KKsi) {
        this.KKsi = KKsi;
    }

    public void setKMu(DoubleMatrix KMu) {
        this.KMu = KMu;
    }

    public void setFkl_j(DoubleMatrix[] fkl_j) {
        Fkl_j = fkl_j;
    }

    public void setT(DoubleMatrix[] t) {
        T = t;
    }

    public void setTInv(DoubleMatrix[] TInv) {
        this.TInv = TInv;
    }

    public void setuNeib(Triangle[] uNeib) {
        this.uNeib = uNeib;
    }

    public void setU(DoubleMatrix u) {
        this.u = u;
    }

    @Override
    protected Triangle clone() {
            Triangle newTriangle = new Triangle();
            newTriangle.init(number, numberInRectangle, A, B, AAbs,AStr, BStr, S, jacobian, Mkl, Fkl, KKsi, KMu, Fkl_j,
                    T, TInv, u, An);
            newTriangle.setNeighbors(uNeib);

            return newTriangle;
    }
}
