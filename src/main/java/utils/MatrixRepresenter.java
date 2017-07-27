package main.java.utils;
import org.jblas.DoubleMatrix;

public class MatrixRepresenter {
    public static void print(DoubleMatrix m){
        double [][] a = m.toArray2();
        StringBuilder result = new StringBuilder();

        for (double [] line : a){
            for (double elem : line){
                result.append(elem);
                result.append("     ");
            }
            result.append("\n");
            result.append("\n");
        }

        System.out.println(result.toString());

    }
}
