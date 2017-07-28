package main.java;

import org.jblas.DoubleMatrix;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sin;
import static java.lang.Math.sqrt;


public class MeshConstructor {
    public static Mesh constructHomoMesh(double lambda, double mu, double rho,
                                         int xMin, int xMax, int yMin, int yMax, int fine){
        assert xMax - xMin == yMax - yMin;

        int sideLength = xMax - xMin;
        List<Triangle> triangles = new ArrayList<Triangle>();

        // x, y - координаты левой нижней вершины текущего прямоугольника
        // вертикальный "ход"
        double nX = 1;
        double nY = 1;

        double cP = sqrt((lambda + 2 * mu) / rho);
        double cS = sqrt(mu / rho);


        DoubleMatrix R2 = new DoubleMatrix(new double[]{
                -2.0 * mu * nX * nY,
                2.0 * mu * nX * nY,
                mu * (nX * nX - nY * nY),
                -nY * cS,
                nX * cS

        });

        DoubleMatrix R5 = new DoubleMatrix(new double[]{
                lambda + 2.0 * mu * nX * nX,
                lambda + 2.0 * mu * nY * nY,
                2.0 * mu  * nX * nY,
                -nX * cP,
                -nY * cP
        });

        DoubleMatrix k = new DoubleMatrix(new double[] {
                2.0 * Math.PI / 25.0, 2.0 * Math.PI / 25.0
        });

        assert cP == 2;
        assert cS == 1;

        int i = 0;

        for (int y = yMin; y < yMax; y += fine){
            // горизонтальный "ход"
            for (int x = xMin; x < xMax; x += fine){
                // в каждом квадрате по четыре треугольника
                // заполняем каждый в соответствии с ФИЗИЧЕСКОЙ координатой
                for (int numberOfTriangle = 0; numberOfTriangle < 4; numberOfTriangle++){

                    double centerX = 0;
                    double centerY = 0;

                    switch (numberOfTriangle) {
                        case 0:

                            centerX = (x + x + x + fine/2.0) / 3.0;
                            centerY = (y + y + fine + y + fine /2.0) / 3.0;
                            break;
                        case 1:
                            centerX = (x + x + fine/2 + x + fine) / 3.0;
                            centerY = (y+fine + y+fine + y+fine/2.0);
                            break;

                        case 2:
                            centerX = (x+fine + x+fine/2.0 + x+fine) / 3.0;
                            centerY = (y+fine + y + y+fine/2.0) / 3.0;
                            break;

                        case 3:

                            centerX = (x + x+fine + x+fine/2.0) / 3.0;
                            centerY = (y + y + y+fine/2.0);
                            break;
                }

                    DoubleMatrix centerVector = new DoubleMatrix(new double[]{
                            centerX, centerY
                    });

                    DoubleMatrix u = R2.mmul(sin(k.dot(centerVector))).add(
                            R5.mmul(sin(k.dot(centerVector))));

                    Triangle triangle = new Triangle(u);
                    triangles.add(triangle);

                }
            }
        }


        // необходимо повторить обход, чтобы заполнить

        return new Mesh(triangles);
    }
}
