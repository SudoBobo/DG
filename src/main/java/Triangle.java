import org.jblas.DoubleMatrix;

public class Triangle {
    public long number;
    public int numberInRectangle;

    public DoubleMatrix u;
    public Triangle (DoubleMatrix u){
        this.u = u;
    }
}
